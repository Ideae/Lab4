package com.madewithorbit.lab4;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/* Class used to listen for incoming SMS Messages */
public class SMSService extends Service {
    public static boolean isStarted;
    private SMSReceiver smsReceiver = new SMSReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        //mHandler = new Handler();
        //mHandler.postDelayed(r, 100);
        isStarted = true;
        IntentFilter i = new IntentFilter();
        i.setPriority(Integer.MAX_VALUE);
        i.addAction("android.provider.Telephony.SMS_RECEIVED");
        i.addAction("com.madewithorbit.lab4.smsservice.STOP");
        registerReceiver(smsReceiver, i);
        Log.d("zack","reg");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //mHandler.removeCallbacks(r);
        super.onDestroy();

    }

    public class SMSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.madewithorbit.lab4.smsservice.STOP"))
            {
                Log.d("zack","unreg");
                unregisterReceiver(this);
                SMSService.this.stopSelf();
                isStarted = false;
            }
            else if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
            {
                final Bundle bundle = intent.getExtras();
                try {
                    if (bundle != null) {
                        final Object[] pdusObj = (Object[]) bundle.get("pdus");
                        for (int i = 0; i < pdusObj.length; i++) {

                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                            String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                            String senderNum = phoneNumber;
                            String message = currentMessage.getDisplayMessageBody();

                            Log.i("zack", "Receieved from: "+ senderNum + " Message: " + message);

                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, "Receieved from: "+ senderNum + " Message: " + message, duration);
                            toast.show();
                        }
                    }

                } catch (Exception e) {
                    Log.e("SmsReceiver", "Exception smsReceiver" +e);
                }
            }
        }
    }
}