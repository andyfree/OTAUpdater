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
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kryten2k35.otaupdater.Connectivity;
import com.kryten2k35.otaupdater.R;
import com.kryten2k35.otaupdater.RemoteUpdateFile;
import com.kryten2k35.otaupdater.UpdaterApplication;
import com.kryten2k35.otaupdater.tasks.GenerateRecoveryScript;
import com.kryten2k35.otaupdater.tasks.MD5Check;
import com.kryten2k35.otaupdater.tasks.UpdateDownloadService;
import com.kryten2k35.otaupdater.utils.Constants;
import com.kryten2k35.otaupdater.utils.Preferences;
import com.kryten2k35.otaupdater.utils.Tools;
import com.kryten2k35.otaupdater.utils.Utils;

import java.io.File;

public class AvailableActivity extends ActionBarActivity implements Constants {

    private Context mContext;
    public final String TAG = this.getClass().getSimpleName();

    private ProgressBar progressBar;
    private TextView progressCounter;
    private Button downloadButton;
    private Button md5Button;
    private TextView md5Text;

    private boolean isHttpDownload;

    private RemoteUpdateFile remoteFileInfo;
    private Connectivity mConnectivity;

    private String remoteVersion;
    private String remoteCodename;
    private String remoteChangelog;
    private String remoteMd5;
    private String remoteDirectUrl;
    private String remoteHttpUrl;
    private int remoteFileSize;
    private String remoteFilename;

    private AlertDialog.Builder mAlert;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // MD5 Check
            if(action.equals(MD5_CHECK)){
                if(intent.getBooleanExtra("md5Result", false)){
                    md5Text.setTextColor(getResources().getColor(R.color.holo_green_light));
                } else {
                    md5Text.setTextColor(getResources().getColor(R.color.holo_red_light));
                }
            }
            if(action.equals(DOWNLOAD_PROGRESS)){
                int progress = intent.getIntExtra("progress", 0);
                int total = intent.getIntExtra("total", 0); 
                onDownloadProgressUpdate(progress, total);
            } 
            if(action.equals(DOWNLOAD_CANCELLED)){
                onDownloadCancelled();
            } 
            if(action.equals(DOWNLOAD_FINISHED)){
                onDownloadFinished();
            } 
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        setTheme(Preferences.getTheme(mContext));
        
        super.onCreate(savedInstanceState);          
        setContentView(R.layout.updater_available);
        setupActionBar();

        mConnectivity = new Connectivity(mContext);

        remoteFileInfo = UpdaterApplication.getRemoteFileInfo();
        
        int[] attrs = { R.attr.alertIcon };
        TypedArray ta = this.obtainStyledAttributes(attrs);
        mAlert = new AlertDialog.Builder(this);
        mAlert.setIcon(ta.getDrawable(0));
        mAlert.setPositiveButton(getString(R.string.ok), null);
        mAlert.setTitle("Attention");
        ta.recycle();

        // View finding
        TextView updateName = (TextView) findViewById(R.id.updater_update_available);
        TextView changeLogContent = (TextView) findViewById(R.id.updater_update_changelog_content);
        progressBar = (ProgressBar) findViewById(R.id.update_download_progress_bar);
        progressCounter = (TextView) findViewById(R.id.updater_download_progress_counter);
        downloadButton = (Button) findViewById(R.id.updater_download_update_button);
        md5Button = (Button) findViewById(R.id.updater_check_md5_button);
        md5Text = (TextView) findViewById(R.id.updater_update_md5);

        // Remote file information
        remoteVersion = remoteFileInfo.getVersion();
        remoteCodename = remoteFileInfo.getCodename();
        remoteChangelog = remoteFileInfo.getChangelog();
        remoteMd5 = remoteFileInfo.getMd5();
        remoteDirectUrl = remoteFileInfo.getDirectUrl();
        remoteHttpUrl = remoteFileInfo.getHttpUrl();
        remoteFileSize = remoteFileInfo.getRemoteFileSize();
        remoteFilename = remoteFileInfo.getFilename();

        // Are we using a HTTP download?
        isHttpDownload = !remoteHttpUrl.isEmpty() && remoteDirectUrl.isEmpty();
        
        if(!isHttpDownload){
	        // Filename
	        File file = new File(Preferences.getDownloadLocation(mContext) + remoteFilename);
	        if(DEBUGGING)
	        	Log.d(TAG, "Local file " + file.toString());
	        // Does the file already exist? Did we download it already?
	        if(file.length() == remoteFileSize){
	        	Log.d(TAG, "Local filesize" + file.length() + " and remote filesize " + remoteFileSize);
	            onDownloadFinished();
	        } else {
	            progressCounter.setText(Utils.formatDataFromBytes(remoteFileSize));
	            md5Button.setEnabled(false);
	        }
        } else {
        	progressCounter.setText(getString(R.string.http_download));
        	md5Button.setEnabled(false);
        	UpdaterApplication.DOWNLOAD_STATE = DOWNLOAD_IS_HTTP;
        }
 
