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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.kryten2k35.otaupdater.utils.Constants;

public class Connectivity implements Constants{

    public final String TAG = this.getClass().getSimpleName();
    
    Context mContext;
    NetworkInfo mInfo;

    public Connectivity(Context context) {
        mContext = context;
        mInfo = getNetworkInfo();
    }
    
    private NetworkInfo getNetworkInfo(){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public boolean isConnected(){
        if(DEBUGGING)
            Log.d(TAG, "Checking Connection = " + mInfo.isConnected());
        return mInfo.isConnected();
    }

    public boolean isMobile(){
        if(DEBUGGING)
            Log.d(TAG, "Checking isMobile = " + (mInfo != null && isConnected() && mInfo.getType() == ConnectivityManager.TYPE_MOBILE));
        return (mInfo != null && isConnected() && mInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }
}
