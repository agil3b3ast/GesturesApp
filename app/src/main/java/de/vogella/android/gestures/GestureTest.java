package de.vogella.android.gestures;

/**
 * Created by Alessandro on 29/12/2015.
 */

import java.lang.annotation.Target;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.android.gesture.builder.R;

public class GestureTest extends Activity implements OnGesturePerformedListener {
    private GestureLibrary gestureLib;


    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.main, null);
        gestureOverlayView.addView(inflate);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureLib = GestureLibraries.fromFile(Environment.getExternalStorageDirectory().getPath() + "/gestures");
        //gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLib.load()) {
            finish();
        }
        setContentView(gestureOverlayView);
    }

    private class MyPhoneListener extends PhoneStateListener {

        private boolean onCall = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {

                case TelephonyManager.CALL_STATE_RINGING:

                    // phone ringing...

                    Toast.makeText(GestureTest.this, incomingNumber + " calls you",

                            Toast.LENGTH_LONG).show();

                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:

                    // one call exists that is dialing, active, or on hold

                    Toast.makeText(GestureTest.this, "on call...",

                            Toast.LENGTH_LONG).show();

                    //because user answers the incoming call

                    onCall = true;

                    break;

                case TelephonyManager.CALL_STATE_IDLE:

                    // in initialization of the class and at the end of phone call

                    // detect flag from CALL_STATE_OFFHOOK

                    if (onCall == true) {

                        Toast.makeText(GestureTest.this, "restart app after call",

                                Toast.LENGTH_LONG).show();

                        // restart our application

                        Intent restart = getBaseContext().getPackageManager().

                        getLaunchIntentForPackage(getBaseContext().getPackageName());

                        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(restart);
                        onCall = false;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        for (Prediction prediction : predictions) {
            if (prediction.score > 2.0) {
                String[] separated = null;
                try {
                    separated = prediction.name.split(",");
                }
                catch(Exception e){

                    Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT)
                            .show();

                }
                if(separated[0] != null && separated.length > 1){

                    Toast.makeText(this, "name = " + separated[0] + "number" + separated[1], Toast.LENGTH_SHORT)
                        .show();
                    try{
                        String uri = "tel:"+separated[1];
                        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
                        startActivity(dialIntent);
                    }
                    catch(Exception e){
                        Toast.makeText(getApplicationContext(),"call failed "+ e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(this, "name = " + prediction.name, Toast.LENGTH_SHORT)
                            .show();
                }

            }
        }
    }
}