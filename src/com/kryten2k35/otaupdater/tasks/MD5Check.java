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
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.kryten2k35.otaupdater.R;
import com.kryten2k35.otaupdater.utils.Constants;
import com.kryten2k35.otaupdater.utils.Preferences;
import com.kryten2k35.otaupdater.utils.Tools;

public class MD5Check extends AsyncTask<Object, Boolean, Boolean> implements Constants{

    public final String TAG = this.getClass().getSimpleName();
    
    Context mContext;
    ProgressDialog mMD5CheckDialog;

    public MD5Check(Context context){
        mContext = context;
    }
    
    @Override
    protected void onPreExecute(){
        mMD5CheckDialog = new ProgressDialog(mContext);
        mMD5CheckDialog.setCancelable(false);
        mMD5CheckDialog.setIndeterminate(true);
        mMD5CheckDialog.setMessage(mContext.getString(R.string.checking_md5));
        mMD5CheckDialog.show();
    }
    
    @Override
    protected Boolean doInBackground(Object... params) {
        String filename = (String) params[0];
        String md5Remote = (String) params[1];

        String md5Local = Tools.noneRootShell("md5sum " + Preferences.getDownloadLocation(mContext) + filename + " | cut -d ' ' -f 1");
        md5Local = md5Local.trim();
        md5Remote = md5Remote.trim();
        return md5Local.equals(md5Remote);
    }
    
    @Override
    protected void onPostExecute(Boolean result) {
        mMD5CheckDialog.cancel();

        Intent intent = new Intent(MD5_CHECK);
        intent.putExtra("md5Result", result);
        mContext.sendBroadcast(intent);
        
        if(result){
            Toast.makeText(mContext, mContext.getString(R.string.md5_ok), Toast.LENGTH_LONG).show();           
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.md5_failed), Toast.LENGTH_LONG).show();           
        }
        super.onPostExecute(result);
    }
}
