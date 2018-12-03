package sm.pay.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import sm.pay.demo.basic.BasicActivity;
import sm.pay.demo.card.CardActivity;
import sm.pay.demo.emv.EMVActivity;
import sm.pay.demo.other.OtherActivity;
import sm.pay.demo.other.VersionActivity;
import sm.pay.demo.pin.PinPadActivity;
import sm.pay.demo.security.SecurityActivity;
import sm.pay.demo.utils.LogUtil;
import sunmi.paylib.SunmiPayKernel;

public class MainActivity extends BaseAppCompatActivity {

    private SunmiPayKernel mSMPayKernel;

    private boolean isDisConnectService = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        connectPayService();
    }

    private void initView() {
        findViewById(R.id.card_view_basic).setOnClickListener(this);
        findViewById(R.id.card_view_card).setOnClickListener(this);
        findViewById(R.id.card_view_pin_pad).setOnClickListener(this);
        findViewById(R.id.card_view_security).setOnClickListener(this);
        findViewById(R.id.card_view_emv).setOnClickListener(this);
        findViewById(R.id.card_view_version).setOnClickListener(this);
        findViewById(R.id.card_view_other).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isDisConnectService) {
            connectPayService();
            showToast(R.string.connect_loading);
            return;
        }
        final int id = v.getId();
        switch (id) {
            case R.id.card_view_basic:
                openActivity(BasicActivity.class);
                break;
            case R.id.card_view_card:
                openActivity(CardActivity.class);
                break;
            case R.id.card_view_pin_pad:
                openActivity(PinPadActivity.class);
                break;
            case R.id.card_view_security:
                openActivity(SecurityActivity.class);
                break;
            case R.id.card_view_emv:
                openActivity(EMVActivity.class);
                break;
            case R.id.card_view_version:
                openActivity(VersionActivity.class);
                break;
            case R.id.card_view_other:
                openActivity(OtherActivity.class);
                break;
        }
    }

    private void connectPayService() {
        mSMPayKernel = SunmiPayKernel.getInstance();
        mSMPayKernel.initPaySDK(this, mConnectCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSMPayKernel != null) {
            mSMPayKernel.destroyPaySDK();
        }
    }

    /**
     * 连接状态回调
     */
    private SunmiPayKernel.ConnectCallback mConnectCallback = new SunmiPayKernel.ConnectCallback() {

        @Override
        public void onConnectPaySDK() {
            LogUtil.e(Constant.TAG, "onConnectPaySDK");
            try {
                MyApplication.mEMVOptV2 = mSMPayKernel.mEMVOptV2;
                MyApplication.mBasicOptV2 = mSMPayKernel.mBasicOptV2;
                MyApplication.mPinPadOptV2 = mSMPayKernel.mPinPadOptV2;
                MyApplication.mReadCardOptV2 = mSMPayKernel.mReadCardOptV2;
                MyApplication.mSecurityOptV2 = mSMPayKernel.mSecurityOptV2;
                isDisConnectService = false;
                showToast(R.string.connect_success);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnectPaySDK() {
            LogUtil.e(Constant.TAG, "onDisconnectPaySDK");
            isDisConnectService = true;
            showToast(R.string.connect_fail);
        }

    };

    public static void reStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
