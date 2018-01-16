package com.hojy.www.hojyupgrader163.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import android.view.WindowManager;
import android.content.Context;
import android.view.WindowManager.LayoutParams;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;

import com.hojy.www.hojyupgrader163.R;
import com.hojy.www.hojyupgrader163.manager.UploadManager;
import com.hojy.www.hojyupgrader163.model.Firmware;
import com.hojy.www.hojyupgrader163.utils.UpgradeFirmwareManager;
import com.hojy.www.hojyupgrader163.view.LVGears;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class LocalUPgradeActivity extends Activity {
    private List<Firmware> lists;
    private List<Map<String, Object>> data;
    private SimpleAdapter adapter;
    private ListView listView;
    private LVGears lvGears;

    private TextView promptTextView;
	private View safeView;
	private TextView statusTextView;
	private WindowManager wm;
    private UploadManager mManager;

    private static final String TAG = "LocalUPgradeActivity";

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    lvGears.startAnim();
                    promptTextView.setText("正在升级，请勿断电!");
                    break;
                case 1:
                    String success = (String) msg.obj;
                    statusTextView.setText(success);
                    lvGears.stopAnim();
                    break;
                case 2:
                    String failed = (String) msg.obj;
                    statusTextView.setText(failed);
                    lvGears.stopAnim();
                    finish();
                    break;
                case 3:


                    /**
                     * kaishi chulishuju
                     */
                    lists = UpgradeFirmwareManager.getInstalce().getLocalFirmwares();
                    if (lists.size() > 0){
                        listView.setVisibility(View.VISIBLE);
                        data = getData();
                        adapter = new SimpleAdapter(LocalUPgradeActivity.this,data,R.layout.upgrade_info_item, new String[]{"title","version"},
                                new int[]{R.id.title_textview,R.id.version_textview});
                        listView.setAdapter(adapter);
                        setListViewOnItemClick();

                        /**
                         * he xin daima
                         */
                        mManager.uploadFirmware(lists.get(0));
                    }else {
                        listView.setVisibility(View.INVISIBLE);
                    }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_upgrade);
        listView = (ListView) findViewById(R.id.local_upgrade_package_listview);
        lvGears = (LVGears) findViewById(R.id.local_upgrade_lv_circularcd);
        promptTextView = (TextView) findViewById(R.id.local_upgrade_prompt_textview);
        mManager = new UploadManager(this, mHander);
		mManager.startCopyThread();

		createSafeWindow();
	    lists = UpgradeFirmwareManager.getInstalce().getLocalFirmwares();

        Log.d(TAG, "onCreate: ----------------------------");
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
        mManager.onDestroy();
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

                                Firmware firmware = lists.get(position);
                                mManager.uploadFirmware(firmware);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
    }







}
