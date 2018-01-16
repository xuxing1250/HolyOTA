package com.hojy.www.hojyupgrader163.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import android.util.Log;
import android.view.View;
import android.view.Window;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hojy.www.hojyupgrader163.R;
import com.hojy.www.hojyupgrader163.manager.UploadManager;
import com.hojy.www.hojyupgrader163.model.Firmware;

import java.io.File;
import java.util.regex.Pattern;

public class ManualModeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mchoose;
    private Button mStart;
    private TextView mPackFileHint;
    private static final String TAG = "ManualModeActivity";

    private View safeView;
    private WindowManager wm;
    private UploadManager mManager;
    private TextView statusTextView;


    private String mPackFilePath = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * start update
                 */
                case 0:
                    break;
                /**
                 * successed
                 */
                case 1:

                    break;
                /**
                 * failed
                 */
                case 2:
                    break;
                /**
                 * do update
                 * moni quju
                 */
                case 3:
                    Firmware firmware = new Firmware();
                    mManager.uploadFirmware(firmware);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_manual_mode);
        mManager = new UploadManager(this, mHandler);
        mchoose = (Button) findViewById(R.id.btn_packFile);
        mStart = (Button) findViewById(R.id.btn_start);
        mchoose.setOnClickListener(this);
        mStart.setOnClickListener(this);

        mPackFileHint = (TextView) findViewById(R.id.txt_packFileHint);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_packFile:
                loadFilePicker();
                break;
            case R.id.btn_start:
                startLoad();
                break;
        }
    }

    /**
     * load file picker
     */
    private void loadFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("zip/*");
        startActivityForResult(intent, 1);
    }


    private void startLoad() {

        if (mPackFilePath == null) {
            Toast.makeText(ManualModeActivity.this,
                    getResources().getString(R.string.pack_file_hint), Toast.LENGTH_SHORT).show();
            return;
        }

        File packFile = new File(mPackFilePath);
        if (!packFile.exists()) {
            Toast.makeText(ManualModeActivity.this,
                    getResources().getString(R.string.select_pack_file_not_exist), Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * bluetooth mode or power on
         */
//        if (!ModulePowerManger.getInstance().isModuleInBootMode() && !ModulePowerManger.getInstance().isModulePowerOn()) {
//            Toast.makeText(FirmwareToolActivity.this,
//                    getResources().getString(R.string.hint_module_not_power_on), Toast.LENGTH_SHORT).show();
//            return;
//        }

        startToLoad();
    }

    /**
     * start to update
     */
    private void startToLoad() {

        mManager.startCopyThread();
        createSafeWindow();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            // Log.e(TAG, "kjianjun, onResult uri is " + uri.getPath());
            mPackFilePath = uri.getPath();
            Log.d(TAG, "onActivityResult: mPackFilePath-------" + mPackFilePath);
            if (mPackFilePath != null) {
                mPackFileHint.setTextColor(Color.WHITE);
                mPackFileHint.setText(getResources().getString(R.string.pack_file_path) + uri.getPath());
            } else {
                mPackFileHint.setTextColor(Color.RED);
                mPackFileHint.setText(R.string.pack_file_hint);
            }
        }
    }

    private void createSafeWindow()
    {
        wm = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);


        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

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
        statusTextView = (TextView) safeView.findViewById(R.id.status);
        wm.addView(safeView, params);
    }

    @Override
    protected void onDestroy() {
        mManager.onDestroy();
        if (safeView != null) {
            wm.removeViewImmediate(safeView);
        }
        super.onDestroy();
    }

}
