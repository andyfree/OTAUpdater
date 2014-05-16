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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;

import com.kryten2k35.otaupdater.UpdaterApplication;
import com.kryten2k35.otaupdater.receivers.BackgroundReceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

public class Utils {

    public final String TAG = this.getClass().getSimpleName();
    
   private static Context mContext = UpdaterApplication.getContext();
   private static final String SPACE = " ";
   private static final String BULLET_SYMBOL = "&#8226";
   private static final String EOL = System.getProperty("line.separator");
   
   private static final int KILOBYTE = 1024;
   private static int KB = KILOBYTE;
   private static int MB = KB * KB;
   private static int GB = MB * KB;
   
   private static DecimalFormat decimalFormat = new DecimalFormat("##0.#");
   static {
       decimalFormat.setMaximumIntegerDigits(3);
       decimalFormat.setMaximumFractionDigits(1);
   }
    
    public static Boolean doesPropExist(String propName) {
        boolean valid = false;

        try {
            Process process = Runtime.getRuntime().exec("getprop");
            BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) 
            {
                if(line.contains("[" + propName +"]")){
                    valid = true;
                }
            }
            bufferedReader.close();
        } 
        catch (IOException e){
            e.printStackTrace();
        }
        return valid;
    }
    
    public static String getProp(String propName) {
        Process p = null;
        String result = "";
        try {
            p = new ProcessBuilder("/system/bin/getprop", propName).redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line=br.readLine()) != null){
                result = line;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    static public String getBulletList(String header, String []items) {
        StringBuilder listBuilder = new StringBuilder();
        if (header != null && !header.isEmpty()) {
            listBuilder.append(header + EOL + EOL);
        }
        if (items != null && items.length != 0) {
            for (String item : items) {
                Spanned formattedItem = Html.fromHtml(BULLET_SYMBOL + SPACE + item);
                listBuilder.append(formattedItem + EOL + EOL);
            }
        }
        return listBuilder.toString();
    }

    public static String formatDataFromBytes(long size) {
        
        String symbol;
        KB = KILOBYTE;
        symbol = "B";
        if (size < KB) {
            return decimalFormat.format(size) + symbol;
        } else if (size < MB) {
            return decimalFormat.format(size / (float)KB) + 'k' + symbol;
        } else if (size < GB) {
            return decimalFormat.format(size / (float)MB) + 'M' + symbol;
        }
        return decimalFormat.format(size / (float)GB) + 'G' + symbol;
    }
    
    public static void setBackgroundCheck(Context context, boolean set){
        int requestedInteval = Preferences.getBackgroundFrequency(mContext);

        Intent intent = new Intent(context, BackgroundReceiver.class);
        
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis() + requestedInteval * 1000;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(set){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, requestedInteval*1000, PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            alarmManager.cancel(PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }
}
