package sm.pay.demo.emv;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.pay.hardware.aidl.AidlConstants.EMV.TLVOpCode;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.AidlErrorCodeV2;
import com.sunmi.pay.hardware.aidlv2.bean.EMVTransDataV2;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.Constant;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;
import sm.pay.demo.utils.ByteUtil;
import sm.pay.demo.utils.LoadingDialog;
import sm.pay.demo.utils.LogUtil;

public class ICProcessActivity extends BaseAppCompatActivity {

    private EditText mEditAmount;
    private TextView mTvShowInfo;
    private Button mBtnOperate;

    private EMVOptV2 mEMVOptV2;
    private PinPadOptV2 mPinPadOptV2;
    private ReadCardOptV2 mReadCardOptV2;

    private int mCardType;  // card type
    private String mCardNo; // card number
    private int mPinType;   // 0 - online pin, 1 - offline pin
    private String mCertInfo;
    private int mSelectIndex;

    private int mProcessStep;
    private AlertDialog mAppSelectDialog;
    private LoadingDialog mLoadingDialog;

    private static final int EMV_APP_SELECT = 1;
    private static final int EMV_FINAL_APP_SELECT = 2;
    private static final int EMV_CONFIRM_CARD_NO = 3;
    private static final int EMV_CERT_VERIFY = 4;
    private static final int EMV_SHOW_PIN_PAD = 5;
    private static final int EMV_ONLINE_PROCESS = 6;

    private static final int PIN_CLICK_NUMBER = 7;
    private static final int PIN_CLICK_PIN = 8;
    private static final int PIN_CLICK_CONFIRM = 9;
    private static final int PIN_CLICK_CANCEL = 10;
    private static final int PIN_ERROR = 11;

    private static final int ERROR = 999;

