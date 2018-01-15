package com.hojy.www.hojyupgrader163.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;
import android.content.Context;
import android.view.WindowManager.LayoutParams;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.os.SystemClock;
import android.os.PowerManager;

import com.hojy.www.hojyupgrader163.R;
import com.hojy.www.hojyupgrader163.model.Firmware;
import com.hojy.www.hojyupgrader163.model.UpgradeInfo;
import com.hojy.www.hojyupgrader163.network.HojyRequestManager;
import com.hojy.www.hojyupgrader163.network.UploadUtil;
import com.hojy.www.hojyupgrader163.utils.HojyLoger;
import com.hojy.www.hojyupgrader163.utils.UpgradeFirmwareManager;
import com.hojy.www.hojyupgrader163.utils.XMLDomService;
import com.hojy.www.hojyupgrader163.view.LVGears;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Response;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.DataOutputStream;


public class LocalUPgradeActivity extends Activity {

    private List<Firmware> lists;
    private List<Map<String, Object>> data;
    private SimpleAdapter adapter;

    private ListView listView;
    private LVGears lvGears;
    private UpgradeInfo upgradeInfo;
    private TextView promptTextView;
    private File uploadFile;
	private View safeView;
	private TextView statusTextView;
	private WindowManager wm;
	boolean isAddView = false;

    private int localUpgradeCheckCount = 0;

    private int localUpgradeCheckApplyCount = 0;
	
