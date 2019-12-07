package com.bluedot.modelctrackpad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.bluedot.modelctrackpad.R;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Activity2 extends AppCompatActivity {
    public static String wifiModuleIp = "raspberrypi";
    static int Ub=0;
    static int Db=0;
    static int Fb=0;
    static int Lb=0;
    static int Rb=0;
    static int Bb=0;
    static String V;
    static String L;
    static String R;
    public static Switch start;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bluedot.modelctrackpad.R.layout.activity_2);
        Button Cockpit = findViewById(com.bluedot.modelctrackpad.R.id.Cockpit);
        Cockpit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });


        ToggleButton up = findViewById(com.bluedot.modelctrackpad.R.id.up);
        ToggleButton down = findViewById(com.bluedot.modelctrackpad.R.id.down);
        ToggleButton forward = findViewById(com.bluedot.modelctrackpad.R.id.forward);
        ToggleButton reverse = findViewById(com.bluedot.modelctrackpad.R.id.reverse);
        ToggleButton left = findViewById(com.bluedot.modelctrackpad.R.id.left);
        ToggleButton right = findViewById(com.bluedot.modelctrackpad.R.id.right);
        start = findViewById(com.bluedot.modelctrackpad.R.id.start);

        if (up.isChecked()){
            Ub = 1;
            Activity2.Socket_AsyncTask cmd_Change_Servo = new Activity2.Socket_AsyncTask();
            cmd_Change_Servo.execute();

        }else{
            Ub = 0;
            Activity2.Socket_AsyncTask cmd_Change_Servo = new Activity2.Socket_AsyncTask();
            cmd_Change_Servo.execute();
        }

        if (down.isChecked()){
            Db = 1;
            Activity2.Socket_AsyncTask cmd_Change_Servo = new Activity2.Socket_AsyncTask();
            cmd_Change_Servo.execute();
        }else{
            Db = 0;
            Activity2.Socket_AsyncTask cmd_Change_Servo = new Activity2.Socket_AsyncTask();
            cmd_Change_Servo.execute();
        }

        if(forward.isChecked()){
            Fb = 1;
            Activity2.Socket_AsyncTask cmd_Change_Servo = new Activity2.Socket_AsyncTask();
            cmd_Change_Servo.execute();
        }else{
            Fb = 0;
            Activity2.Socket_AsyncTask cmd_Change_Servo = new Activity2.Socket_AsyncTask();
            cmd_Change_Servo.execute();
        }

        if (left.isChecked()){
            Lb = 1;
            Activity2.Socket_AsyncTask cmd_Change_Servo = new Activity2.Socket_AsyncTask();
            cmd_Change_Servo.execute();
        }else{
            Lb = 0;
            Activity2.Socket_AsyncTask cmd_Change_Servo = new Activity2.Socket_AsyncTask();
            cmd_Change_Servo.execute();
        }

        if(reverse.isChecked()){
            Bb = 1;
            Activity2.Socket_AsyncTask cmd_Change_Servo = new Activity2.Socket_AsyncTask();
            cmd_Change_Servo.execute();
        }else{
            Bb = 0;
            Activity2.Socket_AsyncTask cmd_Change_Servo = new Activity2.Socket_AsyncTask();
            cmd_Change_Servo.execute();
        }

        if(right.isChecked()){
            Rb = 1;
            Activity2.Socket_AsyncTask cmd_Change_Servo = new Activity2.Socket_AsyncTask();
            cmd_Change_Servo.execute();
        }else{
            Rb = 0;
            Activity2.Socket_AsyncTask cmd_Change_Servo = new Activity2.Socket_AsyncTask();
            cmd_Change_Servo.execute();
        }





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


    }
    public void openMainActivity(){
        Intent intent2 = new Intent(this, MainActivity.class);
        startActivity(intent2);
    }



    public static String makeButtonCommands(){

        if(Ub == 1 & Db==0){
            V = "60";
        }if(Ub == 0 & Db==1){
            V = "-60";
        }else{
            V = "0";
        }

        if(Fb==1 & Rb==0 & Lb==0 & Bb==0){
            L = "60";
            R = "60";
        }else{
            L="0";
            R="0";
        }
        //Right Button:
        if(Fb==0 & Rb==1 & Lb==0 & Bb==0){
            L = "60";
            R = "-60";
        }else{
            L="0";
            R="0";
        }

        //Left Button:
        if(Fb==0 & Rb==0 & Lb==1 & Bb==0){
            L = "-60";
            R = "60";
        }else{
            L="0";
            R="0";
        }

        //Back Button:
        if(Fb==0 & Rb==0 & Lb==0 & Bb==1){
            L = "-60";
            R = "-60";
        }else{
            L="0";
            R="0";
        }

        return L+R+V;
    }

    public static class Socket_AsyncTask extends AsyncTask<Void,Void,Void>
    {
        Socket socket;
        @Override
        protected Void doInBackground(Void... params){
            try{
                InetAddress inetAddress = InetAddress.getByName(com.bluedot.modelctrackpad.MainActivity.wifiModuleIp);
                socket = new Socket(inetAddress, com.bluedot.modelctrackpad.MainActivity.MotorPort);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());



                if (start.isChecked()){
                    dataOutputStream.writeBytes(makeButtonCommands());
                    //dataOutputStream.write(TestInt);
                    dataOutputStream.close();
                    socket.close();
                }else{

                    dataOutputStream.writeBytes("0,0,0,"+makeButtonCommands().split(",")[3]);
                    //dataOutputStream.write(TestInt);
                    dataOutputStream.close();
                    socket.close();
                }

            }catch (UnknownHostException e){e.printStackTrace();}catch (IOException e){e.printStackTrace();}
            return null;
        }
    }

}
