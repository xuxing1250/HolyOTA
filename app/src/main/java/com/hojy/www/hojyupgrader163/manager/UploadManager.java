package com.hojy.www.hojyupgrader163.manager;


import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;


import com.hojy.www.hojyupgrader163.R;
import com.hojy.www.hojyupgrader163.model.Firmware;
import com.hojy.www.hojyupgrader163.model.UpgradeInfo;
import com.hojy.www.hojyupgrader163.network.HojyRequestManager;
import com.hojy.www.hojyupgrader163.network.UploadUtil;
import com.hojy.www.hojyupgrader163.utils.HojyLoger;
import com.hojy.www.hojyupgrader163.utils.UpgradeFirmwareManager;
import com.hojy.www.hojyupgrader163.utils.XMLDomService;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class UploadManager {
    private Context mContext;
    File file = null;
    File sdDir = null;
    private File uploadFile;
    private Handler acHandler;
    private UpgradeInfo upgradeInfo;
    private int localUpgradeCheckCount = 0;
    private int localUpgradeCheckApplyCount = 0;

    public static final int UPDATE_START = 10;
    public static final int UPDATE_SECCESSED = 11;
    public static final int UPDATE_SUCCESSED_REBOOT = 12;

    public UploadManager(Context context, Handler handler) {
        mContext = context;
        acHandler = handler;
    }

    public void startCopyThread() {
        copyFilesthread.start();
    }


    UploadUtil.OnUploadProcessListener onUploadProcessListener = new UploadUtil.OnUploadProcessListener() {
        @Override
        public void onUploadDone(int responseCode, String message) {
            //上传成功
            Message msg = new Message();
            msg.what = 4;
            handler.sendMessage(msg);
            HojyLoger.d("LocalUPgradeActivity","onUploadDone-> responseCore:"+responseCode+"  message:"+message);
        }

        @Override
        public void onUploadProcess(int uploadSize) {
        }

        @Override
        public void initUpload(int fileSize) {
            HojyLoger.d("LocalUPgradeActivity","initUpload-> fileSize:"+fileSize);
        }
    };

    private OnResponseListener<String> localUpgradeApplyResultListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            Log.d("UploadManager", "onSucceed: -------result" + result);
            try {
                result = new String(result.getBytes("utf-8"));
                HojyLoger.d("VersionInfoActivity","localUpgradeApplyResultListener-> onSucceed result:"+result);
            } catch (UnsupportedEncodingException e) {
                HojyLoger.d("VersionInfoActivity","localUpgradeApplyResultListener-> onSucceed exception:"+e.toString());
                e.printStackTrace();
                return;
            }
            upgradeInfo = XMLDomService.parseApplyXml(result);
            Log.d("UploadManager", "onSucceed: -----------------upgradeInfo.getApplyErro()" +upgradeInfo.getApplyErro());
            if(upgradeInfo.getApplyErro() == 0 || upgradeInfo.getApplyErro() == 1){

                localUpgradeCouterIncrease();
                if(localUpgradeCouterReachMax() == true){
                    handleLocalUpgradeFail("升级失败!");
                }else{
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessageDelayed(message,200);
                }

            } else if(upgradeInfo.getApplyErro() == 2 || upgradeInfo.getApplyErro() == 3){
                handleLocalUpgradeSuccess(UPDATE_START);
                checkUpdateDoneThread.start();
            }else if (upgradeInfo.getApplyErro() == 4){
                if(localUpgradeCheckApplyCountReachMax() == true){
                    handleLocalUpgradeFail("升级失败!");
                }else {
                    localUpgradeCheckApplyCountIncrease();
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessageDelayed(message,1000);
                }
            }else{
                handleLocalUpgradeFail("升级失败!");
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            //获取升级信息失败，设备在重启，认为开始升级
            HojyLoger.d("VersionInfoActivity","localUpgradeApplyResultListener-> onFailed :"+response);
            handleLocalUpgradeSuccess(UPDATE_START);
            checkUpdateDoneThread.start();
        }
        @Override
        public void onFinish(int what) {

        }
    };

    public void uploadFirmware(Firmware firmware){
        HojyLoger.d("LocalUPgradeActivity","uploadFirmware-> enter function");
        HojyLoger.d("LocalUPgradeActivity","uploadFirmware-> uploadFile path: "+firmware.getPath());
        uploadFile = new File(firmware.getPath());
        UploadUtil.getInstance().setOnUploadProcessListener(onUploadProcessListener);
        UploadUtil.getInstance().uploadFile(uploadFile.getAbsolutePath(), "update",
                "http://192.168.0.1/xml_action.cgi?Action=Upload&file=upgrade&command", new HashMap<String, String>());
        Message message = new Message();
        message.what = 1;
        Message acMsg = new Message();
        acMsg.what = 0;
        acHandler.sendMessage(acMsg);
        handler.sendMessageDelayed(message,50);
    }

    private void handleLocalUpgradeFail(String prompt){
        //Toast.makeText(LocalUPgradeActivity.this,prompt,Toast.LENGTH_SHORT).show();
        localUpgradeCouterClear();
        localUpgradeCheckApplyCountClear();
        Message message = new Message();
        message.what = 2;
        message.obj = prompt;
        acHandler.sendMessage(message);
    }


    private void handleLocalUpgradeSuccess(int type){
        //Toast.makeText(LocalUPgradeActivity.this,prompt,Toast.LENGTH_SHORT).show();
        localUpgradeCouterClear();
        localUpgradeCheckApplyCountClear();
        Message message = new Message();
        message.what = 1;

        switch (type) {
            case UPDATE_START:
                message.obj = "开始升级";
                message.arg1 = 1;
                break;
            case UPDATE_SECCESSED:
                message.obj = "上传成功";
                message.arg1 = 2;
                break;
            case UPDATE_SUCCESSED_REBOOT:
                message.obj = "升级成功，等待系统重启......";
                message.arg1 = 3;
                break;
        }
        acHandler.sendMessage(message);

    }

    /**
     * tongzhi activity chuanru shuju bing jinxing gengxin
     */

    private void handlelocalUploadFirmware( ){
        Message message = new Message();
        message.what = 3;
        acHandler.sendMessage(message);
    }

    private OnResponseListener<String> localUploadFirmwareState = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {

            String result = response.get();
            try {
                result = new String(result.getBytes("utf-8"));
                HojyLoger.d("LocalUpgradeActivity","localUploadFirmwareState-> onSuccessed  result:"+result);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }
            String uploadState = XMLDomService.paraseUploadStateXML(result);
            if(uploadState.equals("finish")){
                localUpgradeCouterClear();
                localUpgradeCheckApplyCountClear();
                Message message = new Message();
                message.what = 2;
                handler.sendMessageDelayed(message,200);
            }else if(uploadState.equals("deliver")){
                Message message = new Message();
                message.what = 1;
                handler.sendMessageDelayed(message,1000);
            }else{
                handleLocalUpgradeFail("升级失败!");
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            HojyLoger.d("LocalUpgradeActivity","localUploadFirmwareState-> onFailed :"+response);
            handleLocalUpgradeFail("升级失败!");
        }

        @Override
        public void onFinish(int what) {

        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                HojyRequestManager.getInstalce().requestUploadFirmwareState(localUploadFirmwareState);
            } else if(msg.what == 2){
                HojyRequestManager.getInstalce().requestCheckUpgradeInfoFromServer(localUpgradeApplyResultListener);
            }else if(msg.what == 3){
                handleLocalUpgradeFail("上传失败");
            }else if(msg.what == 4){
                handleLocalUpgradeSuccess(UPDATE_SECCESSED);
            }else if(msg.what == 5){
                handlelocalUploadFirmware();
            }else if(msg.what == 6){
                Log.d("ManualActivity", "handleMessage:-------------升级成功 ");
                handleLocalUpgradeSuccess(UPDATE_SUCCESSED_REBOOT);
            }
            super.handleMessage(msg);
        }
    };

    Thread copyFilesthread = new Thread(new Runnable() {

        @Override
        public void run() {
            try{
                //runShellCommandThread.join();
                HojyLoger.d("LocalUPgradeActivity"," runShellCommandThread finish ");
                copyFiles();
                Message message = new Message();
                message.what = 5;
                handler.sendMessage(message);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    Thread checkUpdateDoneThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                //check Update start
                while(runPing()){
                    Thread.sleep(2000);
                }

                HojyLoger.d("LocalUPgradeActivity"," Update start ");

                //check Update done
                while(!runPing()){
                    Thread.sleep(2000);
                }
                RecursionDeleteFile(sdDir);
                Message message = new Message();
                message.what = 6;
                handler.sendMessage(message);

                Thread.sleep(10000);
                rebootSystem();
            }catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    });

    void copyFiles() {
        try {
            String path = "hojydload";
            String filename = "update.zip";
            sdDir = createSDDir(path);
            file = createFileInSDCard(filename, path);
            InputStream inStream;
            inStream = mContext.getResources().openRawResource(R.raw.update);
            OutputStream opStream = new FileOutputStream(file);
            byte [] buffer = new byte[1024];
            int length = inStream.read(buffer);
            while(length > 0) {
                opStream.write(buffer, 0, length);
                length = inStream.read(buffer);
            }

            opStream.flush();
            opStream.close();
            inStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    public File createSDDir(String dir) {
        File dirFile = new File(getSDPath() + dir + File.separator);
        System.out.println("creat dir:" + dirFile.mkdirs());
        return dirFile;
    }

    public File createFileInSDCard(String fileName, String dir) throws IOException {
        File file = new File(getSDPath() + dir + File.separator + fileName);
        file.createNewFile();
        return file;
    }

    private String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist){
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.getAbsolutePath() + "/";
    }


    Thread runShellCommandThread = new Thread(new Runnable() {
        @Override
        public void run() {
            String cmds[]  =  new String[]{"start", "net_dhcp"};
            runShellCommand(cmds);
        }
    });


    private void runShellCommand(String[] command) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
            int status = p.waitFor();
            HojyLoger.d("LocalUPgradeActivity","status:"+status);

            if (status != 0) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean runPing() {
        Runtime runtime =Runtime.getRuntime();
        Process process = null;
        String line = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String ip = "192.168.0.1";
        boolean res = false;
        int num = 3;
        try {
            process =runtime.exec("ping " + " -c " + num + " " + ip);
            is =process.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            while ((line= br.readLine()) != null) {
                if(line.contains("ttl") || line.contains("TTL") ) {
                    res= true;
                    break;
                }
            }
            is.close();
            isr.close();
            br.close();
            process.destroy();
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return res;
    }


    private void rebootSystem(){
        PowerManager pManager=(PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        pManager.reboot("");
    }



    private void localUpgradeCheckApplyCountClear(){
        localUpgradeCheckApplyCount = 0;
    }
    private void localUpgradeCheckApplyCountIncrease(){
        localUpgradeCheckApplyCount++;
    }
    private boolean localUpgradeCheckApplyCountReachMax(){
        if(localUpgradeCheckApplyCount > 10){
            return true;
        }else {
            return false;
        }
    }

    private void localUpgradeCouterClear(){
        localUpgradeCheckCount = 0;
    }
    private void localUpgradeCouterIncrease(){
        localUpgradeCheckCount++;
    }
    private boolean localUpgradeCouterReachMax(){
        if(localUpgradeCheckCount > 60){
            return  true;
        }else {
            return false;
        }
    }

    public void RecursionDeleteFile(File file){

        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

    public void onDestroy() {
        RecursionDeleteFile(sdDir);
        /*if (runShellCommandThread !=  null) {
            runShellCommandThread.interrupt();
            runShellCommandThread = null;
        }*/

        if (copyFilesthread !=  null) {
            copyFilesthread.interrupt();
            copyFilesthread = null;
        }

        if (checkUpdateDoneThread !=  null) {
            checkUpdateDoneThread.interrupt();
            checkUpdateDoneThread = null;
        }
    }
}
