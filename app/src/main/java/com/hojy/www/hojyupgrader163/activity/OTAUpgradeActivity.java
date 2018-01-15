package com.hojy.www.hojyupgrader163.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hojy.www.hojyupgrader163.R;
import com.hojy.www.hojyupgrader163.model.HojyDevice;
import com.hojy.www.hojyupgrader163.model.UpgradeInfo;
import com.hojy.www.hojyupgrader163.network.HojyRequestManager;
import com.hojy.www.hojyupgrader163.utils.HojyLoger;
import com.hojy.www.hojyupgrader163.utils.XMLDomService;
import com.hojy.www.hojyupgrader163.view.LVCircularCD;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Response;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OTAUpgradeActivity extends Activity {

    private LVCircularCD lvCircularCD;
    private ListView listView;
    private Button checkUpgradeButton;
    private TextView textView;
    private UpgradeInfo upgradeInfo;
    private HojyDevice hojyDevice;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> data;
    private String currentFirmwareVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otaupgrade);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("hojy_device",MODE_PRIVATE);
        currentFirmwareVersion = sharedPreferences.getString("firmware_version","");

        upgradeInfo = null;
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        data = getData();
        adapter.notifyDataSetChanged();
    }

    private void initViews(){
        lvCircularCD = (LVCircularCD) findViewById(R.id.lv_circularcd);
        listView     = (ListView) findViewById(R.id.info_listview);
        checkUpgradeButton = (Button) findViewById(R.id.check_upgrade_button);
        textView     = (TextView) findViewById(R.id.prompt_textview);
        initListView();
        setListener();
    }

    private void  initListView(){
        data = getData();
        adapter = new SimpleAdapter(this,data,R.layout.upgrade_info_item, new String[]{"title","version"},
                new int[]{R.id.title_textview,R.id.version_textview});
        listView.setAdapter(adapter);
    }


    private void setListener(){

        HojyRequestManager.getInstalce().requestStatus1(status1Listener);

        checkUpgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpgradeButton.setEnabled(false);
                checkUpgradeButton.setText("正在检查...");
                lvCircularCD.startAnim();
                HojyLoger.d("OTAUpgradeActivity","checkUpgradeButton onClick -> begin check upgrade");
                HojyRequestManager.getInstalce().requestCheckUpgradeInfoFromServer(checkUpgradeListener);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(OTAUpgradeActivity.this,VersionInfoActivity.class);
                if(0 == position && data.size() > 1){
                    intent.putExtra("newpackage",upgradeInfo.getNewPackage());
                    intent.putExtra("version",upgradeInfo.getPackageVersion());
                    intent.putExtra("description",upgradeInfo.getPackageDescCn());
                    startActivity(intent);
                }else {
                    intent.putExtra("newpackage",0);
                    intent.putExtra("version",currentFirmwareVersion);
                    intent.putExtra("description","无升级日志");
                    startActivity(intent);
                }
            }
        });
    }

    private OnResponseListener<String> checkUpgradeListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            try {
                result = new String(result.getBytes("utf-8"));
                HojyLoger.d("OTAUpgradeActivity","checkUpgradeListener onSucceed -> result:"+result);
            } catch (UnsupportedEncodingException e) {
                HojyLoger.d("OTAUpgradeActivity","checkUpgradeListener onSucceed -> exception:"+e.getMessage());
                e.printStackTrace();
                return;
            }

            upgradeInfo = XMLDomService.parseQueryXml(result);
            if(upgradeInfo.getNewPackage() == 1){
                data = getData();
                adapter.notifyDataSetChanged();
                textView.setText("有新版本可升级");
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            HojyLoger.d("OTAUpgradeActivity","checkUpgradeListener onFailed -> response:"+response);
            lvCircularCD.stopAnim();
        }

        @Override
        public void onFinish(int what) {
            lvCircularCD.stopAnim();
            checkUpgradeButton.setEnabled(true);
            checkUpgradeButton.setText("检查更新");
        }
    };

    private OnResponseListener<String> status1Listener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            try {
                result = new String(result.getBytes("utf-8"));
                HojyLoger.d("OTAUpgradeActivity","status1Listener onSucceed -> result:"+result);
            } catch (UnsupportedEncodingException e) {
                HojyLoger.d("OTAUpgradeActivity","status1Listener onSucceed -> exception:"+e.getMessage());
                e.printStackTrace();
                return;
            }
            hojyDevice = XMLDomService.paraseDeviceInfo(result);

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("hojy_device",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("firmware_version",hojyDevice.getFirmwareVersion());
            editor.putString("firmware_version_date",hojyDevice.getFirmwareVersionDate());
            editor.putString("hardware_version",hojyDevice.getHardwareVersion());
            editor.commit();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            HojyLoger.d("OTAUpgradeActivity","status1Listener onSucceed -> response:"+response);
        }

        @Override
        public void onFinish(int what) {

        }
    };

    private List<Map<String, Object>> getData() {
        if(data == null){
            data  = new ArrayList<Map<String, Object>>();
        }

        data.clear();

        Map<String, Object> map = new HashMap<String, Object>();

        if(upgradeInfo != null){
            map.put("title", "可升级版本");
            map.put("version", upgradeInfo.getPackageVersion());
            data.add(map);
        }

        map = new HashMap<String, Object>();
        map.put("title", "当前版本");
        map.put("version", currentFirmwareVersion);
        data.add(map);
        return data;
    }
}
