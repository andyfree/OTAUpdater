package com.kryten2k35.otaupdater;

public class Downloads {

	String name;
    String desc;
    String url;
    
   String categoryName;
    
    static int downloadsCreated = 0;
    final int localIndex;

    public Downloads() {
        localIndex = downloadsCreated++;
    }

    public String getName() {
        if (name == null || name.isEmpty())
            return "Downloads #" + localIndex;
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }  
}