    private Looper mLooper = Looper.myLooper();
    private Handler mHandler = new Handler(mLooper) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EMV_FINAL_APP_SELECT:
                    importFinalAppSelectStatus(0);
                    break;
                case EMV_APP_SELECT:
                    dismissLoadingDialog();
                    List<String> list = (List<String>) msg.obj;
                    int size = list.size();
                    String[] array = list.toArray(new String[size]);
                    mAppSelectDialog = new AlertDialog.Builder(ICProcessActivity.this)
                            .setTitle(R.string.emv_app_select)
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                        importAppSelect(-1);
                                    }
                            )
                            .setPositiveButton(R.string.ok, (dialog, which) -> {
                                        showLoadingDialog(R.string.handling);
                                        importAppSelect(mSelectIndex);
                                    }
                            )
                            .setSingleChoiceItems(array, 0, (dialog, which) -> {
                                        mSelectIndex = which;
                                        LogUtil.e(Constant.TAG, "singleChoiceItems which:" + which);
                                    }
                            ).create();
                    mAppSelectDialog.show();
                    break;
                case EMV_CONFIRM_CARD_NO:
                    dismissLoadingDialog();
                    mTvShowInfo.setText(mCardNo);
                    mBtnOperate.setText(R.string.emv_confirm_card_no);
                    break;
                case EMV_CERT_VERIFY:
                    dismissLoadingDialog();
                    mTvShowInfo.setText(mCertInfo);
                    mBtnOperate.setText(R.string.emv_confirm_cert);
                    break;
                case EMV_SHOW_PIN_PAD:
                    dismissLoadingDialog();
                    initPinPad();
                    break;
                case EMV_ONLINE_PROCESS:
                    mockRequestToServer();
                    break;
                case PIN_CLICK_NUMBER:
                    break;
                case PIN_CLICK_PIN:
                    importPinInputStatus(0);
                    break;
                case PIN_CLICK_CONFIRM:
                    importPinInputStatus(2);
                    break;
                case PIN_CLICK_CANCEL:
                    showToast("user cancel");
                    importPinInputStatus(1);
                    break;
                case PIN_ERROR:
                    showToast("error:" + msg.obj + " -- " + msg.arg1);
                    importPinInputStatus(3);
                    break;
                case ERROR:
                    resetUI();
                    dismissLoadingDialog();
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
                () -> {
                    EmvUtil.initKey();
                    EmvUtil.initAidAndRid();
                    EmvUtil.setTerminalParam();
                }
        ).start();
    }

    private void initView() {
        mEMVOptV2 = MyApplication.mEMVOptV2;
        mPinPadOptV2 = MyApplication.mPinPadOptV2;
        mReadCardOptV2 = MyApplication.mReadCardOptV2;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.emv_ic_process);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> onBackPressed()
        );

        mEditAmount = findViewById(R.id.edit_amount);
        mTvShowInfo = findViewById(R.id.tv_info);
        mBtnOperate = findViewById(R.id.mb_ok);

        findViewById(R.id.mb_ok).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        cancelCheckCard();
        if (mProcessStep == EMV_APP_SELECT) {
            importAppSelect(-1);
        } else if (mProcessStep == EMV_FINAL_APP_SELECT) {
            importFinalAppSelectStatus(-1);
        } else if (mProcessStep == EMV_CONFIRM_CARD_NO) {
            importCardNoStatus(1);
        } else if (mProcessStep == EMV_CERT_VERIFY) {
            importCertStatus(1);
        } else if (mProcessStep == PIN_ERROR) {
            importPinInputStatus(3);
        } else if (mProcessStep == EMV_ONLINE_PROCESS) {
            importOnlineProcessStatus(1);
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.mb_ok:
                if (mProcessStep == 0) {
                    LogUtil.e(Constant.TAG, "***************************************************************");
                    LogUtil.e(Constant.TAG, "****************************Start Process**********************");
                    LogUtil.e(Constant.TAG, "***************************************************************");
                    mTvShowInfo.setText("");
                    String amount = mEditAmount.getText().toString();
                    try {
                        // Before check card, initialize emv process(clear all TLV)
                        mEMVOptV2.initEmvProcess();
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
                } else if (mProcessStep == EMV_CONFIRM_CARD_NO) {
                    showLoadingDialog(R.string.handling);
                    importCardNoStatus(0);
                } else if (mProcessStep == EMV_CERT_VERIFY) {
                    showLoadingDialog(R.string.handling);
                    importCertStatus(0);
                }
                break;
        }
    }

    private void checkCard() {
        try {
            showLoadingDialog(R.string.emv_swing_card_ic);
            int cardType = AidlConstantsV2.CardType.NFC.getValue() |
                    AidlConstantsV2.CardType.IC.getValue();
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
            pinPadConfig.setPinType(mPinType);
            pinPadConfig.setOrderNumKey(false);
            byte[] panBytes = mCardNo.substring(mCardNo.length() - 13, mCardNo.length() - 1).getBytes("US-ASCII");
            pinPadConfig.setPan(panBytes);
            pinPadConfig.setTimeout(60 * 1000); // input password timeout
            pinPadConfig.setPinKeyIndex(12);    // pik index
            pinPadConfig.setMaxInput(12);
            pinPadConfig.setMinInput(0);
            pinPadConfig.setKeySystem(0);
            pinPadConfig.setAlgorithmType(0);
            mPinPadOptV2.initPinPad(pinPadConfig, mPinPadListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEmvTlvDatas() {
        try {
            // set normal tlv data
            String[] tags = {"5F2A", "5F36"};
            String[] values = {"0643", "00"};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tags, values);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void transactProcess() {
        LogUtil.e(Constant.TAG, "transactProcess");
        try {
            EMVTransDataV2 emvTransData = new EMVTransDataV2();
            emvTransData.amount = mEditAmount.getText().toString();
            emvTransData.flowType = 1;
            emvTransData.cardType = mCardType;
            mEMVOptV2.transactProcess(emvTransData, mEMVListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importAppSelect(int selectIndex) {
        LogUtil.e(Constant.TAG, "importAppSelect selectIndex:" + selectIndex);
        try {
            mEMVOptV2.importAppSelect(selectIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importFinalAppSelectStatus(int status) {
        try {
            LogUtil.e(Constant.TAG, "importFinalAppSelectStatus status:" + status);
            mEMVOptV2.importAppFinalSelectStatus(status);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void importCardNoStatus(int status) {
        LogUtil.e(Constant.TAG, "importCardNoStatus status:" + status);
        try {
            mEMVOptV2.importCardNoStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importCertStatus(int status) {
        LogUtil.e(Constant.TAG, "importCertStatus status:" + status);
        try {
            mEMVOptV2.importCertStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importPinInputStatus(int inputResult) {
        LogUtil.e(Constant.TAG, "importPinInputStatus:" + inputResult);
        try {
            String[] tags = {"5F2A", "5F36"};
            byte[] out = new byte[1024];
            int len = mEMVOptV2.getTlvList(TLVOpCode.OP_NORMAL, tags, out);
            if (len < 0) {
                LogUtil.e(Constant.TAG, "getTlvList error,len:" + len);
            } else {
                String hex = ByteUtil.bytes2HexStr(Arrays.copyOf(out, len));
                Map<String, TLV> map = TLVUtil.hexStrToTLVMap(hex);
                LogUtil.e(Constant.TAG, "getTlvList :" + map);
            }
            mEMVOptV2.importPinInputStatus(mPinType, inputResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importOnlineProcessStatus(int status) {
        LogUtil.e(Constant.TAG, "importOnlineProcessStatus status:" + status);
        try {
            mEMVOptV2.importOnlineProcStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mockRequestToServer() {
        new Thread(
                () -> {
                    try {
                        showLoadingDialog(R.string.requesting);
                        if (AidlConstantsV2.CardType.MAGNETIC.getValue() != mCardType) {
                            getTlvData();
                        }
                        Thread.sleep(1500);
                        resetUI();
                        showToast(R.string.success);
                        // notice  ==  import the online result to SDK and end the process.
                        importOnlineProcessStatus(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        resetUI();
                    } finally {
                        dismissLoadingDialog();
                    }
                }
        ).start();
    }

    private void getTlvData() {
        try {
            String[] tagList = {"DF02", "5F34", "9F06", "FF30", "FF31", "95", "9B", "9F36", "9F26",
                    "9F27", "DF31", "5A", "57", "5F24", "9F1A", "9F03", "9F33", "9F10", "9F37", "9C",
                    "9A", "9F02", "5F2A", "82", "9F34", "9F35", "9F1E", "84", "4F", "9F09", "9F41",
                    "9F63", "5F20", "9F12", "50",};
            String[] payPassTags = {"DF811E", "DF812C", "DF8118", "DF8119", "DF811F", "DF8117", "DF8124", "DF8125", "9F6D",
                    "DF811B", "9F53", "DF810C", "9F1D", "DF8130", "DF812D", "DF811C", "DF811D", "9F7C"};
            byte[] outData = new byte[2048];
            Map<String, TLV> map = new HashMap<>();
            int len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tagList, outData);
            if (len > 0) {
                String hexStr = ByteUtil.bytes2HexStr(Arrays.copyOf(outData, len));
                map.putAll(TLVUtil.hexStrToTLVMap(hexStr));
            }
            len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, payPassTags, outData);
            if (len > 0) {
                String hexStr = ByteUtil.bytes2HexStr(Arrays.copyOf(outData, len));
                map.putAll(TLVUtil.hexStrToTLVMap(hexStr));
            }

            runOnUiThread(
                    () -> {
                        String temp = "";
                        Set<String> set = map.keySet();
                        for (String key : set) {
                            TLV tlv = map.get(key);
                            if (tlv != null) {
                                temp += key + ":" + tlv.value + "\n";
                            } else {
                                temp += key + ":" + "\n";
                            }
                        }
                        mTvShowInfo.setText(temp);
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CheckCardCallbackV2 mCheckCardCallback = new CheckCardCallbackV2.Stub() {

        @Override
        public void findMagCard(Bundle bundle) throws RemoteException {

        }

        @Override
        public void findICCard(String atr) throws RemoteException {
            LogUtil.e(Constant.TAG, "findICCard:" + atr);
            mCardType = AidlConstantsV2.CardType.IC.getValue();
//            initEmvTlvDatas();
            transactProcess();
        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {
            LogUtil.e(Constant.TAG, "findRFCard:" + uuid);
            mCardType = AidlConstantsV2.CardType.NFC.getValue();
//            initEmvTlvDatas();
            transactProcess();
        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            String error = "onError:" + message + " -- " + code;
            LogUtil.e(Constant.TAG, error);
            showToast(error);
            dismissLoadingDialog();
        }

    };

    private EMVListenerV2 mEMVListener = new EMVListenerV2.Stub() {

        @Override
        public void onWaitAppSelect(List<String> appNameList, boolean isFirstSelect) throws RemoteException {
            LogUtil.e(Constant.TAG, "onWaitAppSelect isFirstSelect:" + isFirstSelect);
            mProcessStep = EMV_APP_SELECT;
            mHandler.obtainMessage(EMV_APP_SELECT, appNameList).sendToTarget();
        }

        @Override
        public void onAppFinalSelect(String tag9F06value) throws RemoteException {
            LogUtil.e(Constant.TAG, "onAppFinalSelect tag9F06value:" + tag9F06value);
            initEmvTlvDatas();
            if (!TextUtils.isEmpty(tag9F06value)) {
                if (tag9F06value.startsWith("A000000003")) {//VISA(PayWave)
                    LogUtil.e(Constant.TAG, "detect VISA card");
                    // set PayWave tlv data
                    String[] tagsPayWave = {"DF8124", "DF8125", "DF8126"};
                    String[] valuesPayWave = {"999999999999", "999999999999", "000000000000"};
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE, tagsPayWave, valuesPayWave);
                } else if (tag9F06value.startsWith("A000000004")) {//MasterCard(PayPass)
                    LogUtil.e(Constant.TAG, "detect MasterCard card");
                    // set PayPass tlv data
                    String[] tagsPayPass = {"DF8124", "DF8125", "DF8126"};
                    String[] valuesPayPass = {"999999999999", "999999999999", "000000000000"};
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, tagsPayPass, valuesPayPass);
                } else if (tag9F06value.startsWith("A000000333")) {//UnionPay
                    LogUtil.e(Constant.TAG, "detect UnionPay card");
                }
            }
            mProcessStep = EMV_FINAL_APP_SELECT;
            mHandler.obtainMessage(EMV_FINAL_APP_SELECT, tag9F06value).sendToTarget();
        }

        @Override
        public void onConfirmCardNo(String cardNo) throws RemoteException {
            LogUtil.e(Constant.TAG, "onConfirmCardNo cardNo:" + cardNo);
            mCardNo = cardNo;
            mProcessStep = EMV_CONFIRM_CARD_NO;
            mHandler.obtainMessage(EMV_CONFIRM_CARD_NO).sendToTarget();
        }

        @Override
        public void onRequestShowPinPad(int pinType, int remainTime) throws RemoteException {
            LogUtil.e(Constant.TAG, "onRequestShowPinPad pinType:" + pinType + " remainTime:" + remainTime);
            mPinType = pinType;
            mProcessStep = EMV_SHOW_PIN_PAD;
            mHandler.obtainMessage(EMV_SHOW_PIN_PAD).sendToTarget();
        }

        @Override
        public void onCertVerify(String certType, String certInfo) throws RemoteException {
            LogUtil.e(Constant.TAG, "onCertVerify certType:" + certType + " certInfo:" + certInfo);
            mCertInfo = certInfo;
            mProcessStep = EMV_CERT_VERIFY;
            mHandler.obtainMessage(EMV_CERT_VERIFY).sendToTarget();
        }

        @Override
        public void onOnlineProc() throws RemoteException {
            LogUtil.e(Constant.TAG, "onOnlineProcess");
            mProcessStep = EMV_ONLINE_PROCESS;
            mHandler.obtainMessage(EMV_ONLINE_PROCESS).sendToTarget();
        }

        @Override
        public void onTransResult(int code, String desc) throws RemoteException {
            LogUtil.e(Constant.TAG, "onTransResult code:" + code + " desc:" + desc);
            LogUtil.e(Constant.TAG, "***************************************************************");
            LogUtil.e(Constant.TAG, "****************************End Process************************");
            LogUtil.e(Constant.TAG, "***************************************************************");
            if (code != 0) {
                mHandler.obtainMessage(ERROR, code, code, desc).sendToTarget();
            }
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

    private void resetUI() {
        runOnUiThread(
                () -> {
                    mProcessStep = 0;
                    mEditAmount.setText("");
                    mBtnOperate.setText(R.string.ok);
                    dismissLoadingDialog();
                    dismissAppSelectDialog();
                }
        );
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

    private void dismissAppSelectDialog() {
        runOnUiThread(
                () -> {
                    if (mAppSelectDialog != null) {
                        try {
                            mAppSelectDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mAppSelectDialog = null;
                    }
                }
        );
    }


}
