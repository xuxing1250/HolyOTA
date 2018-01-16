package com.hojy.www.hojyupgrader163.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import com.hojy.www.hojyupgrader163.R;

public class ModeChooseActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mAutoMode;
    private Button mManualMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_choose);
        mAutoMode = (Button) findViewById(R.id.bt_auto);
        mManualMode = (Button) findViewById(R.id.bt_manual);
        mAutoMode.setOnClickListener(this);
        mManualMode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_auto:
                loadAutoMode();
                break;
            case R.id.bt_manual:
                loadManualMode();
                break;
        }
    }

    /**
     * Manual Mode
     */
    private void loadManualMode() {
        Intent intent = new Intent(ModeChooseActivity.this, ManualModeActivity.class);
        startActivity(intent);
    }

    /**
     * Auto Mode
     */
    private void loadAutoMode() {
        Intent intent = new Intent(ModeChooseActivity.this, LocalUPgradeActivity.class);
        startActivity(intent);
    }
}
