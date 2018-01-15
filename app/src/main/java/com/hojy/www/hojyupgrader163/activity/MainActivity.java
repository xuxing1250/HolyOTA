package com.hojy.www.hojyupgrader163.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hojy.www.hojyupgrader163.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;



public class MainActivity extends AppCompatActivity {

    ListView listView;
	File file = null;
				File sdDir = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.upgrade_mode_listview);
        SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.upgrade_mode_listview_item, new String[]{"icon","content"},
                new int[]{R.id.icon_item_imageview,R.id.content_item_textview});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    Intent intent = new Intent(MainActivity.this,OTAUpgradeActivity.class);
                    startActivity(intent);
                }else if(position == 1){
                    Intent intent = new Intent(MainActivity.this,LocalUPgradeActivity.class);
                    startActivity(intent);
                }
            }
        });
		thread.start();
    }
	
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("icon", R.drawable.refresh);
        map.put("content", "OTA在线升级");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("icon", R.drawable.storage);
        map.put("content", "本地升级");
        list.add(map);


        return list;
    }

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

	   Thread thread = new Thread(new Runnable() {
		
		@Override
		public void run() {

			copyFiles();
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
	
}
