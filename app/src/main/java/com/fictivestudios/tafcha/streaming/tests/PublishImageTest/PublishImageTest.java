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
package com.fictivestudios.tafcha.streaming.tests.PublishImageTest;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.fictivestudios.tafcha.streaming.tests.PublishTest.PublishTest;



/**
 * Created by davidHeimann on 2/10/16.
 */
public class PublishImageTest extends PublishTest {
    ImageView screenShot;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onSubscribeTouch(event);
            }
        });
    }

    private boolean onSubscribeTouch( MotionEvent e ){

        if( e.getAction() == MotionEvent.ACTION_DOWN ){
            if( screenShot != null ){
                ((FrameLayout)preview.getParent()).removeView(screenShot);
            }
            screenShot = new ImageView( preview.getContext() );
            FrameLayout.LayoutParams position = new FrameLayout.LayoutParams( preview.getWidth()/2, preview.getHeight()/2 );
            position.setMargins(preview.getWidth() / 2, preview.getHeight() / 2, 0, 0);
            screenShot.setLayoutParams(position);

            screenShot.setScaleType(ImageView.ScaleType.FIT_CENTER);

            Bitmap streamImage = publish.getStreamImage();
            Matrix manip = new Matrix();
            manip.setRotate(camOrientation);
            streamImage = Bitmap.createBitmap(streamImage, 0, 0, streamImage.getWidth(), streamImage.getHeight(), manip, true );
            manip.setScale( -1f, 1f );
            streamImage = Bitmap.createBitmap(streamImage, 0, 0, streamImage.getWidth(), streamImage.getHeight(), manip, true );

            screenShot.setImageBitmap(streamImage);

            ((FrameLayout)preview.getParent()).addView(screenShot);
        }

        return true;
    }
}
