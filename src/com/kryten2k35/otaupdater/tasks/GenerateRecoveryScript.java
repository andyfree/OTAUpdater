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

package com.kryten2k35.otaupdater.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.kryten2k35.otaupdater.R;
import com.kryten2k35.otaupdater.RemoteUpdateFile;
import com.kryten2k35.otaupdater.UpdaterApplication;
import com.kryten2k35.otaupdater.utils.Preferences;
import com.kryten2k35.otaupdater.utils.Tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateRecoveryScript extends AsyncTask<Void, String, Boolean> {
    
    public final String TAG = this.getClass().getSimpleName();
    
    private Context mContext;
    ProgressDialog mLoadingDialog;
    private StringBuilder mScript = new StringBuilder();
    private static String mScriptFile = "/cache/recovery/openrecoveryscript";
    private static String NEW_LINE = "\n";   
    private String mFilename;;
    private String mScriptOutput;
    
    RemoteUpdateFile remoteFileInfo;
    
    public GenerateRecoveryScript(Context context){
        mContext = context;
        remoteFileInfo = UpdaterApplication.getRemoteFileInfo();
        mFilename = remoteFileInfo.getFilename();
    }

    protected void onPreExecute() {
        // Show dialog
        mLoadingDialog = new ProgressDialog(mContext);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setIndeterminate(true);
        mLoadingDialog.setMessage(mContext.getString(R.string.rebooting));
        mLoadingDialog.show();
        
        String mDownloadLocation = Preferences.getDownloadLocation(mContext);
        // Replace extSdCard for external_sd on TWRP recoveries
        mDownloadLocation = mDownloadLocation.replaceAll("extSdCard", "external_sd");
        mScript.append("wipe data" + NEW_LINE);
        mScript.append("wipe cache" + NEW_LINE);
        mScript.append("wipe dalvik" + NEW_LINE);
        mScript.append("install " + mDownloadLocation +  mFilename);
        mScriptOutput = mScript.toString();
    }
    
    @Override
    protected Boolean doInBackground(Void... params) {
        FileWriter fstream;
        BufferedWriter out;
        String tempFile = Preferences.getDownloadLocation(mContext) + "/openrecoveryscript";
        try {
            File file = new File(tempFile);
            if(!file.exists()){
                file.createNewFile();
            }
            fstream = new FileWriter(tempFile);
            out = new BufferedWriter(fstream);
            out.append(mScriptOutput);
            out.close();
            
            Tools.shell("cp " + tempFile + " " + mScriptFile + " && rm -rf " + tempFile);
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        
    }
    @Override
    protected void onPostExecute(Boolean value) {
        mLoadingDialog.cancel();
        Tools.recovery();
    }
}