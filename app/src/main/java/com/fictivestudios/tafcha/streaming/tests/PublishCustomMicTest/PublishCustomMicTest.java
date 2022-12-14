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
package com.fictivestudios.tafcha.streaming.tests.PublishCustomMicTest;

import com.fictivestudios.tafcha.streaming.tests.PublishTest.PublishTest;
import com.red5pro.streaming.source.R5Microphone;



/**
 * Created by davidHeimann on 12/22/17.
 */

public class PublishCustomMicTest extends PublishTest {

    @Override
    protected void attachMic() {

        R5Microphone mic = new gainWobbleMic();
        publish.attachMic(mic);
    }

    public class gainWobbleMic extends R5Microphone {

        private float gain = 1.0f;
        private int mod = 1;
        private double lastTime = 0;

        @Override
        public void processData(byte[] samples, double streamtimeMill) {

            modifyGain(streamtimeMill - lastTime);
            lastTime = streamtimeMill;

            int s;
            for(int i = 0; i < samples.length; i++){

                 s = (int) (samples[i] * gain);
                 samples[i] = (byte) Math.min(s, 0xff);
            }

            super.processData(samples, streamtimeMill);
        }

        private void modifyGain(double time){
            //causes the gain to increase to double volume and decrease to 0 volume, then back
            gain += mod * (time/2000);
            if( gain >= 2 || gain <= 0 ){
                System.out.println("gain at: " + gain);
                gain = Math.max(2.0f * mod, 0.0f);
                mod *= -1;
            }
        }
    }
}
