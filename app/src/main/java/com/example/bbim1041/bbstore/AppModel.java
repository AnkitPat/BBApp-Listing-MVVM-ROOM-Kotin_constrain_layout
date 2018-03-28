package com.example.bbim1041.bbstore;

/**
 * Created by BBIM1041 on 22/02/18.
 */

public class AppModel  {
    
    String apkName;
    String apkDate;
    String apkSize;

    public AppModel(String apkName, String apkDate, String apkSize) {
        this.apkName = apkName;
        this.apkDate = apkDate;
        this.apkSize = apkSize;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getApkDate() {
        return apkDate;
    }

    public void setApkDate(String apkDate) {
        this.apkDate = apkDate;
    }

    public String getApkSize() {
        return apkSize;
    }

    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
    }

    @Override
    public String toString() {
        return "AppModel{" +
                "apkName='" + apkName + '\'' +
                ", apkDate='" + apkDate + '\'' +
                ", apkSize='" + apkSize + '\'' +
                '}';
    }
}
