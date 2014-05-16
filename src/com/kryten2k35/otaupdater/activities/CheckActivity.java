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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.kryten2k35.otaupdater.Connectivity;
import com.kryten2k35.otaupdater.R;
import com.kryten2k35.otaupdater.RemoteUpdateFile;
import com.kryten2k35.otaupdater.UpdaterApplication;
import com.kryten2k35.otaupdater.tasks.LoadUpdateManifest;
import com.kryten2k35.otaupdater.utils.Constants;
import com.kryten2k35.otaupdater.utils.Preferences;
import com.kryten2k35.otaupdater.utils.Utils;

public class CheckActivity extends ActionBarActivity implements Constants{

    public final String TAG = this.getClass().getSimpleName();

    ProgressDialog mLoadingDialog;
    RemoteUpdateFile remoteFileInfo;
    private Connectivity mConnectivity;
    Context mContext;
    
    boolean propExists;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(UPDATE_CHECK_FOREGROUND)){
                if(DEBUGGING)
                    Log.d(TAG, "Receiving " + UPDATE_CHECK_FOREGROUND + " intent");
                if(propExists && mConnectivity.isConnected()){
                	mLoadingDialog.cancel();
                	onFinishLoad();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        setTheme(Preferences.getTheme(mContext));

        super.onCreate(savedInstanceState);
        mConnectivity = new Connectivity(this);
        propExists = Utils.doesPropExist("ro.ota.romname");

        
        // This is the entry point for the app
        // We should check the necessary build.prop values are present and accounted for,
        if(!propExists){
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
        	alert.setCancelable(false);
        	alert.setMessage(R.string.not_supported_message);
        	alert.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					CheckActivity.this.finish();
				}
			});
        	alert.show();
        } else if(!mConnectivity.isConnected()){
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
        	alert.setCancelable(false);
        	alert.setMessage(R.string.connect_before_update);
        	alert.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					CheckActivity.this.finish();
				}
			});
        	alert.show();
        } else {       
	        remoteFileInfo = UpdaterApplication.getRemoteFileInfo();
	        mLoadingDialog = new ProgressDialog(this);
	        mLoadingDialog.setCancelable(false);
	        mLoadingDialog.setIndeterminate(true);
	        mLoadingDialog.setMessage(getString(R.string.checking_for_update));
	
	        mLoadingDialog.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(propExists && mConnectivity.isConnected()){
	        if(DEBUGGING)
	            Log.d(TAG, "onStart() = loading update manifest");       
	        new LoadUpdateManifest(this, true).execute();
	
	        if(DEBUGGING)
	            Log.d(TAG, "onStart() = registering receiver");       
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(UPDATE_CHECK_FOREGROUND);
	        registerReceiver(mReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(propExists){
        	if(DEBUGGING)
            	Log.d(TAG, "onStop() = unregister receiver");
        	unregisterReceiver(mReceiver);
        }
    }

    private void onFinishLoad(){
        if(remoteFileInfo != null){
            if(DEBUGGING)
                Log.d(TAG, "onFinishLoad() = Load finished. Starting MainActivity");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
