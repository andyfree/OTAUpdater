/*
 * Copyright (C) 2014 Matt Booth (Kryten2k35).
 * Copyright (C) 2013 Ficeto 
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import com.stericson.RootTools.BuildConfig;
import com.stericson.RootTools.RootTools;

public class Tools {
    
    public final String TAG = this.getClass().getSimpleName();

	public static void recovery(){
		rebootPhone("recovery");
	}

	public static void recovery(Context context){
		rebootPhone(context ,"recovery");
	}
	
	public static String shell(String cmd) {
    	String out = "";
    	ArrayList<String> r = system(getSuBin(),cmd).getStringArrayList("out");
    	for(String l: r){
    		out += l+"\n";
    	}
    	return out;
	}
	
	public static String noneRootShell(String cmd) {
    	String out = "";
    	ArrayList<String> r = system("sh",cmd).getStringArrayList("out");
    	for(String l: r){
    		out += l+"\n";
    	}
    	return out;
	}
	
	public static void getRoot(){
			RootTools.isAccessGiven();
	}
	
	private static void rebootPhone(String type){
		shell("reboot "+type);
	}

	private static void rebootPhone(Context context, String type) {
		try {
			((PowerManager) context.getSystemService(Context.POWER_SERVICE)).reboot(type);
		} catch (Exception e) {
			Log.e("Tools", "reboot '"+type+"' error: "+e.getMessage());
			shell("reboot "+type);
		}
	}
		
	private static boolean isUiThread(){
    	return (Looper.myLooper() == Looper.getMainLooper());
    }
	
    private static String getSuBin(){
    	if(new File("/system/xbin","su").exists()){
    		return "/system/xbin/su";
    	}
    	if(RootTools.isRootAvailable()){
    		return "su";
    	}
    	return "sh";
    }
    
    private static Bundle system(String shell, String command) {
		if (BuildConfig.DEBUG) {
			if (isUiThread()) {
				Log.e(shell,"Application attempted to run a shell command from the main thread");
			}
			Log.d(shell,"START");
		}
	
		ArrayList<String> res = new ArrayList<String>();
		ArrayList<String> err = new ArrayList<String>();
		boolean success = false;
		try {
			Process process = Runtime.getRuntime().exec(shell);
			DataOutputStream STDIN = new DataOutputStream(process.getOutputStream());
			BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader STDERR = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			if (BuildConfig.DEBUG) Log.i(shell, command);
			STDIN.writeBytes(command + "\n");
			STDIN.flush();
			STDIN.writeBytes("exit\n");
			STDIN.flush();
				
			process.waitFor();
			if (process.exitValue() == 255) {
				if (BuildConfig.DEBUG) Log.e(shell,"SU was probably denied! Exit value is 255");
				err.add("SU was probably denied! Exit value is 255");
			}
			
			while (STDOUT.ready()) {
				String read = STDOUT.readLine();
				if (BuildConfig.DEBUG) Log.d(shell, read);
				res.add(read);
			}
			while (STDERR.ready()) {
				String read = STDERR.readLine();
				if (BuildConfig.DEBUG) Log.e(shell, read);
				err.add(read);
			}
			
			process.destroy();
    		success = true;
    		if(err.size() > 0){
    			success = false;
    		}
		} catch (IOException e) {
			if (BuildConfig.DEBUG) Log.e(shell,"IOException: "+e.getMessage());
			err.add("IOException: "+e.getMessage());
		} catch (InterruptedException e) {
			if (BuildConfig.DEBUG) Log.e(shell,"InterruptedException: "+e.getMessage());
			err.add("InterruptedException: "+e.getMessage());
		}
		if (BuildConfig.DEBUG) Log.d(shell,"END");
		Bundle r = new Bundle();
		r.putBoolean("success", success);
		r.putString("cmd", command);
		r.putString("binary", shell);
		r.putStringArrayList("out", res);
		r.putStringArrayList("error", err);
		return r;
	}
}