	File file = null;
	File sdDir = null;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_upgrade);
        listView = (ListView) findViewById(R.id.local_upgrade_package_listview);
        lvGears = (LVGears) findViewById(R.id.local_upgrade_lv_circularcd);
        promptTextView = (TextView) findViewById(R.id.local_upgrade_prompt_textview);
		//runShellCommandThread.start();
		copyFilesthread.start();
		createSafeWindow();

	    lists = UpgradeFirmwareManager.getInstalce().getLocalFirmwares();

	        if (lists.size() > 0){
	            listView.setVisibility(View.VISIBLE);
	            data = getData();
	            adapter = new SimpleAdapter(this,data,R.layout.upgrade_info_item, new String[]{"title","version"},
	                    new int[]{R.id.title_textview,R.id.version_textview});
	            listView.setAdapter(adapter);
	            setListViewOnItemClick();
	        }else {
	            listView.setVisibility(View.INVISIBLE);
	        }
		
    }

    @Override
    protected void onDestroy() {

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
		
        if (safeView != null) {
            wm.removeViewImmediate(safeView);
        }
        super.onDestroy();
    }
	

    private List<Map<String, Object>> getData() {
        if(data == null){
            data  = new ArrayList<Map<String, Object>>();
        }

        data.clear();

        for (Firmware firmware:lists) {
            Map<String, Object> map = new HashMap<String, Object>();
            map = new HashMap<String, Object>();
            map.put("title", firmware.getName());
            map.put("version", firmware.getPath());
            data.add(map);
        }
        return data;
    }


    private void uploadFirmware(int pos){
        HojyLoger.d("LocalUPgradeActivity","uploadFirmware-> enter function");
        Firmware firmware = lists.get(pos);
        HojyLoger.d("LocalUPgradeActivity","uploadFirmware-> uploadFile path: "+firmware.getPath());
        uploadFile = new File(firmware.getPath());
        lvGears.startAnim();
        promptTextView.setText("正在升级，请勿断电!");
        UploadUtil.getInstance().setOnUploadProcessListener(onUploadProcessListener);
        UploadUtil.getInstance().uploadFile(uploadFile.getAbsolutePath(), "update",
                "http://192.168.0.1/xml_action.cgi?Action=Upload&file=upgrade&command", new HashMap<String, String>());

        Message message = new Message();
        message.what = 1;
        handler.sendMessageDelayed(message,50);
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
    private OnResponseListener<String> localUpgradeApplyResultListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            try {
                result = new String(result.getBytes("utf-8"));
                HojyLoger.d("VersionInfoActivity","localUpgradeApplyResultListener-> onSucceed result:"+result);
            } catch (UnsupportedEncodingException e) {
                HojyLoger.d("VersionInfoActivity","localUpgradeApplyResultListener-> onSucceed exception:"+e.toString());
                e.printStackTrace();
                return;
            }
            upgradeInfo = XMLDomService.parseApplyXml(result);
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
                handleLocalUpgradeSuccess("开始升级...");
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
            handleLocalUpgradeSuccess("开始升级!");
			checkUpdateDoneThread.start();
        }
        @Override
        public void onFinish(int what) {

        }
    };

    private void handleLocalUpgradeFail(String prompt){
        //Toast.makeText(LocalUPgradeActivity.this,prompt,Toast.LENGTH_SHORT).show();
        statusTextView.setText(prompt); 
		localUpgradeCouterClear();
        localUpgradeCheckApplyCountClear();
        lvGears.stopAnim();
		finish();
    }
	
    private void handleLocalUpgradeSuccess(String prompt){
        //Toast.makeText(LocalUPgradeActivity.this,prompt,Toast.LENGTH_SHORT).show();
        statusTextView.setText(prompt);        
        localUpgradeCouterClear();
        localUpgradeCheckApplyCountClear();
        lvGears.stopAnim();
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
                handleLocalUpgradeSuccess("上传成功");
            }else if(msg.what == 5){
            	handlelocalUploadFirmware();
            }else if(msg.what == 6){
                handleLocalUpgradeSuccess("升级完成，等待系统重启!");
            }
            super.handleMessage(msg);
        }
    };


    UploadUtil.OnUploadProcessListener onUploadProcessListener = new UploadUtil.OnUploadProcessListener() {
        @Override
        public void onUploadDone(int responseCode, String message) {
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


	 void copyFiles() {
	        try {
				String path = "hojydload";
				String filename = "update.zip";
				sdDir = createSDDir(path);
				file = createFileInSDCard(filename, path);
	            InputStream inStream;
	            inStream = getApplicationContext().getResources().openRawResource(R.raw.update);
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


	private String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); 
        if (sdCardExist){
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.getAbsolutePath() + "/";
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
	
    private void handlelocalUploadFirmware( ){		
		lists = UpgradeFirmwareManager.getInstalce().getLocalFirmwares();
        if (lists.size() > 0){
            listView.setVisibility(View.VISIBLE);
            data = getData();
            adapter = new SimpleAdapter(this,data,R.layout.upgrade_info_item, new String[]{"title","version"},
                    new int[]{R.id.title_textview,R.id.version_textview});
            listView.setAdapter(adapter);
			setListViewOnItemClick();
			uploadFirmware(0);
        }else {
            listView.setVisibility(View.INVISIBLE);
        }
    }

	private void createSafeWindow()
	{

        wm = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        
        
        LayoutParams params = new WindowManager.LayoutParams();

        // 设置window type
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

        params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        // 设置Window flag
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        		| WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.dimAmount = (float)0.8;

        //for V66
        params.x = 0;
        params.y = 0;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        safeView = LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.safe_view_layout, null);
        statusTextView = (TextView)safeView.findViewById(R.id.status);

		wm.addView(safeView, params);
		
	}

	    private void  setListViewOnItemClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(LocalUPgradeActivity.this).setTitle("确认升级此固件?").setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadFirmware(position);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
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
		PowerManager pManager=(PowerManager) getSystemService(Context.POWER_SERVICE);  
    	pManager.reboot("");  
	}

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

    private void runShellCommand(String[] command) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
            int status = p.waitFor();
            HojyLoger.d("LocalUPgradeActivity","status:"+status);
			
            if (status != 0) {
            	return;
            }

   			/*InputStreamReader ir = new InputStreamReader(p.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line;
			do{
				line = input.readLine();

				if(line != null && line.contains("succeeded") ) {
				 break;
		      	}

			}while ((line = input.readLine()) == null);*/


	            //HojyLoger.d("LocalUPgradeActivity","line:"+line);
		
        } catch (Exception e) {
			e.printStackTrace();
        } 
    }

	Thread runShellCommandThread = new Thread(new Runnable() {
		@Override
		public void run() {
			String cmds[]  =  new String[]{"start", "net_dhcp"};		
			runShellCommand(cmds);
		}
	});
}
