package com.bluedot.modelctrackpad;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Activity2 extends AppCompatActivity implements SensorEventListener {
    private Sensor mySensor;
    private SensorManager SM;
    public static String wifiModuleIp = "raspberrypi";
    public static int Ub=0;
    public static int Db=0;
    public static int Fb=0;
    public static int Lb=0;
    public static int Rb=0;
    public static int Bb=0;
    static String V;
    static String L;
    static String R;
    static String C = "125";
    public static float az2;
    public static float ax2;
    public static float ay2;
    public static Switch start;
    public static Switch CamStart;
    static TextView Test;
    public static String Transmit2 = "GO";

    TextView CamLock;
    TextView Arm;
    public static int MotorPort = 21567;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bluedot.modelctrackpad.R.layout.activity_2);
        Button Cockpit = findViewById(com.bluedot.modelctrackpad.R.id.Cockpit);
        Test = findViewById(com.bluedot.modelctrackpad.R.id.TestAsync);

        addTouchListener();
        Cockpit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
        //CamStart = findViewById(com.bluedot.modelctrackpad.R.id.CamSwitch2);
        //start = findViewById(com.bluedot.modelctrackpad.R.id.start2);


        /*down.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (down.isChecked()){
                    Db = 1;
                }else
                {
                    Db = 0;
                }
            }

            });
        up.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (up.isChecked()){
                    Ub = 1;
                }else
                {
                    Ub = 0;
                }
            }

        });
        left.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (left.isChecked()){
                    Db = 1;
                }else
                {
                    Db = 0;
                }
            }

        });

        if (up.isChecked()){
            Ub = 1;

        }else{
            Ub = 0;
        }

        if(forward.isChecked()){
            Fb = 1;
        }else{
            Fb = 0;
        }

        if (left.isChecked()){
            Lb = 1;
        }else{
            Lb = 0;
        }

        if(reverse.isChecked()){
            Bb = 1;
        }else{
            Bb = 0;
        }

        if(right.isChecked()){
            Rb = 1;
        }else{
            Rb = 0;
        }

        );*/


        start = findViewById(com.bluedot.modelctrackpad.R.id.start);


        WebView webView = findViewById(com.bluedot.modelctrackpad.R.id.WebView2);
        webView.loadUrl("http://raspberrypi:8000/stream.mjpg");
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        //webView.getSettings().getDisplayZoomControls();
        //webView.setWebViewClient(new WebViewClient()); //?
        webView.getSettings().setBuiltInZoomControls(true);
        //webView.setVerticalScrollBarEnabled(true);
        //TOGGLE USED TO BE HERE
        webView.setWebViewClient(new WebViewClient());

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        CamStart =findViewById(com.bluedot.modelctrackpad.R.id.CamSwitch);

        //accelerometer sensor:
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //register sensor listener:
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);


    }
    public void openMainActivity(){
        Intent intent2 = new Intent(this, MainActivity.class);
        Transmit2 = "ABORT";
        MainActivity.Transmit = "GO";
        startActivity(intent2);
    }

    public void addTouchListener() {
        Button up = findViewById(com.bluedot.modelctrackpad.R.id.up);
        Button down = findViewById(com.bluedot.modelctrackpad.R.id.down);
        Button forward = findViewById(com.bluedot.modelctrackpad.R.id.forward);
        Button reverse = findViewById(com.bluedot.modelctrackpad.R.id.reverse);
        Button left = findViewById(com.bluedot.modelctrackpad.R.id.left);
        Button right = findViewById(com.bluedot.modelctrackpad.R.id.right);


        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Socket_AsyncTask2 cmd_Change_Servo = new Socket_AsyncTask2();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //do something
                        Test.setText("action down");
                        Ub=1;
                        cmd_Change_Servo.execute();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //something else
                        Test.setText("action up");
                        Ub=0;
                        cmd_Change_Servo.execute();
                        return true;

                }
                return false;
            }
        });
        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Socket_AsyncTask2 cmd_Change_Servo = new Socket_AsyncTask2();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //do something
                        Test.setText("action down");
                        Db=1;
                        cmd_Change_Servo.execute();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //something else
                        Test.setText("action up");
                        Db=0;
                        cmd_Change_Servo.execute();
                        return true;

                }
                return false;
            }
        });
        forward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Socket_AsyncTask2 cmd_Change_Servo = new Socket_AsyncTask2();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //do something
                        Test.setText("action down");
                        Fb=1;
                        cmd_Change_Servo.execute();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //something else
                        Test.setText("action up");
                        Fb=0;
                        cmd_Change_Servo.execute();
                        return true;

                }
                return false;
            }
        });
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Socket_AsyncTask2 cmd_Change_Servo = new Socket_AsyncTask2();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //do something
                        Rb=1;
                        cmd_Change_Servo.execute();
                        Test.setText("action down");
                        return true;
                    case MotionEvent.ACTION_UP:
                        //something else
                        Rb=0;
                        cmd_Change_Servo.execute();
                        Test.setText("action up");
                }
                return false;
            }
        });
        reverse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Socket_AsyncTask2 cmd_Change_Servo = new Socket_AsyncTask2();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //do something
                        Test.setText("action down");
                        Bb=1;
                        cmd_Change_Servo.execute();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //something else
                        Test.setText("action up");
                        Bb=0;
                        cmd_Change_Servo.execute();

                }
                return false;
            }
        });
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Socket_AsyncTask2 cmd_Change_Servo = new Socket_AsyncTask2();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //do something
                        Test.setText("action down");
                        Lb=1;
                        cmd_Change_Servo.execute();

                        return true;
                    case MotionEvent.ACTION_UP:
                        //something else
                        Test.setText("action up");
                        Lb=0;
                        cmd_Change_Servo.execute();

                }
                return false;
            }
        });

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not in use

    }

    @Override
    public void onSensorChanged(SensorEvent event){

        //CamLock = findViewById(com.bluedot.modelctrackpad.R.id.CamText);
        //Arm = findViewById(com.bluedot.modelctrackpad.R.id.ArmText);

        ax2 = event.values[1];
        ay2 = event.values[0];
        az2 = event.values[2];

        Socket_AsyncTask2 cmd_Change_Servo = new Socket_AsyncTask2();
        cmd_Change_Servo.execute();

    }




    public static String makeButtonCommands(){

        float atot2 = (az2*az2)+(ax2*ax2)+(ay2*ay2);
        C =  String.valueOf(Math.round(Math.acos((az2/Math.sqrt(atot2)))*180/3.14)+35);


        if(Ub == 1 & Db==0){
            V = "60";
        }
        if(Ub == 0 & Db==1){
            V = "-60";
        }
        if (Ub==1 & Db==1){
            V = "0";
        }
        if(Ub==0 & Db==0){
            V="0";
        }

        //Forward Button:
        if(Fb==1 & Rb==0 & Lb==0 & Bb==0){
            L = "60";
            R = "60";
        }

        //Right Button:
        if(Fb==0 & Rb==1 & Lb==0 & Bb==0){
            L = "60";
            R = "-60";
        }

        //Left Button:
        if(Fb==0 & Rb==0 & Lb==1 & Bb==0){
            L = "-60";
            R = "60";
        }

        //Back Button:
        if(Fb==0 & Rb==0 & Lb==0 & Bb==1){
            L = "-60";
            R = "-60";
        }
        if(Fb==0 & Rb==0 & Lb==0 & Bb==0){
            L="0";
            R="0";
        }
        return L+","+R+","+V+","+C;
    }

    public static class Socket_AsyncTask2 extends AsyncTask<Void,Void,Void>
    {
        Socket socket;
        @Override
        protected Void doInBackground(Void... params){
            try{
                InetAddress inetAddress = InetAddress.getByName(com.bluedot.modelctrackpad.MainActivity.wifiModuleIp);
                socket = new Socket(inetAddress, com.bluedot.modelctrackpad.Activity2.MotorPort);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                if (Transmit2=="GO"){
                    dataOutputStream.writeBytes(makeButtonCommands());
                    //dataOutputStream.write(TestInt);
                    dataOutputStream.close();
                    socket.close();
                }if(Transmit2=="ABORT"){
                    socket.close();
                }



            }catch (UnknownHostException e){e.printStackTrace();}catch (IOException e){e.printStackTrace();}
            return null;
        }
    }
}
