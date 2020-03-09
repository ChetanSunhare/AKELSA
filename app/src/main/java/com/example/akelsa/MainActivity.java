package com.example.akelsa;

import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    TextToSpeech myTTS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTTS=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(myTTS.getEngines().size() == 0)
                {
                    Toast.makeText(MainActivity.this, "There is no Text To Speech Engine on your Device", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    myTTS.setLanguage(Locale.US);
                    speak("Hello! I am Ready");
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);

    }

    private void speak(String s) {
        myTTS.speak(s,TextToSpeech.QUEUE_FLUSH,null,null);
    }
}
