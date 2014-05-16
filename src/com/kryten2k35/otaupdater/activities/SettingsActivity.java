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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.kryten2k35.otaupdater.R;
import com.kryten2k35.otaupdater.utils.Constants;
import com.kryten2k35.otaupdater.utils.DirectoryPicker;
import com.kryten2k35.otaupdater.utils.Preferences;
import com.kryten2k35.otaupdater.utils.Utils;

@SuppressLint("SdCardPath")
@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener, OnSharedPreferenceChangeListener, Constants{

    public final String TAG = this.getClass().getSimpleName();
    
    Context mContext;
    Preference mDownloadLocation;
       
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = this;
        setTheme(Preferences.getTheme(mContext));
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(Constants.PREF_NAME);
        PreferenceManager.setDefaultValues(this, R.xml.settings_updater, false);
        addPreferencesFromResource(R.xml.settings_updater);
        
        mDownloadLocation = (Preference) findPreference(UPDATER_DOWNLOAD_LOC);
        mDownloadLocation.setOnPreferenceClickListener(this);
        mDownloadLocation.setSummary(Preferences.getDownloadLocation(mContext));
        
        if(Build.VERSION.SDK_INT >= 11){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } 
    }
    
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
            
            if(key.equals(CURRENT_THEME)){
                Preferences.setTheme(mContext, listPref.getValue());
            }
        }
         if(key.equals(UPDATER_BACK_FREQ) || key.equals(UPDATER_BACK_SERVICE)){
             Utils.setBackgroundCheck(this, Preferences.getBackgroundService(mContext));
         } 
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // We need this to be custom, as it is a popup for the file/folder selector
        if(preference == mDownloadLocation){
            showChooser();
        }
        return false;
    }

    private void showChooser() {
     Intent intent = new Intent(this, DirectoryPicker.class);
     intent.putExtra(DirectoryPicker.START_DIR, "/storage");
     startActivityForResult(intent, DirectoryPicker.PICK_DIRECTORY);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DirectoryPicker.PICK_DIRECTORY && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            String path = (String) extras.get(DirectoryPicker.CHOSEN_DIRECTORY);
            
            // Make the path recovery clean
            if(path.contains("/storage/emulated/0")){
                path = path.replaceAll("/storage/emulated/0", "/sdcard");
            } else if(path.contains("/storage/sdcard0")){
                path = path.replaceAll("/storage/sdcard0", "/sdcard");
            } else if(path.contains("/storage/emulated/legacy")){
                path = path.replaceAll("/storage/emulated/legacy", "/sdcard");
            } else{
                path = path.replaceAll("/storage/sdcard1", "/extSdCard");
            }
            Preferences.setDownloadLocation(mContext, path);
            mDownloadLocation.setSummary(path);
        }   
    }
}
