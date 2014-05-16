/*
 * Copyright (C) 2014 Matt Booth (Kryten2k35).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kryten2k35.otaupdater.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.kryten2k35.otaupdater.R;
import com.kryten2k35.otaupdater.utils.Constants;
import com.kryten2k35.otaupdater.utils.Preferences;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class AboutActivity extends ActionBarActivity implements Constants{
    
    public final String TAG = this.getClass().getSimpleName();
    
    private Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        setTheme(Preferences.getTheme(mContext));   

        super.onCreate(savedInstanceState); 

        setContentView(R.layout.updater_info);
        String openHTML = "<font color='#33b5e5'>";
        String closeHTML = "</font>";
        String newLine = "<br />";
        String creditsText =
                openHTML + "Matt Booth" + closeHTML + " - Anything not mentioned below" + newLine +
                openHTML + "Roman Nurik" + closeHTML + " - Android Asset Studio Framework" + newLine +
                openHTML + "Jeff Gilfelt"+ closeHTML + " - Android Action Bar Style Generator" + newLine + 
                openHTML + "Brad Greco" + closeHTML + " - DirectoryPicker" + newLine +
                openHTML + "Ficeto (AllianceROM)" + closeHTML + " - Shell tools" + newLine +
                openHTML + "StackOverflow" + closeHTML + " - Many, many people";
        TextView creditsTextView = (TextView) findViewById(R.id.info_credits_text);
        creditsTextView.setText(Html.fromHtml(creditsText));

    }

    public void donate(View v){
        
        if(DEBUGGING)
            Log.d(TAG, "donate() = Yay");
        String url = "http://goo.gl/bXkCEd";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
