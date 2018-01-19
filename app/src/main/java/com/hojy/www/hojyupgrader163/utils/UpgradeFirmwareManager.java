package com.hojy.www.hojyupgrader163.utils;

import android.os.Environment;

import com.hojy.www.hojyupgrader163.model.Firmware;
import com.hojy.www.hojyupgrader163.network.HojyRequestManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ron on 2016/10/27.
 */

public class UpgradeFirmwareManager {


    private static class UpgradeFirmwareManagerHolder{
        private static final UpgradeFirmwareManager upgradeFirmwareManager = new UpgradeFirmwareManager();
    }

    private UpgradeFirmwareManager(){}

    public static UpgradeFirmwareManager getInstalce(){
        return UpgradeFirmwareManagerHolder.upgradeFirmwareManager;
    }

    public  List<Firmware> getLocalFirmwares(){
        List<Firmware> lists ;
        //获取SD上的固件
        lists = getSDFirmwares();

        //获取root上的固件
        lists.addAll(getRootFirmwares());
        return  lists;
    }

    private String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if  (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();

    }

    private String getRootPath(){
        File rootDir = Environment.getRootDirectory();

        return  rootDir.toString();
    }

    public List<Firmware> getSDFirmwares(){
        //获取SD上的固件
        String path = getSDPath()+"/hojydload";
        if(getSDPath() != null){
            return getFiles(path);
        }else {
            List<Firmware> lists = new ArrayList<Firmware>();
            return lists;
        }
    }

    public List<Firmware> getRootFirmwares(){
        //获取内部存储上的固件
        String path = getRootPath()+"/hojydload";
        return getFiles(path);
    }

    private List<Firmware> getFiles(String path){
        List<Firmware> lists = new ArrayList<Firmware>();
        if(path == null){
            return lists;
        }

        File file = new File(path);
        if(!file.exists()){
            file.mkdir();
        }

        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File filetmp:files) {
                String filename = filetmp.getPath();
                if(filename.endsWith(".zip")){
                    Firmware firmware = new Firmware();
                    firmware.setName(filetmp.getName());
                    firmware.setPath(filetmp.getAbsolutePath());
                    firmware.setSize(filetmp.length());
                    lists.add(firmware);
                }
            }
        }
        return  lists;
    }

    public Firmware getFirmwareFromPath(String path) {
        if (path == null) {
            return null;
        }
        Firmware firmware = new Firmware();
        File filetmp = new File(path);
        firmware.setName(filetmp.getName());
        firmware.setPath(filetmp.getAbsolutePath());
        firmware.setSize(filetmp.length());
        return firmware;
    }
}
