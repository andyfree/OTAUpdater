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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kryten2k35.otaupdater.UpdaterApplication;
import com.kryten2k35.otaupdater.utils.Preferences;
import com.kryten2k35.otaupdater.utils.Utils;

public class BootReceiver extends BroadcastReceiver {
    
    public final String TAG = this.getClass().getSimpleName();

    Context mContext = UpdaterApplication.getContext();
    
    @Override
    public void onReceive(Context context, Intent intent) {       
        if(Preferences.getBackgroundService(mContext)){
           Utils.setBackgroundCheck(UpdaterApplication.getContext(), true);
        }
    }
}
