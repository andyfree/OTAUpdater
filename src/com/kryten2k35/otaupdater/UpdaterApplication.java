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

package com.kryten2k35.otaupdater;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;

import com.kryten2k35.otaupdater.utils.Constants;

public class UpdaterApplication extends Application implements Constants {
    
    private static Context mContext;
    
    private static RemoteUpdateFile remoteFileInfo = new RemoteUpdateFile();   
    public static int DOWNLOAD_STATE = DOWNLOAD_NOT_STARTED;
    public static ArrayList<DownloadsCategory> downloadsCategories = null;

    public UpdaterApplication() {
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
    
    public static RemoteUpdateFile getRemoteFileInfo(){
        return remoteFileInfo;
    }  
}