package com.soil.soilsample.model;

/**
 * Created by GIS on 2016/6/23 0023.
 */
public class FirVersionJson {
    private String version;

    public String getVersionShort() {
        return versionShort;
    }

    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    private String versionShort;
    private String changelog;
    private String update_url;

    public String getVersion() {
        return version;
    }

    public void setVersion(String versionShort) {
        this.version = version;
    }

    public String getChangeLog() {
        return changelog;
    }

    public void setChangeLog(String changeLog) {
        this.changelog = changeLog;
    }

    public String getUpdataUrl() {
        return update_url;
    }

    public void setUpdataUrl(String updataUrl) {
        this.update_url = updataUrl;
    }


}
