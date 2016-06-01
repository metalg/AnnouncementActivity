package com.gli.announcementcontrollerservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.gli.announcementcontroller.IMediaInterface;
import com.gli.announcementcontroller.TrackInfo;

public class ControllerService extends Service implements View.OnClickListener {
    public static final String TAG = ControllerService.class.getSimpleName();
    private static final int SHOW = 1;
    private static final int HIDE = 2;
    private IControllerInterface service;
    private IMediaInterface mediainterface;
    private LayoutInflater inflater;
    private WindowManager windowManager;
    private RelativeLayout controlLayout;
    private WindowManager.LayoutParams layoutParams;
    private Handler serviceHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW:
                    Log.d(TAG, "handleMessage: SHOW");
                    controlLayout.setVisibility(View.VISIBLE);
                    if (msg.arg1>0) {
                        sendEmptyMessageDelayed(HIDE, msg.arg1);
                    }
                    break;
                case HIDE:
                    Log.d(TAG, "handleMessage: HIDE");
                    controlLayout.setVisibility(View.GONE);
                    break;
                default:
            }
        }
    };
    public ControllerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: create IcontrollerInterface");
        this.service = new IControllerInterface();
        // we want the service context to be used.
        prepareControllerView(getApplicationContext());

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: return IcontrollerInterface");
        return this.service;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: release IcontrollerInterface");
        this.service = null;
        if (controlLayout != null) {
            windowManager.removeViewImmediate(controlLayout);
        }
        super.onDestroy();

    }

    public void prepareControllerView(Context context) {

        windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        controlLayout = (RelativeLayout) inflater.inflate(
                R.layout.controller_layout, null);

        
        controlLayout.findViewById(R.id.button).setOnClickListener(this);
        controlLayout.setBackgroundColor(Color.GREEN);
        layoutParams = new WindowManager.LayoutParams(
                100, 100, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        windowManager.addView(controlLayout, layoutParams);

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.button) {
            Log.d(TAG, "onClick: button clicked");
            try {
                mediainterface.setCurrentTrack(new TrackInfo(30,"DE"));
                Log.d(TAG, "onClick: ---->");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public class IControllerInterface extends com.gli.announcementcontroller.IControllerInterface.Stub {

        @Override
        public boolean show(int timeout) throws RemoteException {
            // show the window by this call
            Log.d(TAG, "show: ");
            serviceHandler.obtainMessage(SHOW,timeout,0).sendToTarget();
            return true;
        }

        @Override
        public boolean hide() throws RemoteException {
            // hide the window by this call
            Log.d(TAG, "hide: ");
            serviceHandler.obtainMessage(HIDE).sendToTarget();
            return false;
        }

        @Override
        public int initialize(IMediaInterface mediainterfaceLocal) throws RemoteException {
            // Initialize the window by this call
            Log.d(TAG, "initialize: mediainterface");
            mediainterface = mediainterfaceLocal;
            return 0;
        }
    }
}
