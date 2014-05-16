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

import android.util.Log;

import com.kryten2k35.otaupdater.utils.Constants;
import com.kryten2k35.otaupdater.utils.Preferences;
import com.kryten2k35.otaupdater.utils.Utils;

public class RemoteUpdateFile implements Constants{
    
    public final String TAG = this.getClass().getSimpleName();
    
    private String name;
    private String version;
    private String codename;
    private String directUrl;
    private String httpUrl;
    private String md5;
    private String change;
    private String android;
    private String website;
    private String developer;
    private String donateLink;
    private int filesize;
    boolean updateAvailable;

    public RemoteUpdateFile() {
    }

    public String getName(){
        return name;
    }
    
    public String getVersion(){
        return version;
    }
    
    public String getCodename(){
        return codename;
    }
    
    public String getDirectUrl(){
        return directUrl;
    }
    
    public String getHttpUrl(){
        return httpUrl;
    }
    
    public String getMd5(){
        return md5;
    }
    
    public String getChangelog(){
        return change;
    }
    
    public String getAndroidVersion(){
        return android;
    }
    
    public String getWebsite(){
        return website;
    }
    
    public String getDeveloper(){
        return developer;
    }
    
    public String getDonateLink(){
    	return donateLink;
    }
    
    public int getRemoteFileSize(){
        return filesize;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setVersion(String version){
        this.version = version;
    }
    
    public void setCodename(String codename){
        this.codename = codename;
    }
    
    public void setDirectUrl(String url){
        this.directUrl = url;
    }
    
    public void setHttpUrl(String url){
        this.httpUrl = url;
    }
    
    public void setMd5(String md5){
        this.md5 = md5;
    }
    
    public void setChangelog(String change){
        this.change = change;
    }
    
    public void setAndroidVersion(String android){
        this.android = android;
    }
    
    public void setWebsite(String website){
        this.website = website;
    }
    
    public void setDeveloper(String developer){
        this.developer = developer;
    }
    
    public void setDonateLink(String donateLink){
        this.donateLink = donateLink;
    }
    
    public void setRemoteFileSize(int size){
        filesize = size;
    }
    
    public String getFilename(){
        String filename = "/" + name + "-" + codename
                + "-" + Utils.getProp("ro.ota.device") + "-"
                + version + ".zip";
        
        if(DEBUGGING)
            Log.d(TAG, "getFilename() = " + filename);
        return filename;
    }
    
    public String getUpdateName(){
        String update = name
                + " " + android
                + " " + codename
                + " " + version;
        
        if(DEBUGGING)
            Log.d(TAG, "getUpdateName() = " + update);
        return update;
    }
    
    public void setUpdateAvailable(boolean input){
        updateAvailable = input;
        Preferences.setUpdateAvailable(UpdaterApplication.getContext(), input);
    }
}
