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

import java.security.Provider;

/* Controller class to the user interface found under /res/layout/main */
public class Lab4 extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //registerReceiver(new SMSReceiver(), new IntentFilter(Intent.ACTION_TIME_TICK), null, null);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SMSService.isStarted) {
                    startService(new Intent(Lab4.this, SMSService.class));
                }
            }
        });
        findViewById(R.id.end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.madewithorbit.lab4.smsservice.STOP");
                sendBroadcast(i);
            }
        });
        findViewById(R.id.toop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("android.provider.Telephony.SMS_RECEIVED");
                sendBroadcast(i);
            }
        });
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