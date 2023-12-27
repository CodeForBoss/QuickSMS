package com.example.quicksms;

import static androidx.core.content.ContextCompat.startForegroundService;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import androidx.core.app.ServiceCompat;

public class PhoneStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Intent intent1= new Intent(context,ChatService.class);
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                System.out.println("incomingNumber : "+incomingNumber);
            }
        },PhoneStateListener.LISTEN_CALL_STATE);

        String action = intent.getAction();
        if (action != null){
            if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
                intent1.putExtra("isVisible","false");
                intent1.putExtra("PhoneNumber","");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent1);
                } else {
                    context.startService(intent1);
                }
            }
        }

        String outgoingPhoneNumber="",incomingPhoneNumber="";
       // String state=intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    incomingPhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.i("incoming", ""+incomingPhoneNumber);
                    outgoingPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

                if(incomingPhoneNumber!=""){
                    intent1.putExtra("PhoneNumber",incomingPhoneNumber);
                    intent1.putExtra("isVisible","true");
                    Log.d("anisur", "onReceive: incoming "+incomingPhoneNumber);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent1);
                    } else {
                        context.startService(intent1);
                    }
                    Log.d("phoneCallReceiver", "phone picked up");
                } else if(outgoingPhoneNumber!=""){
                    intent1.putExtra("PhoneNumber",outgoingPhoneNumber);
                    intent1.putExtra("isVisible","true");
                    Log.d("anisur", "onReceive: outgoing "+outgoingPhoneNumber);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent1);
                    } else {
                        context.startService(intent1);
                    }
                }
            } else  if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Log.d("anisur", "onReceive: idle called");
                intent1.putExtra("isVisible","false");
                intent1.putExtra("PhoneNumber","");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent1);
                } else {
                    context.startService(intent1);
                }
            }


    }
}