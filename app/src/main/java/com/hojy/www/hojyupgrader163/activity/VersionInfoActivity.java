package com.hojy.www.hojyupgrader163.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hojy.www.hojyupgrader163.R;
import com.hojy.www.hojyupgrader163.model.UpgradeInfo;
import com.hojy.www.hojyupgrader163.network.HojyRequestManager;
import com.hojy.www.hojyupgrader163.utils.HojyLoger;
import com.hojy.www.hojyupgrader163.utils.XMLDomService;
import com.hojy.www.hojyupgrader163.view.LVCircularCD;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Response;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class VersionInfoActivity extends Activity {

    private  int newpackage;
    private  String version;
    private  String desc;

    private TextView promptTextView;
    private TextView versionTextView;
    private TextView descTextView;
    private Button   applyButton;
    private LVCircularCD lvCircularCD;

    private UpgradeInfo upgradeInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_info);

        promptTextView  = (TextView) findViewById(R.id.upgrade_info_prompt_textview);
        versionTextView = (TextView) findViewById(R.id.upgrade_info_version);
        descTextView    = (TextView) findViewById(R.id.upgrade_info_description);
        applyButton     = (Button)  findViewById(R.id.upgrade_info_apply_button);
        lvCircularCD    = (LVCircularCD) findViewById(R.id.upgrade_info_lv_circularcd);

        Intent intent = getIntent();
        newpackage = intent.getIntExtra("newpackage",0);
        version    = intent.getStringExtra("version");
        desc       = intent.getStringExtra("description");

        if(newpackage == 1){
            promptTextView.setText("可升级版本");
        }else {
            promptTextView.setText("当前版本");
            applyButton.setVisibility(View.GONE);
        }

        versionTextView.setText(version);
        descTextView.setText(desc);

        setLinstener();

    }


    private void setLinstener(){
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HojyLoger.d("VersionInfoActivity","applyButton onClick -> begin download package");
                //开始下载升级包
                HojyRequestManager.getInstalce().requestDownloadUpgradePackgeFromServer(null);
                //查询下载结果
                HojyLoger.d("VersionInfoActivity","applyButton onClick -> begin check download state");
                HojyRequestManager.getInstalce().requestCheckUpgradeInfoFromServer(OtaDownloadStateListener);

                applyButton.setEnabled(false);
                lvCircularCD.startAnim();
            }
        });
    }

    private OnResponseListener<String> OtaDownloadStateListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {

            String result = response.get();
            try {
                result = new String(result.getBytes("utf-8"));
                HojyLoger.d("VersionInfoActivity","OtaDownloadStateListener onSucceed -> result:"+result);
            } catch (UnsupportedEncodingException e) {
                HojyLoger.d("VersionInfoActivity","OtaDownloadStateListener onSucceed -> exception:"+e.getMessage());
                e.printStackTrace();
                return;
            }

            upgradeInfo = XMLDomService.parseDownloadXml(result);
            Message message = new Message();
            if(upgradeInfo.getDownloadErro() == 1){
                //正在下载
                message.what = 1;
                handler.sendMessageDelayed(message,1000);
            }else if (upgradeInfo.getDownloadErro() == 2){
                //下载完成，开始升级
                message.what = 20;
                handler.sendMessage(message);
            }else {
                handleUpgradeFail("下载升级包失败!");
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            HojyLoger.d("VersionInfoActivity","OtaDownloadStateListener onFailed -> response:"+response);
            handleUpgradeFail("下载失败！");
        }

        @Override
        public void onFinish(int what) {
        }
    };
    private OnResponseListener<String> OtaApplyStateListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            handleUpgradeSuccess("开始升级...");
        }
        @Override
        public void onFailed(int what, Response<String> response) {
            handleUpgradeFail("升级失败！");
        }

        @Override
        public void onFinish(int what) {
        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                HojyRequestManager.getInstalce().requestCheckUpgradeInfoFromServer(OtaDownloadStateListener);
            }else if(msg.what == 20){
                //发起升级指令
                HojyRequestManager.getInstalce().requestApplyUpgradePackage(OtaApplyStateListener);
            }
            super.handleMessage(msg);
        }
    };

    private void handleUpgradeFail(String prompt){
        Toast.makeText(VersionInfoActivity.this,prompt,Toast.LENGTH_SHORT).show();
        applyButton.setEnabled(true);
        lvCircularCD.stopAnim();
    }

    private void handleUpgradeSuccess(String prompt){
        Toast.makeText(VersionInfoActivity.this,prompt,Toast.LENGTH_SHORT).show();
        applyButton.setEnabled(true);
        lvCircularCD.stopAnim();
        finish();
    }
}
