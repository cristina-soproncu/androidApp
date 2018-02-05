package mape3.project;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    static long loggedTime;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkTTSstatus();

        if (savedInstanceState == null) {
            /* Set loggedTime */
            loggedTime = System.currentTimeMillis();
            ;
        } else {
            /* Set loggedTime */
            loggedTime = savedInstanceState.getLong("loggedTime");
        }
        showActivity();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("loggedTime", loggedTime);
    }

    public void onCenterButtonClick(View view) {
        /* Hide Activity */
        TextView activityView = (TextView) findViewById(R.id.activity_status);
        activityView.setVisibility(View.VISIBLE);

        /* Load details */
        EditText inputName = (EditText) findViewById(R.id.camp_text);
        EditText inputSecond = (EditText) findViewById(R.id.seconds);

        Intent intent = new Intent(getApplicationContext(), FragmentActivity.class);

        intent.putExtra("alarm_second", Integer.parseInt(inputSecond.getText().toString()));
        String message = inputName.getText().toString();

        Toast.makeText(MainActivity.this, "Welcome " + message + "!", Toast.LENGTH_LONG).show();
        intent.putExtra("Nume", message);

        startActivity(intent);
    }

    public void checkTTSstatus(){
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Toast.makeText(getApplicationContext(), "TTS is enabled!" , Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "TTS is disabled!" , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void showActivity() {
        /* Get View */
        TextView activityView = (TextView) findViewById(R.id.activity_status);

        /* Reset */
        activityView.setVisibility(View.GONE);
        activityView.setText("");

        Intent intent = getIntent();
        if (intent.hasExtra("start_time")) {
            if (intent.getLongExtra("start_time", 0) > 0) {
                /* Get User Name */
                String user = intent.getStringExtra("user_name");
                /* Get Start Time */
                long start_time = intent.getLongExtra("start_time", 0);
                /* Calculate activity time */
                long active_time = loggedTime - start_time;
                /* Convert activity time in min */
                long minutes = TimeUnit.MILLISECONDS.toMinutes(active_time);
                /* Calculate activity seconds that can't be convertet in minutes */
                active_time -= TimeUnit.MINUTES.toMillis(minutes);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(active_time);

                /* Display Activity Details */
                activityView.setVisibility(View.VISIBLE);
                activityView.setText("User: " + user + "\nActive Time: " + minutes + "m, " + seconds + "s");
            }
        }
    }
}
