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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kryten2k35.otaupdater.Connectivity;
import com.kryten2k35.otaupdater.R;
import com.kryten2k35.otaupdater.RemoteUpdateFile;
import com.kryten2k35.otaupdater.UpdaterApplication;
import com.kryten2k35.otaupdater.utils.Constants;
import com.kryten2k35.otaupdater.utils.Preferences;
import com.kryten2k35.otaupdater.utils.Utils;

public class MainActivity extends ActionBarActivity implements Constants{

	public final String TAG = this.getClass().getSimpleName();

	private Context mContext;
	private Connectivity mConnectivity;
	private AlertDialog.Builder mAlert;
	private RemoteUpdateFile remoteFileInfo;

	private TextView progressCounter;
	private ProgressBar progressBar;
	private TextView downloadFilename;
	private TextView updateAvailableSummary;
	private TextView noUpdateAvailableSummary;

	private LinearLayout ongoingDownloadsLayout;
	private LinearLayout noUpdateAvailableLayout;
	private LinearLayout updateAvailableLayout;
	private LinearLayout updaterDevLinkLayout;
	private LinearLayout updaterDonateLinkLayout;


	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if(action.equals(DOWNLOAD_PROGRESS)){
				int progress = intent.getIntExtra("progress", 0);
				int total = intent.getIntExtra("total", 0); 
				onDownloadProgressUpdate(progress, total);
				UpdaterApplication.DOWNLOAD_STATE = DOWNLOAD_IS_ONGOING;
			} 
			if(action.equals(DOWNLOAD_FINISHED)){
				onDownloadFinished();
				UpdaterApplication.DOWNLOAD_STATE = DOWNLOAD_HAS_FINISHED;
			}
			if(action.equals(DOWNLOAD_CANCELLED)){
				onDownloadCancelled();
				UpdaterApplication.DOWNLOAD_STATE = DOWNLOAD_NOT_STARTED;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		setTheme(Preferences.getTheme(mContext));   

		super.onCreate(savedInstanceState);        
		setContentView(R.layout.updater_main);

		mConnectivity = new Connectivity(this);
		remoteFileInfo = UpdaterApplication.getRemoteFileInfo();

		downloadFilename = (TextView) findViewById(R.id.updater_ongoing_download_title);
		progressCounter = (TextView) findViewById(R.id.updater_ongoing_download_summary);
		progressBar = (ProgressBar) findViewById(R.id.update_download_progress_bar);

		updateAvailableSummary = (TextView) findViewById(R.id.updater_available_summary);
		noUpdateAvailableSummary = (TextView) findViewById(R.id.updater_check_for_updates_summary);

		ongoingDownloadsLayout = (LinearLayout) findViewById(R.id.updater_ongoing_download_layout);
		noUpdateAvailableLayout = (LinearLayout) findViewById(R.id.updater_no_updates);
		updateAvailableLayout = (LinearLayout) findViewById(R.id.updater_updates_available);
		
		updaterDevLinkLayout = (LinearLayout) findViewById(R.id.updater_dev_link);
		updaterDonateLinkLayout = (LinearLayout) findViewById(R.id.updater_dev_donate_link);

		createAlert();

		updateLayouts();
		getRomInformation();
	}

	private void createAlert() {
		int[] attrs = { R.attr.alertIcon };
		TypedArray ta = this.obtainStyledAttributes(attrs);

		mAlert = new Builder(this);
		mAlert.setTitle(getString(R.string.not_connected));
		mAlert.setMessage(getString(R.string.connect_before_update));
		mAlert.setIcon(ta.getDrawable(0));
		mAlert.setPositiveButton("Ok", null);
		ta.recycle();
	}

	@Override
	public void onStart(){
		super.onStart();
		this.registerReceiver(mReceiver, new IntentFilter(DOWNLOAD_PROGRESS));
	}

