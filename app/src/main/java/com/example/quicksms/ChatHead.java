package com.example.quicksms;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ChatHead extends AccessibilityService {

    private WindowManager windowManager;
    private ImageView chatHead;
    WindowManager windowManager2;
    String phoneNumber;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }



    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        phoneNumber = intent.getStringExtra("PhoneNumber");
        Log.d("newtag", "StartService" + phoneNumber);


        windowManager2 = (WindowManager) getSystemService(WINDOW_SERVICE);


        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_chathead, null);

        Button btn1 = view.findViewById(R.id.btnStartService);
        Button btn2 = view.findViewById(R.id.btnStopService);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("newtag", "Click Chat");

                windowManager2.removeViewImmediate(view);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("newtag", "Click Chat");

                windowManager2.removeViewImmediate(view);
            }
        });


        TextView number_txt = view.findViewById(R.id.incomingNumber);
        number_txt.setText(phoneNumber);



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

//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT
//        );

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        windowManager2.addView(view, params);
        view.bringToFront();

        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) {

            windowManager2.removeViewImmediate(chatHead);
        }
    }
}