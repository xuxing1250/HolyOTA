package com.hojy.www.hojyupgrader163.model;

import org.dom4j.io.STAXEventReader;

/**
 * Created by Ron on 2016/10/28.
 */

public class HojyDevice {
    private String hardwareVersion;
    private String firmwareVersion;
    private String firmwareVersionDate;

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getFirmwareVersionDate() {
        return firmwareVersionDate;
    }

    public void setFirmwareVersionDate(String firmwareVersionDate) {
        this.firmwareVersionDate = firmwareVersionDate;
    }
}
