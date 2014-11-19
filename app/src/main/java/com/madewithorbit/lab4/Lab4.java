package com.madewithorbit.lab4;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.security.Provider;

/* Controller class to the user interface found under /res/layout/main */
public class Lab4 extends Activity implements View.OnClickListener {
    /** Called when the activity is first created. */
    EditText random;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //registerReceiver(new SMSReceiver(), new IntentFilter(Intent.ACTION_TIME_TICK), null, null);

        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.end).setOnClickListener(this);
        findViewById(R.id.toop).setOnClickListener(this);
        findViewById(R.id.midnight).setOnClickListener(this);
        findViewById(R.id.tester).setOnClickListener(this);
        findViewById(R.id.randomDisplay);
        random = (EditText)findViewById(R.id.randomDisplay);


    }
    public void onClick(View v) {
            v.getId();
            if(v.getId() == R.id.start){
                if (!SMSService.isStarted) {
                    startService(new Intent(Lab4.this, SMSService.class));
                }
            }
            else if(v.getId() == R.id.end){
                Intent i = new Intent("com.madewithorbit.lab4.smsservice.STOP");
                sendBroadcast(i);
            }
            else if(v.getId() == R.id.toop){
                Intent i = new Intent("android.provider.Telephony.SMS_RECEIVED");
                sendBroadcast(i);
            }
            else if(v.getId() == R.id.midnight){
                Intent i = new Intent("com.madewithorbit.lab4.smsservice.PHONEHOME");
                sendBroadcast(i);
            }
        }
    public void OnClickSend(View view)
    {
        String portNumber = ((EditText)findViewById(R.id.portNumber)).getText().toString();
        String message = ((EditText)findViewById(R.id.messageText)).getText().toString();
        //Log.d("zack", portNumber + ":" + message);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(portNumber, null, message, null, null);
    }
}