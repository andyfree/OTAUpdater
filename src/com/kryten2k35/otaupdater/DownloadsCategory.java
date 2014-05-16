
package com.kryten2k35.otaupdater;

import java.util.ArrayList;
import java.util.List;

public class DownloadsCategory {

    static int totalCategoryCount = 0;
    final int localIndex;
    String id;
    String name;
    List<Downloads> downloads;

    public DownloadsCategory(String i, String n) {
        localIndex = totalCategoryCount++;
        if (i == null)
            id = totalCategoryCount + "";
        else
            id = i;

        if (n == null)
            n = totalCategoryCount + "";
        else
            name = n;

        downloads = new ArrayList<Downloads>();
        
    }

    public void addDownload(Downloads down) {
        downloads.add(down);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public List<Downloads> getDownloads() {
        return downloads;
    }

    public void setDownloads(List<Downloads> downloads) {
        this.downloads = downloads;
    }

    public String toString() {
        return name;
    }
}
