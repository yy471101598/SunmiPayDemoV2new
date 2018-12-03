package sm.pay.demo.other;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.CacheHelper;
import sm.pay.demo.Constant;
import sm.pay.demo.MainActivity;
import sm.pay.demo.R;

public class LanguageActivity extends BaseAppCompatActivity {

    private int mCurrentLanguage;

    private RadioButton mRbAuto;
    private RadioButton mRbZH_CN;
    private RadioButton mRbEN_US;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_language);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.other_language);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> onBackPressed()
        );

        mRbAuto = findViewById(R.id.rb_auto);
        mRbZH_CN = findViewById(R.id.rb_zh_cn);
        mRbEN_US = findViewById(R.id.rb_en_us);

        findViewById(R.id.item_auto).setOnClickListener(this);
        findViewById(R.id.item_zh_cn).setOnClickListener(this);
        findViewById(R.id.item_en_us).setOnClickListener(this);

        reset();
        mCurrentLanguage = CacheHelper.getCurrentLanguage();
        switch (mCurrentLanguage) {
            case Constant.LANGUAGE_CH_CN:
                mRbZH_CN.setChecked(true);
                break;
            case Constant.LANGUAGE_EN_US:
                mRbEN_US.setChecked(true);
                break;
            default:
                mRbAuto.setChecked(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (CacheHelper.getCurrentLanguage() != mCurrentLanguage) {
            MainActivity.reStart(this);
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.item_auto:
                reset();
                mRbAuto.setChecked(true);
                CacheHelper.saveCurrentLanguage(Constant.LANGUAGE_AUTO);
                break;
            case R.id.item_zh_cn:
                reset();
                mRbZH_CN.setChecked(true);
                CacheHelper.saveCurrentLanguage(Constant.LANGUAGE_CH_CN);
                break;
            case R.id.item_en_us:
                reset();
                mRbEN_US.setChecked(true);
                CacheHelper.saveCurrentLanguage(Constant.LANGUAGE_EN_US);
                break;
        }
    }

    private void reset() {
        mRbAuto.setChecked(false);
        mRbZH_CN.setChecked(false);
        mRbEN_US.setChecked(false);
    }


}