	@Override
	public void onStop(){
		super.onStop();
		this.unregisterReceiver(mReceiver);
	}


	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed Called");
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.updater_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.updater_menusettings:
			Intent settings = new Intent(this, SettingsActivity.class);
			startActivity(settings);
			return true;
		case R.id.updater_menuinfo:
			Intent about = new Intent(this, AboutActivity.class);
			startActivity(about);          
			return true;
		default:
			return super.onOptionsItemSelected(item);           
		}
	}

	public void openUpdates(View v){
		if(mConnectivity.isConnected()){
			if(Preferences.getUpdateAvailable(mContext)){
				Intent intent = new Intent(this, AvailableActivity.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, CheckActivity.class);
				startActivity(intent);
			}
		} else {
			mAlert.show();
		}
	}

	public void openExtraDownloads(View v){
		//Intent intent = new Intent(this, UpdaterCheck.class);
		//startActivity(intent);
	}

	public void openOngoingUpdate(View v){
		Intent intent = new Intent(this, AvailableActivity.class);
		startActivity(intent);
	}

	public void openGapps(View v){
		//Intent intent = new Intent(this, UpdaterCheck.class);
		//startActivity(intent);
	}

	public void openDevDonateLink(View v){
				String url = remoteFileInfo.getDonateLink();
		        Intent intent = new Intent(Intent.ACTION_VIEW);
		        intent.setData(Uri.parse(url));
		        startActivity(intent);
	}

	public void openDevLink(View v){
		String url = remoteFileInfo.getWebsite();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	private void updateLayouts(){
		boolean updateAvailable = Preferences.getUpdateAvailable(mContext);

		if(UpdaterApplication.DOWNLOAD_STATE == DOWNLOAD_IS_ONGOING){
			updateAvailableLayout.setVisibility(View.GONE);
			noUpdateAvailableLayout.setVisibility(View.GONE);
		} else {
			updateAvailableLayout.setVisibility(
					updateAvailable ? View.VISIBLE : View.GONE);
			noUpdateAvailableLayout.setVisibility(
					updateAvailable ? View.GONE : View.VISIBLE);
		}

		ongoingDownloadsLayout.setVisibility(
				UpdaterApplication.DOWNLOAD_STATE == DOWNLOAD_IS_ONGOING ? View.VISIBLE : View.GONE);

		if(updateAvailable){

			updateAvailableSummary.setText(remoteFileInfo.getUpdateName() + "\n" + mContext.getString(R.string.tap_to_view));

			switch(UpdaterApplication.DOWNLOAD_STATE){
			case DOWNLOAD_NOT_STARTED:
				progressCounter.setText(Utils.formatDataFromBytes(remoteFileInfo.getRemoteFileSize())); 
				break;
			case DOWNLOAD_IS_ONGOING:
				downloadFilename.setText(remoteFileInfo.getUpdateName());
				break;
			case DOWNLOAD_HAS_FINISHED:
				onDownloadFinished();
			default:
				break;
			}
		} else {
			boolean is24 = DateFormat.is24HourFormat(mContext);
			Date now = new Date();
			Locale locale = Locale.getDefault();
			String time;

			if(is24){
				time = new SimpleDateFormat("d, MMMM HH:mm", locale).format(now);
			} else {
				time = new SimpleDateFormat("d, MMMM hh:mm a", locale).format(now);
			}    

			Preferences.setUpdateLastChecked(mContext, time);
			String lastChecked = getString(R.string.last_checked);
			noUpdateAvailableSummary.setText(lastChecked + " " + time);
		}
	}

	private void getRomInformation(){
		String htmlColorOpen = "<font color='#33b5e5'>";
		String htmlColorClose = "</font>";

		//ROM name
		TextView romName = (TextView) findViewById(R.id.updater_rom_name);        
		String romNameTitle = getApplicationContext().getResources().getString(R.string.rom_name) + " ";
		String romNameActual = Utils.getProp("ro.ota.romname");        
		romName.setText(Html.fromHtml(romNameTitle + htmlColorOpen + romNameActual + htmlColorClose));

		//ROM version
		TextView romVersion = (TextView) findViewById(R.id.updater_rom_version);        
		String romVersionTitle = getApplicationContext().getResources().getString(R.string.rom_version) + " ";
		String romVersionActual = Utils.getProp("ro.ota.version");        
		romVersion.setText(Html.fromHtml(romVersionTitle + htmlColorOpen + romVersionActual + htmlColorClose));

		//ROM codename
		TextView romCodeName = (TextView) findViewById(R.id.updater_rom_codename);        
		String romCodeNameTitle = getApplicationContext().getResources().getString(R.string.rom_codename) + " ";
		String romCodeNameActual = Utils.getProp("ro.ota.codename");       
		romCodeName.setText(Html.fromHtml(romCodeNameTitle + htmlColorOpen + romCodeNameActual + htmlColorClose));

		//ROM date
		TextView romDate = (TextView) findViewById(R.id.updater_rom_date);        
		String romDateTitle = getApplicationContext().getResources().getString(R.string.rom_build_date) + " ";
		String romDateActual = Utils.getProp("ro.build.date");
		romDate.setText(Html.fromHtml(romDateTitle + htmlColorOpen + romDateActual + htmlColorClose));

		//ROM android version
		TextView romAndroid = (TextView) findViewById(R.id.updater_android_version);        
		String romAndroidTitle = getApplicationContext().getResources().getString(R.string.android_verison) + " ";
		String romAndroidActual = Utils.getProp("ro.build.version.release");       
		romAndroid.setText(Html.fromHtml(romAndroidTitle + htmlColorOpen + romAndroidActual + htmlColorClose));

		//ROM developer
		TextView romDeveloper = (TextView) findViewById(R.id.updater_rom_developer);
		boolean showDevName = !remoteFileInfo.getDeveloper().equals("null");
		romDeveloper.setVisibility(showDevName? View.VISIBLE : View.GONE);
		
		String romDeveloperTitle = getApplicationContext().getResources().getString(R.string.rom_developer) + " ";
		String romDeveloperActual = remoteFileInfo.getDeveloper();       
		romDeveloper.setText(Html.fromHtml(romDeveloperTitle + htmlColorOpen + romDeveloperActual + htmlColorClose));
		
		// ROM website summary
		boolean showDevLink = !remoteFileInfo.getWebsite().equals("null");
		updaterDevLinkLayout.setVisibility(showDevLink? View.VISIBLE : View.GONE);
		View linkSeparator = (View) findViewById(R.id.updaterLinkSeparator);
		linkSeparator.setVisibility(showDevLink? View.VISIBLE : View.GONE);
		
		TextView romWebsiteDisplay = (TextView) findViewById(R.id.updater_dev_link_summary);
		romWebsiteDisplay.setText(remoteFileInfo.getWebsite());

		// ROM Donate summary
		boolean showDonateLink = !remoteFileInfo.getDonateLink().equals("null");
		updaterDonateLinkLayout.setVisibility(showDonateLink? View.VISIBLE : View.GONE);
		View donateSeparator = (View) findViewById(R.id.updaterDonateSeparator);
		donateSeparator.setVisibility(showDonateLink? View.VISIBLE : View.GONE);
		
		TextView romDonateLinkDisplay = (TextView) findViewById(R.id.updater_dev_donate_link_summary);
		romDonateLinkDisplay.setText(remoteFileInfo.getDonateLink());
	}


	private void onDownloadProgressUpdate(int progress, int total) {
		progressBar.setProgress(progress);
		progressCounter.setText(Utils.formatDataFromBytes(total)+"/"+Utils.formatDataFromBytes(remoteFileInfo.getRemoteFileSize()));        
	}

	private void onDownloadFinished() {
		progressCounter.setTextColor(getResources().getColor(R.color.holo_green_light));
		progressCounter.setText(R.string.ready_to_flash);
		progressBar.setProgress(100);
		UpdaterApplication.DOWNLOAD_STATE = DOWNLOAD_HAS_FINISHED;
	}

	private void onDownloadCancelled() {
		progressBar.setProgress(0);
		progressCounter.setText(Utils.formatDataFromBytes(remoteFileInfo.getRemoteFileSize())); 
		UpdaterApplication.DOWNLOAD_STATE = DOWNLOAD_NOT_STARTED;
	}
}
