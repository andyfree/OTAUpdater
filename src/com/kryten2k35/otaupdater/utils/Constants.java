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

public interface Constants {
    
    // Developer
    public static final boolean DEBUGGING = true;
	
	public static final String PREF_NAME = "UpdaterSettings";

	// Theme
	public static final String CURRENT_THEME = "current_theme";
		
	// Updater
	public static final String IS_UPDATE_AVAILABLE = "updater_update_available";
	public static final String LAST_CHECKED = "updater_last_update_check";
	
	// Intents
	public static final String MD5_CHECK = "updater.action.intent.MD5_CHECK";
	public static final String UPDATE_CHECK_FOREGROUND = "updater.action.intent.UPDATE_CHECK_FINISHED";
	public static final String UPDATE_CHECK_BACKGROUND = "updater.action.intent.UPDATE_BACKGROUND";
	public static final String DOWNLOAD_FINISHED = "updater.action.intent.DOWNLOAD_FINISHED";
	public static final String DOWNLOAD_CANCELLED = "updater.action.intent.DOWNLOAD_CANCELLED";
	public static final String DOWNLOAD_PROGRESS = "updater.action.intent.DOWNLOAD_PROGRESS";
	public static final String DOWNLOAD_STARTING = "updater.action.intent.DOWNLOAD_STARTING";
	
	public final static int DOWNLOAD_IS_ONGOING = 0;
	public final static int DOWNLOAD_NOT_STARTED = 1;
	public final static int DOWNLOAD_HAS_FINISHED = 2;
	public final static int DOWNLOAD_IS_HTTP = 3;
	
	// Updater Preferences
	public static final String UPDATER_ORS_TWRP = "updater_twrp_ors";
	public static final String UPDATER_BACK_SERVICE = "updater_background_service";
	public static final String UPDATER_BACK_FREQ = "updater_background_frequency";
	public static final String UPDATER_DOWNLOAD_LOC = "updater_download_location";
	public static final String UPDATER_MOBILE_WIFI = "updater_mobile_wifi";
}
