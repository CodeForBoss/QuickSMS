package com.example.quicksms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


//public class PhoneStateReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        try {
//            System.out.println("Receiver start");
//            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//            Log.e("Incoming Number", "Number is ," + incomingNumber);
//            Log.e("State", "State is ," + state);
//            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
//                Toast.makeText(context,"Incoming Call State",Toast.LENGTH_SHORT).show();
//                Toast.makeText(context,"Ringing State Number is -"+incomingNumber,Toast.LENGTH_SHORT).show();
//
//
//            }
//            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
//                Toast.makeText(context,"Call Received State",Toast.LENGTH_SHORT).show();
//            }
//            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
//                Toast.makeText(context,"Call Idle State",Toast.LENGTH_SHORT).show();
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//}
public class PhoneStateReceiver extends BroadcastReceiver {
    private String quickSMSWindow ="quickSMSWindow";
    @Override
    public void onReceive(final Context context, Intent intent) {
//        SharedPreferences prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
       // SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("MyPref", 0);

        SharedPreferences.Editor editor = pref.edit();
        boolean isWindowOpened = pref.getBoolean(quickSMSWindow, false);
        Intent intent1= new Intent(context,ChatHead.class);
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);


                System.out.println("incomingNumber : "+incomingNumber);
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
       // String state=intent.getStringExtra(TelephonyManager.EXTRA_STATE);

//        if(intent.getAction().equals("android.intent.action.PHONE_STATE")){
//
//            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//
//            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
//                Log.d(TAG, "Inside Extra state off hook");
//                String number = intent.getStringExtra(TelephonyManager.EXTRA_PHONE_NUMBER);
//                Log.e(TAG, "outgoing number : " + number);
//            }
//
//            else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
//                Log.e(TAG, "Inside EXTRA_STATE_RINGING");
//                String number = arg1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                Log.e(TAG, "incoming number : " + number);
//            }
//            else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
//                Log.d(TAG, "Inside EXTRA_STATE_IDLE");
//            }
//        }
        String outgoingPhoneNumber="",incomingPhoneNumber="";
//        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//        if (intent.getAction().equals("android.intent.action.PHONE_STATE"))
//        {
//            incomingPhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//            Log.i("incoming", ""+incomingPhoneNumber);
//            Toast.makeText(context, ""+incomingPhoneNumber, Toast.LENGTH_LONG).show();
//            intent1.putExtra("PhoneNumber",outgoingPhoneNumber);
//        }
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            incomingPhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.i("incoming", ""+incomingPhoneNumber);
          //  Toast.makeText(context, ""+incomingPhoneNumber, Toast.LENGTH_LONG).show();

            outgoingPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            if(incomingPhoneNumber!=""){
                intent1.putExtra("PhoneNumber",incomingPhoneNumber);
                context.startService(intent1);
                Log.d("phoneCallReceiver", "phone picked up");
            }
            else if(outgoingPhoneNumber!=""){
                intent1.putExtra("PhoneNumber",outgoingPhoneNumber);
                context.startService(intent1);
            }

        }
//        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL"))
//        {
//             outgoingPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//            Log.i("outgoing", ""+outgoingPhoneNumber);
//            // Toast.makeText(context, ""+outgoingPhoneNumber, Toast.LENGTH_LONG).show();
//            Toast.makeText(context, "Call Start...!", Toast.LENGTH_SHORT).show();
//            intent1.putExtra("PhoneNumber",outgoingPhoneNumber);
//            context.startService(intent1);
//        }
//        if(TelephonyManager.EXTRA_STATE_IDLE.equals(state))
//        {
//            Log.i("TEST","Outgoing " + outgoingPhoneNumber);
//            Log.i("TEST","Incoming" + incomingPhoneNumber);
//            Toast.makeText(context, "Call Start...!", Toast.LENGTH_SHORT).show();
//           // context.stopService(intent1);
//
//        }

//        if(TelephonyManager.EXTRA_STATE_IDLE.equals(state))
//        {
//            Log.i("TEST","Outgoing " + outgoingPhoneNumber);
//            Log.i("TEST","Incoming" + incomingPhoneNumber);
//            Toast.makeText(context, "Call Start...!", Toast.LENGTH_SHORT).show();
//
//
//        }
    }
}