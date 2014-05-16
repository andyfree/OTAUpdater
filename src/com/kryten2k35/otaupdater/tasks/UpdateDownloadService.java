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

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.kryten2k35.otaupdater.R;
import com.kryten2k35.otaupdater.RemoteUpdateFile;
import com.kryten2k35.otaupdater.UpdaterApplication;
import com.kryten2k35.otaupdater.utils.Constants;
import com.kryten2k35.otaupdater.utils.Preferences;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class UpdateDownloadService extends IntentService implements Constants {
    
    public final String TAG = this.getClass().getSimpleName();

    private boolean isCancelled;
    private boolean mIsSuccessful;
    private Context mContext;
    private Builder mBuilder;
    private NotificationManager mNotifyManager;

    RemoteUpdateFile remoteFileInfo;

    public UpdateDownloadService() {
        super("DownloadService");
    }

    @Override
    public void onDestroy (){
        isCancelled =  true;
        Log.d(TAG, "DownloadService destroyed");
        mBuilder.setContentText(getString(R.string.download_cancelled))
        .setProgress(0,0,false)
        .setOngoing(false);
        mNotifyManager.notify(0, mBuilder.build());
        sendBroadcast(new Intent(DOWNLOAD_CANCELLED));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mContext = getApplicationContext();
        if(DEBUGGING)
        Log.d(TAG, "DownloadService started");
        remoteFileInfo = UpdaterApplication.getRemoteFileInfo();
        String filename = remoteFileInfo.getFilename();
        String fileUrl = remoteFileInfo.getDirectUrl();
        
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(filename.replaceAll("/", ""))
        .setContentText(getString(R.string.download_in_progress))
        .setSmallIcon(R.drawable.ic_notification)
        .setOngoing(true);
      
        Intent progressIntent = new Intent(DOWNLOAD_PROGRESS);

        try {
            InputStream input = null;

            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();

            // download the file
            input = new BufferedInputStream(url.openStream());

            String downloadLocation = Preferences.getDownloadLocation(mContext);
            File dir = new File(downloadLocation);
            File file = new File(downloadLocation + filename);
            dir.mkdirs();
            FileOutputStream fOut = new FileOutputStream(file);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            int percentDone = -1;
            
            if(DEBUGGING)
            Log.d(TAG, "DownloadService Downloading...");
            while ((count = input.read(data)) != -1 && !isCancelled) {
                total += count;
                // publishing the progress....
                int progress = (int) Math.round(total * 100 / fileLength);

                fOut.write(data, 0, count);

                if (percentDone != progress) {
                    percentDone = progress;
                    mBuilder.setProgress(100, progress, false);
                    mNotifyManager.notify(0, mBuilder.build());
                }

                progressIntent.putExtra("progress", progress);
                progressIntent.putExtra("total", (int)total);
                sendBroadcast(progressIntent);
            }

            fOut.flush();
            fOut.close();
            input.close();
            mIsSuccessful = true;
        } catch (Exception e) {
            mIsSuccessful = false;
            Log.d(TAG, "Exception!", e);
        }

        if(mIsSuccessful){
            if(isCancelled){
                mBuilder.setContentText(getString(R.string.download_cancelled))
                .setProgress(0,0,false)
                .setOngoing(false);
                mNotifyManager.notify(0, mBuilder.build());
                sendBroadcast(new Intent(DOWNLOAD_CANCELLED));
            } else {
                mBuilder.setContentText(getString(R.string.download_complete))
                .setProgress(0,0,false)
                .setOngoing(false);
                mNotifyManager.notify(0, mBuilder.build());
                sendBroadcast(new Intent(DOWNLOAD_FINISHED));
            }
        }
    }
}
