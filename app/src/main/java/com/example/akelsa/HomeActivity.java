package com.example.akelsa;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    TextToSpeech myTTS;
    SpeechRecognizer mySR;
    TextView tx1,tx2;
    ArrayList myDataSet;
    Python py;
    PyObject module,obj;
    private int PERMISSION_CODE = 1;
   // ListView list1;
    // MyAdapter m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeTextToSpeech();
        initializeSpeechRecognizer();
        tx1=findViewById(R.id.text1);
        tx2=findViewById(R.id.text2);
        myDataSet = new ArrayList();

        if(ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(HomeActivity.this,
                        Manifest.permission.INTERNET)==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(HomeActivity.this,
                        Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "All permissions are granted", Toast.LENGTH_SHORT).show();
        }
        else
        {
            requestPermission();
        }
        //list1 =findViewById(R.id.rc_view);
        //m=new MyAdapter(this,myDataSet);
        //list1.setAdapter(m);
        // starting python
        if(!Python.isStarted())
        {
            Python.start(new AndroidPlatform(this));
        }
        py=Python.getInstance();
        module=py.getModule("myscript");
    }

    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this
        ,Manifest.permission_group.CONTACTS))
        {
            new AlertDialog.Builder(this).setTitle("Permission Needed")
                    .setMessage("This permissions is needed for app")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(HomeActivity.this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE,Manifest.permission.RECORD_AUDIO,Manifest.permission.INTERNET},PERMISSION_CODE);

                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }
        else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE,Manifest.permission.RECORD_AUDIO,Manifest.permission.INTERNET},PERMISSION_CODE);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Permmission Not Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    // Initialization of Text-To-Speech Engine
    public void initializeTextToSpeech() {
        myTTS =new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(myTTS.getEngines().size() == 0)
                {
                    Toast.makeText(HomeActivity.this, "There is no Text To Speech Engine on your Device", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    myTTS.setLanguage(Locale.US);
                }

            }

        });
    }
    // Method for speaking
    private void speak(String s)
    {
        tx2.setText(""+s);
        //myDataSet.add(""+s);
       // m.notifyDataSetChanged();
        myTTS.speak(s,TextToSpeech.QUEUE_FLUSH,null,null);
    }

    // Initialization of Speech Recognizer
    public void initializeSpeechRecognizer()
    {
        if(SpeechRecognizer.isRecognitionAvailable(this))
        {
            mySR =SpeechRecognizer.createSpeechRecognizer(this);
            mySR.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

                }
                @Override
                public void onBeginningOfSpeech() {

                }
                @Override
                public void onRmsChanged(float v) {

                }
                @Override
                public void onBufferReceived(byte[] bytes) {

                }
                @Override
                public void onEndOfSpeech() {

                }
                @Override
                public void onError(int i) {

                }
                @Override
                public void onResults(Bundle bundle) {

                    List<String> result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    try {
                        tx1.setText(""+result.get(0));
                        //myDataSet.add(""+result.get(0));
                        //m.notifyDataSetChanged();
                        processResult(result.get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onPartialResults(Bundle bundle) {
                    //List<String> rs=bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                }
                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });
        }
    }

    // Processing User Command
    private void processResult(String cmd) throws IOException {
        cmd = cmd.toLowerCase();
        try
        {

            if(cmd.contains("open"))
            {
                Intent i = null;
                final PackageManager manager=this.getPackageManager();
                if(cmd.indexOf("gaana") != -1)
                {
                    speak("Opening Gaana App");
                    i = manager.getLaunchIntentForPackage("com.gaana");
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                }
                if(cmd.indexOf("youtube") != -1)
                {
                    speak("Opening Youtube");
                    i = manager.getLaunchIntentForPackage("com.google.android.youtube");
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                }
                if(cmd.indexOf("browser") != -1)
                {
                    speak("Opening browser");
                    i=new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"));
                }
                if(cmd.contains("facebook"))
                {
                    speak("Opening Facebook");
                    i = manager.getLaunchIntentForPackage("com.facebook.katana");
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                }
                if(cmd.contains("file location"))
                {
                    cmd=cmd.replace("open file location ","/");
                    cmd=cmd.replace(" then ","/");
                    tx2.setText(Environment.getExternalStorageDirectory().getPath()+cmd);
                    //Toast.makeText(this, ""+Environment.getExternalStorageDirectory()+cmd, Toast.LENGTH_SHORT).show();
                    Uri selectUri = Uri.parse(Environment.getExternalStorageDirectory().getPath()+cmd);
                    i=new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(selectUri,"resource/folder");
                    startActivity(i);
                    /*
                   Uri selectUri = Uri.parse(Environment.getExternalStorageDirectory()+cmd);
                    i=new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(selectUri,"resource/folder");
                    if(i.resolveActivityInfo(getPackageManager(),0)!=null)
                    {
                        startActivity(i);
                        speak("opening");
                    }
                    else
                        {
                            speak("did not found location");
                        }
                        */
                }
                Thread.sleep(2000);
                startActivity(i);
            }
            else if(cmd.contains("call"))
            {
                try {
                    String name = cmd.replace("call ", "");
                    Toast.makeText(this, "" + name, Toast.LENGTH_SHORT).show();
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                    while (phones.moveToNext()) {
                        if (name.equalsIgnoreCase(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))) {
                            Intent call = new Intent(Intent.ACTION_CALL);
                            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Toast.makeText(this, ""+call, Toast.LENGTH_SHORT).show();
                            if(number.contains("+91"))
                            {
                                call.setData(Uri.parse("tel:" + number));
                            }
                            else if(number.startsWith("0")) {
                                call.setData(Uri.parse("tel:" + number));
                            }
                            else
                            {
                                call.setData(Uri.parse("tel:0" + number));
                            }
                            speak("calling "+name);
                            Thread.sleep(2000);
                            startActivity(call);
                            break;

                        }
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(this, ""+e, Toast.LENGTH_LONG).show();
                }
            }
            else if(cmd.contains("search on google"))
            {
                cmd=cmd.replace("search on google","");
                cmd=cmd.replace(" ","+");
                Toast.makeText(this, ""+cmd, Toast.LENGTH_SHORT).show();
                speak("Searching.....!!");
                try
                {
                    Thread.sleep(1000);
                }
                catch(Exception e){}
                Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com/search?q="+cmd+"&op="+cmd+"&sourceid=chrome-mobile&ie=UTF-8"));
                startActivity(i);
            }
            else if (cmd.contains("what"))
            {
                cmd=cmd.replace("what","");
                if(cmd.indexOf("your name") != -1)
                {
                    speak("My Name is AKELSA");
                }
                else if(cmd.indexOf("are you doing")!=-1)
                {
                    speak("Nothing Special...!! I am just waiting for your commands");
                }
                else if(cmd.indexOf("do for me")!=-1)
                {
                    speak("I can do lots of stuffs like opening applications, making calls and etc.");
                }
                else
                {
                    obj=module.callAttr("searchOnPython",cmd);
                    speak(obj.toString());
                }
            }
            else if(cmd.indexOf("who") != -1)
            {
                if(cmd.contains("created you")||cmd.contains("devloped you")||cmd.contains("yor owner"))
                {
                    speak("Chetan Sunhare, Harshil Joshi and Manoj Choudhary..!!");
                }
                else
                    {
                        obj=module.callAttr("searchOnPython",cmd);
                        speak("According to wikipedia...!!\n"+obj.toString());
                    }
                }
            else if(cmd.contains("exit")||cmd.contains("bye"))
            {
                speak("Okay... Good Bye...!!");
                Thread.sleep(2000);
                finish();
            }
            else
            {
                obj=module.callAttr("searchOnPython",cmd);
                speak(obj.toString());
            }
        }
        catch (Exception e)
        {

        }

    }

    public void listen(View view) {
        //myDataSet.clear();
        //m.notifyDataSetChanged();
        tx2.setText("");
        tx1.setText("");
        Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Ask Something..!!");
        //Toast.makeText(HomeActivity.this, "Start Listening", Toast.LENGTH_SHORT).show();
        mySR.startListening(i);
    }
}