        setUpDownloadButton();

        // Setup MD5 checker button
        md5Button.setOnClickListener(new OnClickListener() {          
            @Override
            public void onClick(View v) {
                new MD5Check(AvailableActivity.this).execute(remoteFilename, remoteMd5 );
            }
        });

        // Set the update name
        updateName.setText(remoteFileInfo.getUpdateName());

        // MD5 information
        md5Text.setText(remoteMd5);

        // Changelog
        String[] changeLogStr = remoteChangelog.split(";");
        changeLogContent.setText(Utils.getBulletList(
                remoteCodename + " " + remoteVersion, changeLogStr));
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MD5_CHECK);
        filter.addAction(DOWNLOAD_CANCELLED);
        filter.addAction(DOWNLOAD_FINISHED);
        filter.addAction(DOWNLOAD_PROGRESS);
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        setUpDownloadButton();
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mContext.unregisterReceiver(mReceiver);
    }
    
    private void setupActionBar() {
        if(Build.VERSION.SDK_INT >= 11){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }    }

    @Override
    public void onBackPressed() {
        Intent mIntent= new Intent(this, MainActivity.class);
        startActivity(mIntent);
        finish();
    }

    private void setUpDownloadButton(){
        switch(UpdaterApplication.DOWNLOAD_STATE){
            case DOWNLOAD_NOT_STARTED:
                downloadButton.setText(getString(R.string.download));
                break;
            case DOWNLOAD_HAS_FINISHED:
                downloadButton.setText(getString(R.string.install));
                break;
            case DOWNLOAD_IS_ONGOING:
                downloadButton.setText(getString(R.string.cancel));
                break;
            case DOWNLOAD_IS_HTTP:
            	downloadButton.setText(getString(R.string.open_browser));
            	break;
            default:
                //Shouldn't be here!
                break;           
        }
        
        // Setup download/cancel/flash button
        downloadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateDownloadService.class);
                switch(UpdaterApplication.DOWNLOAD_STATE){
                    
                    case DOWNLOAD_NOT_STARTED:
                        if(mConnectivity.isMobile() && Preferences.getUpdateOverNetwork(mContext) == 0){
                            mAlert.setMessage(mContext.getString(R.string.not_preferred_network));
                            mAlert.show();              
                        } else if(!mConnectivity.isConnected()){
                            mAlert.setMessage(mContext.getString(R.string.connect_before_download));
                            mAlert.show();
                        } else {
                            if(DEBUGGING)
                            Log.d(TAG, "Starting DownloadService");
                            UpdaterApplication.DOWNLOAD_STATE = DOWNLOAD_IS_ONGOING;
                            setUpDownloadButton();
                            startService(intent);
                        }
                        break;
                        
                    case DOWNLOAD_HAS_FINISHED:
                        if(!Preferences.getOpenRecovery(mContext)){
                            Tools.recovery();
                        } else {
                            new GenerateRecoveryScript(mContext).execute();
                        }
                        break;
                        
                    case DOWNLOAD_IS_ONGOING:
                        UpdaterApplication.DOWNLOAD_STATE = DOWNLOAD_IS_ONGOING;
                        if(DEBUGGING)
                        Log.d(TAG, "Stopping DownloadService");
                        stopService(intent);
                        break;
                    case DOWNLOAD_IS_HTTP:
                    	Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    	browserIntent.setData(Uri.parse(remoteHttpUrl));
                    	startActivity(browserIntent);
                    	break;
                    default:
                        //Shouldn't be here!
                        break;           
                }
            }
        });
    }

    private void onDownloadProgressUpdate(int progress, int total) {
        progressBar.setProgress(progress);
        progressCounter.setText(Utils.formatDataFromBytes(total)+"/"+Utils.formatDataFromBytes(remoteFileSize));        
    }

    private void onDownloadFinished() {
        progressCounter.setTextColor(getResources().getColor(R.color.holo_green_light));
        progressCounter.setText(R.string.ready_to_flash);
        downloadButton.setText(getString(R.string.install));
        md5Button.setEnabled(true);
        UpdaterApplication.DOWNLOAD_STATE = DOWNLOAD_HAS_FINISHED;
        setUpDownloadButton();
    }
    
    private void onDownloadCancelled() {
        progressBar.setProgress(0);
        progressCounter.setText(Utils.formatDataFromBytes(remoteFileSize)); 
        UpdaterApplication.DOWNLOAD_STATE = DOWNLOAD_NOT_STARTED;
        setUpDownloadButton();
    }
}
