package com.lyl.autoupdate.base;

import com.lyl.autoupdate.R;

/**
 * Created by lyl on 2018/02/02.
 * Update by lyl on 2022/03/17
 */
public class BaseSetting {

    private static volatile BaseSetting instance;

    public static BaseSetting getInstance() {
        if (instance == null) {
            instance = new BaseSetting();
        }
        return instance;
    }

    public static void setBaseSetting(BaseSetting basesetting) {
        BaseSetting.instance = basesetting;
    }

    public BaseSetting() {
    }

    public BaseSetting(int downloadLogo, int appName) {
        this.downloadLogo = downloadLogo;
        this.appName = appName;
        setBaseSetting(this);
    }


    int downloadLogo = R.mipmap.ic_launcher;
    int appName = R.string.app_name;
    int downloadStart = R.string.downloadStart;
    int downloadFail = R.string.downloadFail;
    int downloadSuccess = R.string.downloadSuccess;
    int installSuccess = R.string.installSuccess;
    int downloadSuccessToClick = R.string.downloadSuccessToClick;
    int er_downloadUrl = R.string.er_downloadUrl;
    int pt_downloading = R.string.pt_downloading;
    int pt_waiting = R.string.pt_waiting;
    int pt_checkUpdate = R.string.pt_checkUpdate;
    int pt_downloadedIsInstall = R.string.pt_downloadedIsInstall;
    int pt_install = R.string.pt_install;
    int pt_cancel = R.string.pt_cancel;
    int pt_installFileIsDelete = R.string.pt_installFileIsDelete;
    int pt_findNewVersion = R.string.pt_findNewVersion;
    int updateNow = R.string.updateNow;

    public int getDownloadLogo() {
        return downloadLogo;
    }

    public void setDownloadLogo(int downloadLogo) {
        this.downloadLogo = downloadLogo;
    }

    public int getAppName() {
        return appName;
    }

    public void setAppName(int appName) {
        this.appName = appName;
    }

    public int getDownloadFail() {
        return downloadFail;
    }

    public void setDownloadFail(int downloadFail) {
        this.downloadFail = downloadFail;
    }

    public int getDownloadSuccess() {
        return downloadSuccess;
    }

    public void setDownloadSuccess(int downloadSuccess) {
        this.downloadSuccess = downloadSuccess;
    }

    public int getInstallSuccess() {
        return installSuccess;
    }

    public void setInstallSuccess(int installSuccess) {
        this.installSuccess = installSuccess;
    }

    public int getDownloadSuccessToClick() {
        return downloadSuccessToClick;
    }

    public void setDownloadSuccessToClick(int downloadSuccessToClick) {
        this.downloadSuccessToClick = downloadSuccessToClick;
    }

    public int getEr_downloadUrl() {
        return er_downloadUrl;
    }

    public void setEr_downloadUrl(int er_downloadUrl) {
        this.er_downloadUrl = er_downloadUrl;
    }

    public int getPt_downloading() {
        return pt_downloading;
    }

    public void setPt_downloading(int pt_downloading) {
        this.pt_downloading = pt_downloading;
    }

    public int getPt_waiting() {
        return pt_waiting;
    }

    public void setPt_waiting(int pt_waiting) {
        this.pt_waiting = pt_waiting;
    }

    public int getPt_checkUpdate() {
        return pt_checkUpdate;
    }

    public void setPt_checkUpdate(int pt_checkUpdate) {
        this.pt_checkUpdate = pt_checkUpdate;
    }

    public int getPt_downloadedIsInstall() {
        return pt_downloadedIsInstall;
    }

    public void setPt_downloadedIsInstall(int pt_downloadedIsInstall) {
        this.pt_downloadedIsInstall = pt_downloadedIsInstall;
    }

    public int getPt_install() {
        return pt_install;
    }

    public void setPt_install(int pt_install) {
        this.pt_install = pt_install;
    }

    public int getPt_cancel() {
        return pt_cancel;
    }

    public void setPt_cancel(int pt_cancel) {
        this.pt_cancel = pt_cancel;
    }

    public int getPt_installFileIsDelete() {
        return pt_installFileIsDelete;
    }

    public void setPt_installFileIsDelete(int pt_installFileIsDelete) {
        this.pt_installFileIsDelete = pt_installFileIsDelete;
    }

    public int getPt_findNewVersion() {
        return pt_findNewVersion;
    }

    public void setPt_findNewVersion(int pt_findNewVersion) {
        this.pt_findNewVersion = pt_findNewVersion;
    }

    public int getUpdateNow() {
        return updateNow;
    }

    public void setUpdateNow(int updateNow) {
        this.updateNow = updateNow;
    }

    public int getDownloadStart() {
        return downloadStart;
    }

    public void setDownloadStart(int downloadStart) {
        this.downloadStart = downloadStart;
    }
}
