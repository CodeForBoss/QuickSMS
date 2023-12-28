package com.example.quicksms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Objects;

public class ChatService extends Service {
    String phoneNumber = "";
    String isVisible = "";

    private LinearLayout rootView;
    View view;
    WindowManager windowManager2;

    SmsManager smsManager;
    TextView pName,pAddressButton,emailButton,phoneButton,bName,bAddressButton,
            bEmailButton,bPhoneButton,myNameTextButton;

    @Override
    public void onCreate() {
        super.onCreate();
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.activity_chathead, null);
        view.bringToFront();

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager.LayoutParams appOverlayLayoutParams = new WindowManager.LayoutParams();
        appOverlayLayoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        appOverlayLayoutParams.format = PixelFormat.TRANSLUCENT;
        appOverlayLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        appOverlayLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        appOverlayLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = 0;
        params.y = 0;
        windowManager2 = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager2.addView(view, params);

        rootView = view.findViewById(R.id.layoutRoot);
        smsManager = SmsManager.getDefault();

        LinearLayout pLayout = view.findViewById(R.id.personal_layout_sms);
        LinearLayout bLayout = view.findViewById(R.id.business_layout_sms);
        pLayout.setVisibility(View.VISIBLE);
        bLayout.setVisibility(View.GONE);
        TextView switchButton = view.findViewById(R.id.swipeButton);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pLayout.getVisibility() == View.VISIBLE){
                    pLayout.setVisibility(View.GONE);
                    bLayout.setVisibility(View.VISIBLE);
                } else {
                    bLayout.setVisibility(View.GONE);
                    pLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        pName = view.findViewById(R.id.p_name);
        pAddressButton = view.findViewById(R.id.p_address);
        emailButton = view.findViewById(R.id.p_email);
        phoneButton = view.findViewById(R.id.p_phone);
        bName = view.findViewById(R.id.b_name);
        bAddressButton = view.findViewById(R.id.b_address);
        bEmailButton = view.findViewById(R.id.b_email);
        bPhoneButton = view.findViewById(R.id.b_phone);
        myNameTextButton = view.findViewById(R.id.bp_name);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getPackageName(), 0);
        showForegroundServiceNotification("notificaiton");
        if(intent.hasExtra("PhoneNumber")){
            phoneNumber = intent.getStringExtra("PhoneNumber");
        }
        if(intent.hasExtra("isVisible")){
            isVisible = intent.getStringExtra("isVisible");
        }
        //personal name button click operation
        String pNameText = pref.getString(AppConstant.pNameKey,"");
        pName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    smsManager.sendTextMessage(phoneNumber, null, "Name: "+pNameText, null, null);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //personal home address click operation
        String stateAddress = pref.getString(AppConstant.pStreetAddress,"");
        String addreline2 = pref.getString(AppConstant.pAddressLine,"");
        String city = pref.getString(AppConstant.pCity,"");
        String state = pref.getString(AppConstant.pState,"");
        String country = pref.getString(AppConstant.pCountry,"");
        String zip = pref.getString(AppConstant.pZipCode,"");

        String homeAddressMessage = "Address: "+stateAddress+" "+addreline2+"\n"+"City: "+city+"\n"+"State: "+state+"\n"+"Country: "+country+"\n"+"Zip: "+zip;
        pAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    smsManager.sendTextMessage(phoneNumber, null, homeAddressMessage, null, null);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //personal email button click operation
        String pEmailText = pref.getString(AppConstant.pEmailKey,"");
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    smsManager.sendTextMessage(phoneNumber, null, "Email: "+pEmailText, null, null);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //personal Phone button click listener
        String pPhoneText = pref.getString(AppConstant.pPhoneKey,"");
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    smsManager.sendTextMessage(phoneNumber, null, "Phone: "+pPhoneText, null, null);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //Business name button click operation
        String bNameText = pref.getString(AppConstant.bNameKey,"");
        bName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    smsManager.sendTextMessage(phoneNumber, null, "Business Name: "+bNameText, null, null);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //Business address button click operation
        String bStateAddress = pref.getString(AppConstant.bStreetAddress,"");
        String bAddreline2 = pref.getString(AppConstant.bAddressLine,"");
        String bCity = pref.getString(AppConstant.bCity,"");
        String bState = pref.getString(AppConstant.bState,"");
        //  String bCountry = pref.getString(AppConstant.bCountry,"");
        String bZip = pref.getString(AppConstant.bZipCode,"");

        String bHomeAddressMessage = "Address: "+bStateAddress+" "+bAddreline2+"\n"+"City: "+bCity+"\n"+"State: "+bState+"\n"+"Zip: "+bZip;
        bAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    smsManager.sendTextMessage(phoneNumber, null, bHomeAddressMessage, null, null);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        //Business email button click operation
        String bEmailText = pref.getString(AppConstant.bEmailKey,"");
        bEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    smsManager.sendTextMessage(phoneNumber, null, "Email: "+bEmailText, null, null);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //Business Phone button click listener
        String bPhoneText = pref.getString(AppConstant.bPhoneKey,"");
        bPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    smsManager.sendTextMessage(phoneNumber, null, "Phone: "+bPhoneText, null, null);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        myNameTextButton = view.findViewById(R.id.bp_name);
        myNameTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    smsManager.sendTextMessage(phoneNumber,null,"Name: "+pNameText,null,null);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        if (Objects.equals(isVisible, "false")){
            rootView.setVisibility(View.GONE);
        } else {
            rootView.setVisibility(View.VISIBLE);
        }

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showForegroundServiceNotification(String message){
        String foregroundNotificationTitle = getResources().getString(R.string.app_name);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationManager forgroundNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(forgroundNotificationManager != null) {
            NotificationChannel notificationChannel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(foregroundNotificationTitle, foregroundNotificationTitle, NotificationManager.IMPORTANCE_LOW);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                forgroundNotificationManager.createNotificationChannel(notificationChannel);
            }
            Notification notification = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notification = new NotificationCompat.Builder(this, forgroundNotificationManager.getNotificationChannel(foregroundNotificationTitle).getId())
                        .setContentTitle(foregroundNotificationTitle)
                        .setContentText(message)
                        .setTicker(foregroundNotificationTitle + ": " + message)
                        .setContentIntent(pendingIntent)
                        .setDefaults(0)
                        .setSound(null)
                        .build();
            }
            assert notification != null;
            notification.flags |= Notification.FLAG_NO_CLEAR;

            // start as foreground
            startForeground(1, notification);
        }
    }
}
