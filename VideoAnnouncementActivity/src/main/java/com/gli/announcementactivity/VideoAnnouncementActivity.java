package com.gli.announcementactivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.gli.announcementcontroller.IControllerInterface;
import com.gli.announcementcontroller.IMediaInterface;
import com.gli.announcementcontroller.TrackInfo;

public class VideoAnnouncementActivity extends AppCompatActivity implements
        View.OnTouchListener, ServiceConnection {

    private static final String TAG = VideoAnnouncementActivity.class.getSimpleName();
    private static final int SET_CURRENT_TRACK = 1;
    private static final int GET_TRACK_LIST = 2;
    private IControllerInterface controllerInterface;
    private SurfaceView surfaceView;
    private TrackInfo trackinfo;
    private IMediaInterface mediaInterface = new IMediaInterface.Stub() {
        @Override
        public int getTrackInfoList(TrackInfo info) throws RemoteException {
            Log.d(TAG, "getTrackInfoList: ");
            VideoAnnouncementActivity.this.activityHandler
                    .obtainMessage(GET_TRACK_LIST).sendToTarget();
            info = trackinfo;
            return 0;
        }

        @Override
        public int setCurrentTrack(TrackInfo info) throws RemoteException {
            Log.d(TAG, "setCurrentTrack: ");
            VideoAnnouncementActivity.this.activityHandler
                    .obtainMessage(SET_CURRENT_TRACK,info).sendToTarget();
            return 0;
        }
    };

    private final Handler activityHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_CURRENT_TRACK:
                    Log.d(TAG, "handleMessage: SET_CURRENT_TRACK");
                    trackinfo = (TrackInfo) msg.obj;
                    // Update the media player track
                    Log.d(TAG, "handleMessage: trackinfo"+trackinfo.toString());
                    break;
                case GET_TRACK_LIST:
                    Log.d(TAG, "handleMessage: GET_TRACK_LIST");
                    // send message when list is ready
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: set on touch listener");
        setContentView(R.layout.activity_video_announcement);
        // set onTouchlistener to the surface view
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.setOnTouchListener(this);
        trackinfo = new TrackInfo(10,"ES");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: start the controller service by intent category");
        // Start the controller service by intent category
        Intent serviceIntent = new Intent();
        serviceIntent.setAction("com.gli.controller.START");
        serviceIntent.addCategory("com.gli.controller");

        // bind to the controller service
        if (!super.bindService(serviceIntent,this, BIND_AUTO_CREATE)) {
            Log.e(TAG, "onStart: service BIND failed");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: unbind from the controller service");
        // unbind to the controller service
        super.unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected: set controllerInterface");
        this.controllerInterface = IControllerInterface.Stub.asInterface(service);

        try {
            Log.d(TAG, "onServiceConnected: initialize the controller");
            this.controllerInterface.initialize(mediaInterface);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected: release controllerInterface");
        this.controllerInterface = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();

        if (action ==MotionEvent.ACTION_UP) {
            try {
                Log.d(TAG, "onTouch: UP call controllerInterface");
                controllerInterface.show(5000);
                return true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (action==MotionEvent.ACTION_DOWN) {
            // return true so that we get up action
            return true;
        }
        return false;
    }
}
