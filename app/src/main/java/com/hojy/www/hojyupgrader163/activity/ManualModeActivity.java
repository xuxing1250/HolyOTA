package com.hojy.www.hojyupgrader163.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hojy.www.hojyupgrader163.R;

import java.io.File;
import java.util.regex.Pattern;

public class ManualModeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mchoose;
    private Button mStart;
    private TextView mPackFileHint;
    private static final String TAG = "ManualModeActivity";


    private String mPackFilePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_manual_mode);
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

}
