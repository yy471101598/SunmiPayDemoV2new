package sm.pay.demo.pin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sunmi.pay.hardware.aidlv2.AidlErrorCodeV2;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.Constant;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;
import sm.pay.demo.emv.EmvUtil;
import sm.pay.demo.utils.ByteUtil;
import sm.pay.demo.utils.LogUtil;

public class PinPadActivity extends BaseAppCompatActivity {

    private EditText mEditCardNo;
    private EditText mEditTimeout;
    private EditText mEditKeyIndex;

    private TextView mTvInfo;

    private RadioGroup mRGKeyboard;
    private RadioGroup mRGIsOnline;
    private RadioGroup mRGKeyboardStyle;
    private RadioGroup mRGPikKeySystem;
    private RadioGroup mRGPinAlgorithmType;


    private static final int HANDLER_WHAT_INIT_PIN_PAD = 661;
    private static final int HANDLER_PIN_LENGTH = 662;
    private static final int HANDLER_CONFIRM = 663;
    private static final int HANDLER_WHAT_CANCEL = 664;
    private static final int HANDLER_ERROR = 665;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_WHAT_INIT_PIN_PAD:
                    initPinPad();
                    break;
                case HANDLER_WHAT_CANCEL:
                    showToast("user cancel");
                    break;
                case HANDLER_PIN_LENGTH:
                    showToast("inputting");
                    break;
                case HANDLER_CONFIRM:
                    showToast("click ok");
                    break;
                case HANDLER_ERROR:
                    showToast("error:" + msg.obj + " -- " + msg.arg1);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_pad);
        initToolbarBringBack(R.string.pin_pad);
        initView();
        EmvUtil.setTerminalParam();
    }

    private void initView() {
        mEditCardNo = findViewById(R.id.edit_card_no);
        mEditTimeout = findViewById(R.id.edit_timeout);
        mEditKeyIndex = findViewById(R.id.edit_key_index);

        mTvInfo = findViewById(R.id.tv_info);

        mRGKeyboard = findViewById(R.id.rg_keyboard);
        mRGIsOnline = findViewById(R.id.rg_is_online);
        mRGKeyboardStyle = findViewById(R.id.rg_keyboard_style);
        mRGPikKeySystem = findViewById(R.id.key_system);
        mRGPinAlgorithmType = findViewById(R.id.pin_type);

        findViewById(R.id.mb_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.mb_ok:
                mHandler.sendEmptyMessage(HANDLER_WHAT_INIT_PIN_PAD);
                break;
        }
    }

    private void initPinPad() {
        int keyIndex;
        try {
            String index = mEditKeyIndex.getText().toString();
            keyIndex = Integer.parseInt(index);
            if (keyIndex < 0 || keyIndex > 19) {
                showToast(R.string.pin_pad_key_index_hint);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.pin_pad_key_index_hint);
            return;
        }

        int timeout;
        try {
            String time = mEditTimeout.getText().toString();
            timeout = Integer.parseInt(time) * 1000;
            if (timeout < 0 || timeout > 60000) {
                showToast(R.string.pin_pad_timeout_hint);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.pin_pad_timeout_hint);
            return;
        }

        String cardNo = mEditCardNo.getText().toString();
        if (cardNo.trim().length() < 13 || cardNo.trim().length() > 19) {
            showToast(R.string.pin_pad_card_no_hint);
            return;
        }

        try {
            PinPadConfigV2 pinPadConfig = new PinPadConfigV2();

            // 密码键盘类型 0：预置密码键盘(由服务实现样式统一的键盘)  1:调用方自己实现的密码键盘
            pinPadConfig.setPinPadType(mRGKeyboardStyle.getCheckedRadioButtonId() == R.id.rb_preset_keyboard ? 0 : 1);

            // pin类型标识(0是联机pin，1是脱机pin)
            pinPadConfig.setPinType(mRGIsOnline.getCheckedRadioButtonId() == R.id.rb_online_pin ? 0 : 1);

            // true:顺序键盘 false:乱序键盘
            pinPadConfig.setOrderNumKey(mRGKeyboard.getCheckedRadioButtonId() == R.id.rb_orderly_keyboard);

            pinPadConfig.setAlgorithmType(mRGPinAlgorithmType.getCheckedRadioButtonId() == R.id.rb_pin_type1 ? 0 : 1);

            pinPadConfig.setKeySystem(mRGPikKeySystem.getCheckedRadioButtonId() == R.id.rb_key_system1 ? 0 : 1);

            // ascii格式转换成的byte 例如 “123456”.getBytes("us ascii")
            byte[] panBytes = cardNo.substring(cardNo.length() - 13, cardNo.length() - 1).getBytes("US-ASCII");
            pinPadConfig.setPan(panBytes);

            // 超时时间/毫秒
            pinPadConfig.setTimeout(timeout);

            // PIK索引(pin密钥索引)
            pinPadConfig.setPinKeyIndex(keyIndex);

            // 最大输入位数(最多允许输入12位)
            pinPadConfig.setMaxInput(12);

            // 最小输入位数
            pinPadConfig.setMinInput(0);

            MyApplication.mPinPadOptV2.initPinPad(pinPadConfig, mPinPadListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PinPadListenerV2 mPinPadListener = new PinPadListenerV2.Stub() {

        @Override
        public void onPinLength(int i) {
            LogUtil.e(Constant.TAG, "onPinLength:" + i);
            mHandler.obtainMessage(HANDLER_PIN_LENGTH, i, 0).sendToTarget();
        }

        @Override
        public void onConfirm(int i, byte[] bytes) {
            if (bytes != null) {
                String hexStr = ByteUtil.bytes2HexStr(bytes);
                LogUtil.e(Constant.TAG, "onConfirm:" + hexStr);
                runOnUiThread(
                        () -> mTvInfo.setText(hexStr)
                );
                mHandler.obtainMessage(HANDLER_CONFIRM, bytes).sendToTarget();
            } else {
                mHandler.obtainMessage(HANDLER_CONFIRM).sendToTarget();
            }
        }

        @Override
        public void onCancel() {
            LogUtil.e(Constant.TAG, "onCancel");
            mHandler.sendEmptyMessage(HANDLER_WHAT_CANCEL);
        }

        @Override
        public void onError(int code) {
            LogUtil.e(Constant.TAG, "onError:" + code);
            String msg = AidlErrorCodeV2.valueOf(code).getMsg();
            mHandler.obtainMessage(HANDLER_ERROR, code, code, msg).sendToTarget();
        }

    };


}
