package com.bluedot.modelctrackpad;
//recording imports:
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
/* import android.support.design.widget.Snackbar; */
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
//rov functions:

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    Sensor mySensor;
    SensorManager SM;
    public static String wifiModuleIp = "raspberrypi";//"192.168.8.196";
    public static int MotorPort = 21567;
    public static float fx = 0;
    public static float fy = 0;
    /*public static String CMD = "0,0,0";*/
    public static float a = 0;
    public static float b = 0;
    public static float c = 0;
    /*public static float fz = 0;*/
    public static float zeta = 0; //HERE
    public static float L=0;
    public static float R=0;
    public static float V=0;
    public static float ax;
    public static float az;
    public static float ay;
    public static float atot;
    public static double tilt=125;

    //Recording code:
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1000;
    private int mScreenDensity;
    Button btn_action;
    private MediaProjectionManager mProjectionManager;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;
    private MediaRecorder mMediaRecorder;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_PERMISSION_KEY = 1;
    boolean isRecording = false;


    public static String Result;
    public static Switch start;
    public static Switch CamStart;
    public static String Thrust;
    public String[] readings;
    WebView webView;
    ImageView image;
    ImageView imagev;
    ImageView compass;

    public static String Transmit = "GO";

    TextView LeftReadout;
    TextView RightReadout;
    TextView VertReadout;
    TextView CamReadout;
    TextView Arm;
    TextView CamLock;
    TextView VoltageReadout;
    TextView VoltageHeading;
    static String data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //i think tells the phone youre gonna do stuff without the title
        getSupportActionBar().hide(); //hides the title bar
        setContentView(com.bluedot.modelctrackpad.R.layout.activity_main);


        //**RECORDING**//
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        if (!Function.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;

        mMediaRecorder = new MediaRecorder();

        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);


        btn_action = (Button) findViewById(com.bluedot.modelctrackpad.R.id.RecordButton);
        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onToggleScreenShare();

            }
        });
        //**END RECORDING**//



        webView = findViewById(com.bluedot.modelctrackpad.R.id.WebView);
        webView.loadUrl("http://raspberrypi:8000/stream.mjpg");
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        //webView.getSettings().getDisplayZoomControls();
        //webView.setWebViewClient(new WebViewClient()); //?
        webView.getSettings().setBuiltInZoomControls(true);
        //webView.setVerticalScrollBarEnabled(true);
        //TOGGLE USED TO BE HERE
        webView.setWebViewClient(new WebViewClient(){
            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
                webView.loadUrl("file:///android_asset/dontpanic.html");

            }
        });



        //textView = findViewById(com.bluedot.modelctrackpad.R.id.textViewReadout);
        LeftReadout = findViewById(com.bluedot.modelctrackpad.R.id.LeftReadout);
        RightReadout = findViewById(com.bluedot.modelctrackpad.R.id.RightReadout);
        VertReadout = findViewById(com.bluedot.modelctrackpad.R.id.VertReadout);
        CamReadout = findViewById(com.bluedot.modelctrackpad.R.id.CamReadout);
        VoltageReadout = findViewById(com.bluedot.modelctrackpad.R.id.VoltageReadout);
        VoltageReadout.setVisibility(View.INVISIBLE);
        VoltageHeading = findViewById(com.bluedot.modelctrackpad.R.id.VoltageHeading);
        VoltageHeading.setVisibility(View.INVISIBLE);

        Arm = findViewById(com.bluedot.modelctrackpad.R.id.Arm);
        CamLock = findViewById(com.bluedot.modelctrackpad.R.id.CamLock);

        start=findViewById(com.bluedot.modelctrackpad.R.id.switch1);
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        CamStart =findViewById(com.bluedot.modelctrackpad.R.id.CamSwitch);

        //accelerometer sensor:
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //register sensor listener:
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        start = findViewById(com.bluedot.modelctrackpad.R.id.switch1);
        addTouchListener();

        //DataReadout
        //DataRead = findViewById(com.bluedot.modelctrackpad.R.id.textView2);

        //Switching to novice mode:
        Button novice = findViewById(com.bluedot.modelctrackpad.R.id.NoviceButton);
        novice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });

        //COMPASS:
        //compass = findViewById(com.bluedot.modelctrackpad.R.id.compassView);
        compass = findViewById(com.bluedot.modelctrackpad.R.id.compassView);
        data = "none";





    }


    public void openActivity2(){
        Intent intent = new Intent(this, Activity2.class);
        Transmit = "ABORT";
        Activity2.Transmit2 = "GO";
        startActivity(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not in use

    }
    public void rotateCompass(ImageView imageView, String telem){
        //Random r = new Random();
        //float compVal = Float.valueOf(telem)+r.nextFloat()*45;
        if(data.equals("none")){
            imageView.setVisibility(View.INVISIBLE);
        }
        if(!data.equals("none")){
            imageView.setVisibility(View.VISIBLE);
        }
        try{
            List<String> items = Arrays.asList(telem.split("\\s*,\\s*"));
            imageView.setRotation(Float.valueOf(items.get(0)));
        }catch (Exception e){
        }

    }

    public void displayVoltage(TextView textView, String telem){
        //Random r = new Random();
        //float compVal = Float.valueOf(telem)+r.nextFloat()*45;
        List<String> items = Arrays.asList(telem.split("\\s*,\\s*"));
        textView.setText(items.get(1));
        VoltageHeading.setVisibility(View.VISIBLE);
        VoltageReadout.setVisibility(View.VISIBLE);
    }

    public static String makeCommands() {

        if (CamStart.isChecked()){
            atot = (az*az)+(ax*ax)+(ay*ay);
            tilt =  Math.round(Math.acos((az/Math.sqrt(atot)))*180/3.14)+35;
        }

        //a = ((fx-180)/180)*60;
        a = Math.round(fx*40);
        //b = ((180-fy)/180)*100;
        b = Math.round(fy*100);
        c = Math.round(zeta*100);
        //b=Vert-100;
        //c=((250-fy)/250)*100;

        if (a>100){
            a=100;
        }
        if (a<-100){
            a=-100;
        }
        if (b>100){
            b=100;
        }
        if (b<-100){
            b=-100;
        }


        L = a+b;
        R = b-a;
        V = c;


        if (L>100f){
            L = 100f;
        }
        if(L<-100f){
            L = -100f;
        }

        if(R>100f){
            R = 100f;
        }
        if(R<-100f){
            R = -100f;
        }

        if(V<-100f){
            V=-100f;
        }

        if(V>100f){
            V=100f;
        }


        //R = Math.round(R/10)*10;
        //V = Math.round(V/10)*10;
        //L,R,V
        Thrust = String.valueOf(L) + "," + String.valueOf(R) + "," + String.valueOf(V);

        Result = Thrust+","+String.valueOf(tilt);


        return Result;
    }
    @Override
    public void onSensorChanged(SensorEvent event){

        ax = event.values[1];
        ay = event.values[0];
        az = event.values[2];
        makeCommands();
        readings = makeCommands().split(",");

        //textView.setText(makeCommands());
        LeftReadout.setText(readings[0]);
        RightReadout.setText(readings[1]);
        VertReadout.setText(readings[2]);
        CamReadout.setText(readings[3]);
        //System.out.println("hi there");


        if (CamStart.isChecked()){
            CamLock.setText("UNLOCK CAMERA");



        }else{
            String LockNotification = "LOCK CAMERA";
            CamLock.setText(LockNotification);
        }

        if (start.isChecked()){
            Arm.setText("STOP MOTORS");


        }else{
            Arm.setText("ARM MOTORS");
        }
        //rotateCompass(compass, data);
        Socket_AsyncTask cmd_Change_Servo = new Socket_AsyncTask();
        cmd_Change_Servo.execute();

        Socket_AsyncTask_Data cmd_DataReadout = new Socket_AsyncTask_Data();
        cmd_DataReadout.execute();
        try{
            rotateCompass(compass, data);
        }catch(Exception e){

        }
        try{
            displayVoltage(VoltageReadout,data);
        }catch(Exception e){

        }

        //DataRead.setText(data);



    }

    public void addTouchListener() {
        image = findViewById(com.bluedot.modelctrackpad.R.id.imageView);
        imagev = findViewById(com.bluedot.modelctrackpad.R.id.VertPad);
        //unifying the droids:



        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int Width = image.getWidth();
                int Height = image.getHeight();

                fx = 0;
                fx = (event.getX()-(Width/2))/(Width/2);
                fy = 0;
                fy = ((Height/2)-event.getY())/(Height/2);
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_UP:
                        fx=0;
                        fy=0;

                }

                Socket_AsyncTask cmd_Change_Servo = new Socket_AsyncTask();
                cmd_Change_Servo.execute();
                readings = makeCommands().split(",");

                //textView.setText(makeCommands());
                LeftReadout.setText(readings[0]);
                RightReadout.setText(readings[1]);
                VertReadout.setText(readings[2]);
                CamReadout.setText(readings[3]);
                //DataRead.setText(data);
                rotateCompass(compass, data);
                return true;
            }
        });

        imagev.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int VHeight = imagev.getHeight();

                zeta = ((VHeight/2)-event.getY())/(VHeight/2);
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_UP:
                        zeta=0;

                }

                Socket_AsyncTask cmd_Change_Servo = new Socket_AsyncTask();
                cmd_Change_Servo.execute();
                readings = makeCommands().split(",");
                //DataRead.setText(data);

                //textView.setText(makeCommands());
                LeftReadout.setText(readings[0]);
                RightReadout.setText(readings[1]);
                VertReadout.setText(readings[2]);
                CamReadout.setText(readings[3]);
                //textView.setText(String.valueOf(zeta));
                return true;
            }
        });
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
                //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                if (start.isChecked() & Transmit=="GO"){
                    //System.out.println(in.readLine());
                    dataOutputStream.writeBytes(makeCommands()+","+"AutoOn");
                    //data = in.readLine();
                    //dataOutputStream.write(TestInt);
                    dataOutputStream.close();
                    socket.close();
                }

                if(Transmit=="GO"){
                    dataOutputStream.writeBytes("0,0,0,"+makeCommands().split(",")[3]+","+"AutoOff");
                    //data = dataInputStream.readUTF();
                    //dataOutputStream.write(TestInt);
                    dataOutputStream.close();
                    System.out.println("hi there1");
                    //System.out.println(dataInputStream.available());
                    socket.close();
                }
                if(Transmit=="ABORT"){
                    socket.close();
                }
            }catch (UnknownHostException e){e.printStackTrace();}catch (IOException e){e.printStackTrace();}
            return null;
        }
    }
    //Data Capture:
    public static class Socket_AsyncTask_Data extends AsyncTask<Void,Void,Void> {
        Socket socketData;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                System.out.println("starting try");
                InetAddress inetAddress2 = InetAddress.getByName(MainActivity.wifiModuleIp);
                socketData = new Socket(inetAddress2, 52849);
                //DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socketData.getInputStream()));
                System.out.println("ReaderTest");
                //System.out.println(in.readLine());
                data = in.readLine();
                System.out.println("ReadTest");
                System.out.println(data);

                //dataOutputStream.write(TestInt);
                socketData.close();
                return null;
            } catch (UnknownHostException e) {
                data = "none";
                e.printStackTrace();
            } catch (IOException e) {
                data = "none";
                e.printStackTrace();
            }

            return null;
        }
    }

    //Recording:


    public void actionBtnReload() {
        if (isRecording) {
            btn_action.setText("Stop Recording");
        } else {
            btn_action.setText("Start Recording");
        }

    }


    public void onToggleScreenShare() {
        if (!isRecording) {
            initRecorder();
            shareScreen();
        } else {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            stopScreenSharing();
        }
    }

    private void shareScreen() {
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
        isRecording = true;
        actionBtnReload();
    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("MainActivity", DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
    }

    private void initRecorder() {
        try {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            String strDate = dateFormat.format(date);

            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //THREE_GPP
            mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/ModelcVideo"+strDate+".mp4");
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mMediaRecorder.setVideoFrameRate(16); // 30
            mMediaRecorder.setVideoEncodingBitRate(3000000);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            actionBtnReload();
            stopScreenSharing();

        }catch(IllegalStateException e){
            e.printStackTrace();
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            stopScreenSharing();

        }
    }



    private void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        destroyMediaProjection();
        isRecording = false;
        actionBtnReload();
    }



    private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i(TAG, "MediaProjection Stopped");
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            isRecording = false;
            actionBtnReload();
            return;
        }
        mMediaProjectionCallback = new MediaProjectionCallback();
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        mMediaProjection.registerCallback(mMediaProjectionCallback, null);
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
        isRecording = true;
        actionBtnReload();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_KEY:
            {
                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    onToggleScreenShare();
                } else {
                    isRecording = false;
                    actionBtnReload();
                }
            }
        }
    }





    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if (isRecording) {
                isRecording = false;
                actionBtnReload();
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            }
            mMediaProjection = null;
            stopScreenSharing();
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyMediaProjection();

    }

    @Override
    public void onBackPressed() {
        if (isRecording) {
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    Log.v(TAG, "Stopping Recording");
                    stopScreenSharing();
                    finish();
                }
            };
        } else {
            finish();
        }

    }
}