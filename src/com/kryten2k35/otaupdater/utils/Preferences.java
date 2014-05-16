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

package com.kryten2k35.otaupdater.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.kryten2k35.otaupdater.R;

public class Preferences implements Constants{
    
    public final String TAG = this.getClass().getSimpleName();
	
    private Preferences() {
    }
    
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
	
	public static int getCurrentTheme(Context context){
		return Integer.parseInt(getPrefs(context).getString(CURRENT_THEME, "2")); // #2 is the Dark Theme
	}
	
	public static int getTheme(Context context)
    {       
        switch(getCurrentTheme(context))
        {
        case 0:
            return R.style.RagnarTheme_Light;
        case 1:
            return R.style.RagnarTheme_Light_Dark_Actionbar;
        case 2:
            return R.style.RagnarTheme_Dark;
        default:
            return R.style.RagnarTheme_Dark;
        }
    }
	
	public static void setTheme(Context context, String value){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(CURRENT_THEME, value);
        editor.commit();
    }
	
	// Updater settings
	public static boolean getUpdateAvailable(Context context){
	    return getPrefs(context).getBoolean(IS_UPDATE_AVAILABLE, false);
	}
	
	public static String getUpdateLastChecked(Context context, String time){
        return getPrefs(context).getString(LAST_CHECKED, time);
    }
	
	public static void setUpdateAvailable(Context context, boolean value){
	    SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(IS_UPDATE_AVAILABLE, value);
        editor.commit();
	}
	
	public static void setUpdateLastChecked(Context context, String time){
	    SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(LAST_CHECKED, time);
        editor.commit();
	}
	
	public static boolean getOpenRecovery(Context context){
	    return getPrefs(context).getBoolean(UPDATER_ORS_TWRP, false);
	}
	
	public static boolean getBackgroundService(Context context){
	    return getPrefs(context).getBoolean(UPDATER_BACK_SERVICE, true);
	}
	
	public static int getBackgroundFrequency(Context context){
	    return Integer.parseInt(getPrefs(context).getString(UPDATER_BACK_FREQ, "259200"));
	}
	
	public static int getUpdateOverNetwork(Context context){
        return Integer.parseInt(getPrefs(context).getString(UPDATER_MOBILE_WIFI, "1"));
    }
	
	public static String getDownloadLocation(Context context){
	    return getPrefs(context).getString(UPDATER_DOWNLOAD_LOC, context.getString(R.string.download_folder));
	}
	
	public static void setDownloadLocation(Context context,String path){
	    SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(UPDATER_DOWNLOAD_LOC, path);
        editor.commit();
	}
}
