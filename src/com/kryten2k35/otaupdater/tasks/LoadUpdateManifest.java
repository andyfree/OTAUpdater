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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.kryten2k35.otaupdater.RemoteUpdateFile;
import com.kryten2k35.otaupdater.utils.Constants;
import com.kryten2k35.otaupdater.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public  class LoadUpdateManifest extends AsyncTask<Void, Boolean, RemoteUpdateFile> implements Constants {
    
    public final String TAG = this.getClass().getSimpleName();
    
    private Context mContext;

    protected static final String MANIFEST = "update_manifest.xml";

    // Did this come from the BackgroundReceiver class?
    boolean shouldUpdateForegroundApp;
    
    public LoadUpdateManifest(Context context, boolean input){
        mContext = context;
        shouldUpdateForegroundApp = input;
    }

    @Override
    protected RemoteUpdateFile doInBackground(Void... v) {

        try {
            InputStream input = null;

            URL url = new URL(Utils.getProp("ro.ota.manifest"));
            URLConnection connection = url.openConnection();
            connection.connect();
            // download the file
            input = new BufferedInputStream(url.openStream());

            OutputStream output = mContext.openFileOutput(
                    MANIFEST, Context.MODE_PRIVATE);

            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            // file finished downloading, parse it!
            UpdateXmlParser parser = new UpdateXmlParser();
            parser.parse(new File(mContext.getFilesDir(), MANIFEST),
                    mContext);
        } catch (Exception e) {
            Log.d(TAG, "Exception!", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(RemoteUpdateFile result) {
        Intent intent;
        if(shouldUpdateForegroundApp){
            intent = new Intent(UPDATE_CHECK_FOREGROUND);
        } else {
            intent = new Intent(UPDATE_CHECK_BACKGROUND);
        }
        mContext.sendBroadcast(intent);
        super.onPostExecute(result);
    }
}