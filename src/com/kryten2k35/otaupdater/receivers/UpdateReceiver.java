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

package com.kryten2k35.otaupdater.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.kryten2k35.otaupdater.R;
import com.kryten2k35.otaupdater.UpdaterApplication;
import com.kryten2k35.otaupdater.RemoteUpdateFile;
import com.kryten2k35.otaupdater.utils.Constants;
import com.kryten2k35.otaupdater.utils.Utils;

public class UpdateReceiver extends BroadcastReceiver implements Constants{
    
    public final String TAG = this.getClass().getSimpleName();

    Context mContext = UpdaterApplication.getContext();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(UPDATE_CHECK_BACKGROUND)){
            RemoteUpdateFile remoteFileInfo = UpdaterApplication.getRemoteFileInfo();

            String currentVersion = Utils.getProp("ro.ota.version");
            currentVersion = currentVersion.replaceAll("[a-zA-Z]", "");
            String manifestVer = remoteFileInfo.getVersion();
            manifestVer = manifestVer.replaceAll("[a-zA-Z]", "");

            if(!currentVersion.equalsIgnoreCase(manifestVer)){

                final String filename = remoteFileInfo.getFilename();
                setupNotification(filename);
            }
        }        
    }

    private void setupNotification(String filename){
        NotificationManager mNotifyManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle(mContext.getString(R.string.update_available))
        .setContentText(filename)
        .setSmallIcon(R.drawable.ic_download_dark);

        mNotifyManager.notify(0, mBuilder.build());
    }

}
