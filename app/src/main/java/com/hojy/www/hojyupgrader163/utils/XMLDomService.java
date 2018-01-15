package com.hojy.www.hojyupgrader163.utils;

import android.os.Handler;
import android.os.Message;

import com.hojy.www.hojyupgrader163.model.HojyDevice;
import com.hojy.www.hojyupgrader163.model.UpgradeInfo;
import com.hojy.www.hojyupgrader163.utils.xml.XmlFactory;
import com.hojy.www.hojyupgrader163.utils.xml.XmlParser;

/**
 * Created by Ron on 2016/10/26.
 */

public class XMLDomService {

    public static UpgradeInfo parseQueryXml(String xmlString){
        UpgradeInfo upgradeInfo = new UpgradeInfo();

        XmlParser xmlParser = XmlFactory.getDefaultParser();

        String newPackageString = xmlParser.getString(xmlString,
                "RGW/upgrade_info/Upgradeinfo/Query/NewPackage");
        upgradeInfo.setNewPackage(Integer.parseInt(newPackageString));

        String versionString = xmlParser.getString(xmlString,
                "RGW/upgrade_info/Upgradeinfo/Query/Version");
        upgradeInfo.setPackageVersion(versionString);

        String descCn = xmlParser.getString(xmlString,
                "\"RGW/upgrade_info/Upgradeinfo/Query/Desc_cn");
        upgradeInfo.setPackageDescCn(descCn);

        return upgradeInfo;
    }

    public static UpgradeInfo parseDownloadXml(String xmlString){
        UpgradeInfo upgradeInfo = new UpgradeInfo();

        XmlParser xmlParser = XmlFactory.getDefaultParser();

        String downloadErrnoString = xmlParser.getString(xmlString,
                "RGW/upgrade_info/Upgradeinfo/Download/DownloadErrno");
        upgradeInfo.setDownloadErro(Integer.parseInt(downloadErrnoString));

        String downloadProgressString = xmlParser.getString(xmlString,
                "RGW/upgrade_info/Upgradeinfo/Download/DownloadProgress");
        upgradeInfo.setDownloadProgress(Integer.parseInt(downloadProgressString));

        String  upgradeState = xmlParser.getString(xmlString,
                "RGW/upgrade_info/Upgradeinfo/UpgradeState");
        upgradeInfo.setUpgradeState(upgradeState);

        return upgradeInfo;
    }

    public static UpgradeInfo parseApplyXml(String xmlString){
        UpgradeInfo upgradeInfo = new UpgradeInfo();

        XmlParser xmlParser = XmlFactory.getDefaultParser();

        if(xmlString.contains("Error 500: Internal Server Error")){
            //升级程序开始重启，升级，系统不能获取升级程序状态，返回内部错误，此时默认为系统升级成功
            upgradeInfo.setApplyErro(2);
        }else {
            String applyErrnoString = xmlParser.getString(xmlString,
                    "RGW/upgrade_info/Upgradeinfo/Apply/ApplyErrno");
            upgradeInfo.setApplyErro(Integer.parseInt(applyErrnoString));
        }
        return upgradeInfo;
    }

    public static String paraseUploadStateXML(String xmlString){
        XmlParser xmlParser = XmlFactory.getDefaultParser();
        String state = xmlParser.getString(xmlString,"RGW/download/download_state");
        return state;
    }

    public static HojyDevice paraseDeviceInfo(String xmlString){
        HojyDevice hojyDevice = new HojyDevice();
        XmlParser xmlParser = XmlFactory.getDefaultParser();

        String firmwareVersion = xmlParser.getString(xmlString,"RGW/sysinfo/version_num");
        hojyDevice.setFirmwareVersion(firmwareVersion);

        String firmwareVersionDate = xmlParser.getString(xmlString,"RGW/sysinfo/version_date");
        hojyDevice.setFirmwareVersionDate(firmwareVersion);

        String hardwareVersion = xmlParser.getString(xmlString,"RGW/sysinfo/hardware_version");
        hojyDevice.setHardwareVersion(hardwareVersion);

        return hojyDevice;
    }
}
