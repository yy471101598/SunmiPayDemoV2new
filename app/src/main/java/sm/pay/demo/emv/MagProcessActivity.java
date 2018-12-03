package sm.pay.demo.emv;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.AidlErrorCodeV2;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.Constant;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;
import sm.pay.demo.utils.ByteUtil;
import sm.pay.demo.utils.LoadingDialog;
import sm.pay.demo.utils.LogUtil;

public class MagProcessActivity extends BaseAppCompatActivity {

    private EditText mEditAmount;
    private TextView mTvShowInfo;

    private PinPadOptV2 mPinPadOptV2;
    private ReadCardOptV2 mReadCardOptV2;

    private String mCardNo;
    private LoadingDialog mLoadingDialog;

    private static final int PIN_INIT = 1;
    private static final int PIN_CLICK_NUMBER = 2;
    private static final int PIN_CLICK_PIN = 3;
    private static final int PIN_CLICK_CONFIRM = 4;
    private static final int PIN_CLICK_CANCEL = 5;
    private static final int PIN_ERROR = 6;

    private Looper mLooper = Looper.myLooper();
    private Handler mHandler = new Handler(mLooper) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PIN_INIT:
                    dismissLoadingDialog();
                    initPinPad();
                    break;
                case PIN_CLICK_NUMBER:
                    break;
                case PIN_CLICK_PIN:
                    mockRequestToServer();
                    break;
                case PIN_CLICK_CONFIRM:
                    mockRequestToServer();
                    break;
                case PIN_CLICK_CANCEL:
                    showToast("user cancel");
                    break;
                case PIN_ERROR:
                    showToast("error:" + msg.obj + " -- " + msg.arg1);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emv_ic);
        initView();
        new Thread(
                EmvUtil::initKey
        ).start();
    }

    private void initView() {
        mPinPadOptV2 = MyApplication.mPinPadOptV2;
        mReadCardOptV2 = MyApplication.mReadCardOptV2;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.emv_mag_process);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> onBackPressed()
        );

        mEditAmount = findViewById(R.id.edit_amount);
        mTvShowInfo = findViewById(R.id.tv_info);

        findViewById(R.id.mb_ok).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        cancelCheckCard();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.mb_ok:
                mTvShowInfo.setText("");
                String amount = mEditAmount.getText().toString();
                try {
                    long parseLong = Long.parseLong(amount);
                    if (parseLong > 0) {
                        checkCard();
                    } else {
                        showToast(R.string.card_cost_hint);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(R.string.card_cost_hint);
                }
                break;
        }
    }

    private void checkCard() {
        try {
            showLoadingDialog(R.string.emv_swing_card_mag);
            int cardType = AidlConstantsV2.CardType.MAGNETIC.getValue();
            mReadCardOptV2.checkCard(cardType, mCheckCardCallback, 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelCheckCard() {
        try {
            mReadCardOptV2.cancelCheckCard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPinPad() {
        LogUtil.e(Constant.TAG, "initPinPad");
        try {
            PinPadConfigV2 pinPadConfig = new PinPadConfigV2();
            pinPadConfig.setPinPadType(0);
            pinPadConfig.setPinType(0);
            pinPadConfig.setOrderNumKey(false);
            byte[] panBytes = mCardNo.substring(mCardNo.length() - 13, mCardNo.length() - 1).getBytes("US-ASCII");
            pinPadConfig.setPan(panBytes);
            pinPadConfig.setTimeout(60 * 1000); // input password timeout
            pinPadConfig.setPinKeyIndex(12);    // pik index
            pinPadConfig.setMaxInput(12);
            pinPadConfig.setMinInput(0);
            mPinPadOptV2.initPinPad(pinPadConfig, mPinPadListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mockRequestToServer() {
        new Thread(
                () -> {
                    try {
                        showLoadingDialog(R.string.requesting);
                        Thread.sleep(1500);
                        showToast(R.string.success);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        dismissLoadingDialog();
                    }
                }
        ).start();
    }

    private void showLoadingDialog(int resId) {
        String message = getString(resId);
        runOnUiThread(
                () -> {
                    if (mLoadingDialog == null) {
                        mLoadingDialog = new LoadingDialog(this, message);
                    } else {
                        mLoadingDialog.setMessage(message);
                    }
                    try {
                        mLoadingDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private void dismissLoadingDialog() {
        runOnUiThread(
                () -> {
                    if (mLoadingDialog != null) {
                        try {
                            mLoadingDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    private CheckCardCallbackV2 mCheckCardCallback = new CheckCardCallbackV2.Stub() {

        @Override
        public void findMagCard(Bundle bundle) throws RemoteException {
            LogUtil.e(Constant.TAG, "findMagCard");
            String track1 = bundle.getString("TRACK1");
            String track2 = bundle.getString("TRACK2");
            String track3 = bundle.getString("TRACK3");
            runOnUiThread(
                    () -> {
                        String value = "track1:" + track1 + "\ntrack2:" + track2 + "\ntrack3:" + track3;
                        mTvShowInfo.setText(value);
                    }
            );
            if (track2 != null) {
                int index = track2.indexOf("=");
                if (index != -1) {
                    mCardNo = track2.substring(0, index);
                }
            }
            if (mCardNo != null && mCardNo.length() > 0) {
                mHandler.obtainMessage(PIN_INIT).sendToTarget();
            } else {
                showToast(R.string.emv_card_no_error);
            }
        }

        @Override
        public void findICCard(String atr) throws RemoteException {

        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {

        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            String error = "onError:" + message + " -- " + code;
            LogUtil.e(Constant.TAG, error);
            showToast(error);
            dismissLoadingDialog();
        }

    };

    private PinPadListenerV2 mPinPadListener = new PinPadListenerV2.Stub() {

        @Override
        public void onPinLength(int len) {
            LogUtil.e(Constant.TAG, "onPinLength:" + len);
            mHandler.obtainMessage(PIN_CLICK_NUMBER, len).sendToTarget();
        }

        @Override
        public void onConfirm(int i, byte[] pinBlock) {
            if (pinBlock != null) {
                String hexStr = ByteUtil.bytes2HexStr(pinBlock);
                LogUtil.e(Constant.TAG, "onConfirm pin block:" + hexStr);
                mHandler.obtainMessage(PIN_CLICK_PIN, pinBlock).sendToTarget();
            } else {
                mHandler.obtainMessage(PIN_CLICK_CONFIRM).sendToTarget();
            }
        }

        @Override
        public void onCancel() {
            LogUtil.e(Constant.TAG, "onCancel");
            mHandler.obtainMessage(PIN_CLICK_CANCEL).sendToTarget();
        }

        @Override
        public void onError(int code) {
            LogUtil.e(Constant.TAG, "onError:" + code);
            String msg = AidlErrorCodeV2.valueOf(code).getMsg();
            mHandler.obtainMessage(PIN_ERROR, code, code, msg).sendToTarget();
        }

    };


}
