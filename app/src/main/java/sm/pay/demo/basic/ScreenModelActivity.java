package sm.pay.demo.basic;

import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;

public class ScreenModelActivity extends BaseAppCompatActivity {

    private MaterialButton mMaterialButton;

    private int mScreenExclusive = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_screen_model);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.basic_screen_mode);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> onBackPressed()
        );

        mMaterialButton = findViewById(R.id.mb_ok);
        mMaterialButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.mb_ok:
                screenExclusive();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mScreenExclusive = -1;
        screenExclusive();
        super.onBackPressed();
    }

    private void screenExclusive() {
        try {
            MyApplication.mBasicOptV2.setScreenMode(mScreenExclusive);
            mScreenExclusive = -mScreenExclusive;
            switch (mScreenExclusive) {
                case -1:
                    mMaterialButton.setText(R.string.basic_screen_model_close);
                    break;
                case 1:
                    mMaterialButton.setText(R.string.basic_screen_model_open);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
