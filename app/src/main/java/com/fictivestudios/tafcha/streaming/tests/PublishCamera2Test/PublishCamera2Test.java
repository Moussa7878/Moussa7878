//
// Copyright © 2015 Infrared5, Inc. All rights reserved.
//
// The accompanying code comprising examples for use solely in conjunction with Red5 Pro (the "Example Code")
// is  licensed  to  you  by  Infrared5  Inc.  in  consideration  of  your  agreement  to  the  following
// license terms  and  conditions.  Access,  use,  modification,  or  redistribution  of  the  accompanying
// code  constitutes your acceptance of the following license terms and conditions.
//
// Permission is hereby granted, free of charge, to you to use the Example Code and associated documentation
// files (collectively, the "Software") without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The Software shall be used solely in conjunction with Red5 Pro. Red5 Pro is licensed under a separate end
// user  license  agreement  (the  "EULA"),  which  must  be  executed  with  Infrared5,  Inc.
// An  example  of  the EULA can be found on our website at: https://account.red5pro.com/assets/LICENSE.txt.
//
// The above copyright notice and this license shall be included in all copies or portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,  INCLUDING  BUT
// NOT  LIMITED  TO  THE  WARRANTIES  OF  MERCHANTABILITY, FITNESS  FOR  A  PARTICULAR  PURPOSE  AND
// NONINFRINGEMENT.   IN  NO  EVENT  SHALL INFRARED5, INC. BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN  AN  ACTION  OF  CONTRACT,  TORT  OR  OTHERWISE,  ARISING  FROM,  OUT  OF  OR  IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.fictivestudios.tafcha.streaming.tests.PublishCamera2Test;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fictivestudios.tafcha.R;
import com.fictivestudios.tafcha.streaming.TestDetailFragment;
import com.fictivestudios.tafcha.streaming.tests.TestContent;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.source.R5Camera2;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;


/**
 * Created by dheimann on 3/24/17.
 */

@TargetApi(22)
public class PublishCamera2Test extends TestDetailFragment implements R5ConnectionListener {
    protected R5VideoView preview;
    protected R5Stream publish;
    private R5Camera2 camera2;
    private CameraDevice camera;
    private CameraCharacteristics camInfo;
    protected int camOrientation;

    protected void showToast(String message) {
        final CharSequence text = message;
        final Context context = getContext();
        final int duration = Toast.LENGTH_SHORT;

        if (getActivity() == null || context == null) {
            return;
        }

        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
        } catch (Exception e) {
            // Most likely have moved away from activity back to main listing on Event.CLOSE.
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.publish_test, container, false);

        preview = (R5VideoView) rootView.findViewById(R.id.videoPreview);

        String b = getActivity().getPackageName();

        //Create the configuration from the values.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("publish_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID(b);

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        publish = new R5Stream(connection);

        publish.audioController.sampleRate = TestContent.GetPropertyInt("sample_rate");

        //show all logging
        publish.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        if (TestContent.GetPropertyBool("audio_on")) {
            //attach a microphone
            R5Microphone mic = new R5Microphone();
            publish.attachMic(mic);
        }

        preview.attachStream(publish);

        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] camList = manager.getCameraIdList();
            for (String id : camList) {
                CameraCharacteristics info = manager.getCameraCharacteristics(id);
                if (info.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    camOrientation = info.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    camInfo = info;
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return null;
                    }
                    manager.openCamera(id, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            if (publish == null)
                                return;
                            startPublish(camera);
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {
                        }

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {
                        }
                    }, null);
                    break;
                }
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    Thread delayThread = null;
    public void startPublish(final CameraDevice device) {
        if(camera == null && delayThread == null){
            delayThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(100);
                    }catch (Exception e){}
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            delayedStartPublish(device);
                        }
                    });
                }
            });
            delayThread.start();
        }
    }

    public void delayedStartPublish(CameraDevice device){
        camera = device;

        camera2 = new R5Camera2(camera, camInfo, TestContent.GetPropertyInt("camera_width"), TestContent.GetPropertyInt("camera_height"));
        camera2.setBitrate(TestContent.GetPropertyInt("bitrate"));
        camera2.setOrientation(camOrientation);
        camera2.setFramerate(TestContent.GetPropertyInt("fps"));

        publish.attachCamera(camera2);

        preview.showDebugView(TestContent.GetPropertyBool("debug_view"));

        publish.setListener(this);
        publish.publish(TestContent.GetPropertyString("stream1"), getPublishRecordType());
    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent event) {
        Log.d("Publisher", ":onConnectionEvent " + event.name());
		String msg = event.message;
		showToast(msg == null ? event.name() : msg);

        if (event.name() == R5ConnectionEvent.START_STREAMING.name()){

        }
    }

    @Override
    public void onStop() {

        if(publish != null){
            publish.stop();

            if(camera != null){
                camera.close();
                camera = null;
            }

            publish = null;
        }

        super.onStop();
    }
}
