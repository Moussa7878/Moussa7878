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
package com.fictivestudios.tafcha.streaming.tests.PublishStreamManagerTranscodeTest;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fictivestudios.tafcha.R;
import com.fictivestudios.tafcha.streaming.tests.TestContent;

import java.util.ArrayList;
import java.util.HashMap;



public class PublishTranscoderForm {

    public PublishTranscoderDelegate delegate;
    public ViewGroup view;
    public Button submitButton;

    public EditText highBitrate;
    public EditText highWidth;
    public EditText highHeight;
    public EditText medBitrate;
    public EditText medWidth;
    public EditText medHeight;
    public EditText lowBitrate;
    public EditText lowWidth;
    public EditText lowHeight;

    public PublishTranscoderForm (ViewGroup view, final PublishTranscoderDelegate delegate) {
        this.view = view;
        this.delegate = delegate;
        this.submitButton = this.view.findViewById(R.id.submit_button);
        this.highBitrate = (EditText)this.view.findViewById(R.id.high_bitrate);
        this.highWidth = (EditText)this.view.findViewById(R.id.high_width);
        this.highHeight = (EditText)this.view.findViewById(R.id.high_height);
        this.medBitrate = (EditText)this.view.findViewById(R.id.med_bitrate);
        this.medWidth = (EditText)this.view.findViewById(R.id.med_width);
        this.medHeight = (EditText)this.view.findViewById(R.id.med_height);
        this.lowBitrate = (EditText)this.view.findViewById(R.id.low_bitrate);
        this.lowWidth = (EditText)this.view.findViewById(R.id.low_width);
        this.lowHeight = (EditText)this.view.findViewById(R.id.low_height);

        final PublishTranscoderForm self = this;
        this.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                self.delegate.onProvisionSubmit(self);
            }
        });

        Integer high_bitrate = TestContent.GetPropertyInt("high_bitrate");
        Integer high_width = TestContent.GetPropertyInt("high_width");
        Integer high_height = TestContent.GetPropertyInt("high_height");
        Integer med_bitrate = TestContent.GetPropertyInt("med_bitrate");
        Integer med_width = TestContent.GetPropertyInt("med_width");
        Integer med_height = TestContent.GetPropertyInt("med_height");
        Integer low_bitrate = TestContent.GetPropertyInt("low_bitrate");
        Integer low_width = TestContent.GetPropertyInt("low_width");
        Integer low_height = TestContent.GetPropertyInt("low_height");

        highBitrate.setText(high_bitrate.toString());
        highWidth.setText(high_width.toString());
        highHeight.setText(high_height.toString());
        medBitrate.setText(med_bitrate.toString());
        medWidth.setText(med_width.toString());
        medHeight.setText(med_height.toString());
        lowBitrate.setText(low_bitrate.toString());
        lowWidth.setText(low_width.toString());
        lowHeight.setText(low_height.toString());
    }

    public ArrayList<Integer> getHighFormValues () {
        ArrayList<Integer> values = new ArrayList<Integer>();
        values.add(Integer.parseInt((highBitrate).getText().toString()));
        values.add(Integer.parseInt((highWidth).getText().toString()));
        values.add(Integer.parseInt((highHeight).getText().toString()));
        return values;
    }

    public ArrayList<Integer> getMediumFormValues () {
        ArrayList<Integer> values = new ArrayList<Integer>();
        values.add(Integer.parseInt((medBitrate).getText().toString()));
        values.add(Integer.parseInt((medWidth).getText().toString()));
        values.add(Integer.parseInt((medHeight).getText().toString()));
        return values;
    }

    public ArrayList<Integer> getLowFormValues () {
        ArrayList<Integer> values = new ArrayList<Integer>();
        values.add(Integer.parseInt((lowBitrate).getText().toString()));
        values.add(Integer.parseInt((lowWidth).getText().toString()));
        values.add(Integer.parseInt((lowHeight).getText().toString()));
        return values;
    }

    public HashMap<String, Integer> getVariantPropertyMap (ArrayList<Integer> values) {
        HashMap<String, Integer> props = new HashMap<>();
        props.put("videoBR", values.get(0));
        props.put("videoWidth", values.get(1));
        props.put("videoHeight", values.get(2));
        return props;
    }

    public HashMap<String, Object> getHighVariant (String streamName, int order) {
        ArrayList<Integer> high = getHighFormValues();
        HashMap<String, Object> variant = new HashMap<>();
        variant.put("name", streamName + "_" + order);
        variant.put("level", order);
        variant.put("properties", getVariantPropertyMap(high));
        return  variant;
    }

    public HashMap<String, Object> getMediumVariant (String streamName, int order) {
        ArrayList<Integer> high = getMediumFormValues();
        HashMap<String, Object> variant = new HashMap<>();
        variant.put("name", streamName + "_" + order);
        variant.put("level", order);
        variant.put("properties", getVariantPropertyMap(high));
        return  variant;
    }

    public HashMap<String, Object> getLowVariant (String streamName, int order) {
        ArrayList<Integer> high = getLowFormValues();
        HashMap<String, Object> variant = new HashMap<>();
        variant.put("name", streamName + "_" + order);
        variant.put("level", order);
        variant.put("properties", getVariantPropertyMap(high));
        return variant;
    }

    public interface PublishTranscoderDelegate {
        public void onProvisionSubmit(PublishTranscoderForm form);
    }
}
