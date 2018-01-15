package com.hojy.www.hojyupgrader163.model;

/**
 * Created by Ron on 2016/10/25.
 */

public class UpgradeInfo {

    private int upgradeModel;
    private String upgradeState;
    private int forceUpgrade;

    private int queryErro;
    private int newPackage;

    //query
    private String packageVersion;
    private String packageUrl;
    private long   packageSize;
    private String packageMd5;
    private String packageDescCn;
    private String packageDescEn;
    //download
    private int    downloadErro;
    private int    downloadProgress;
    //apply
    private int    applyErro;


    public int getUpgradeModel() {
        return upgradeModel;
    }

    public void setUpgradeModel(int upgradeModel) {
        this.upgradeModel = upgradeModel;
    }

    public String getUpgradeState() {
        return upgradeState;
    }

    public void setUpgradeState(String upgradeState) {
        this.upgradeState = upgradeState;
    }

    public int getForceUpgrade() {
        return forceUpgrade;
    }

    public void setForceUpgrade(int forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }

    public int getQueryErro() {
        return queryErro;
    }

    public void setQueryErro(int queryErro) {
        this.queryErro = queryErro;
    }

    public int getNewPackage() {
        return newPackage;
    }

    public void setNewPackage(int newPackage) {
        this.newPackage = newPackage;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getPackageUrl() {
        return packageUrl;
    }

    public void setPackageUrl(String packageUrl) {
        this.packageUrl = packageUrl;
    }

    public long getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(long packageSize) {
        this.packageSize = packageSize;
    }

    public String getPackageMd5() {
        return packageMd5;
    }

    public void setPackageMd5(String packageMd5) {
        this.packageMd5 = packageMd5;
    }

    public String getPackageDescCn() {
        return packageDescCn;
    }

    public void setPackageDescCn(String packageDescCn) {
        this.packageDescCn = packageDescCn;
    }

    public String getPackageDescEn() {
        return packageDescEn;
    }

    public void setPackageDescEn(String packageDescEn) {
        this.packageDescEn = packageDescEn;
    }

    public int getDownloadErro() {
        return downloadErro;
    }

    public void setDownloadErro(int downloadErro) {
        this.downloadErro = downloadErro;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int donnloadProgress) {
        this.downloadProgress = donnloadProgress;
    }

    public int getApplyErro() {
        return applyErro;
    }

    public void setApplyErro(int applyErro) {
        this.applyErro = applyErro;
    }
}
