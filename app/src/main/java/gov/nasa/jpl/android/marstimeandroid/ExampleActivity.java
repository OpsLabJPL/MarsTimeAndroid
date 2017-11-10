package gov.nasa.jpl.android.marstimeandroid;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import gov.nasa.jpl.android.marstime.MarsTime;

public class ExampleActivity extends AppCompatActivity {

    private static final String EPOCH_FORMAT = "yyyyMMddhh:mm:sszzz";
    private static final double EARTH_SECS_PER_MARS_SEC = 1.027491252;
    private static final float CURIOSITY_WEST_LONGITUDE = 222.6f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        try {
            final Date oppyEpoch = new SimpleDateFormat(EPOCH_FORMAT).parse("2004012415:08:59GMT");
            final TextView earthText = (TextView) findViewById(R.id.earthTimeText);
            final TextView oppyText = (TextView) findViewById(R.id.oppyTimeText);
            final TextView curioText = (TextView) findViewById(R.id.curioTimeText);
            final SimpleDateFormat earthTimeFormat = new SimpleDateFormat("yyyy-DDD'T'hh:mm:ss' UTC'");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Date today = new Date();
                    earthText.setText(earthTimeFormat.format(today));

                    long timeDiff = today.getTime()/1000 - oppyEpoch.getTime()/1000;
                    timeDiff = (long)(timeDiff / EARTH_SECS_PER_MARS_SEC);
                    int sol = (int)(timeDiff / 86400);
                    timeDiff -= sol * 86400;
                    int hour = (int)(timeDiff / 3600);
                    timeDiff -= hour * 3600;
                    int minute = (int)(timeDiff / 60);
                    int seconds = (int)(timeDiff - minute*60);
                    sol += 1; //MER convention of landing day sol 1
                    oppyText.setText(String.format("Sol %03d %02d:%02d:%02d", sol, hour, minute, seconds));

                    Date curiosityTime = new Date();
                    Object[] marsTimes = MarsTime.getMarsTimes(curiosityTime, CURIOSITY_WEST_LONGITUDE);
                    Double msd = (Double) marsTimes[10];
                    Double mtc = (Double) marsTimes[11];
                    sol = (int)(msd - (360-CURIOSITY_WEST_LONGITUDE) / 360) - 49268;
                    double mtcInHours = MarsTime.canonicalValue24(mtc - CURIOSITY_WEST_LONGITUDE*24.0/360.0);
                    hour = (int) mtcInHours;
                    minute = (int) ((mtcInHours-hour)*60.0);
                    seconds = (int) ((mtcInHours-hour)*3600 - minute*60);
                    curioText.setText(String.format("Sol %03d %02d:%02d:%02d", sol, hour, minute, seconds));

                    handler.postDelayed(this, 100);
                }
            }, 100);
        } catch (ParseException e) {
            Log.e("Example", "Parse error for date: "+e);
        }
    }
}
