package sm.pay.demo.card;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.Constant;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;
import sm.pay.demo.utils.LogUtil;

public class ICActivity extends BaseAppCompatActivity {

    private TextView mTvUUID;
    private TextView mTvATR;

    private ReadCardOptV2 mReadCardOptV2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_ic);
        initView();
        checkCard();
    }

    private void initView() {
        mReadCardOptV2 = MyApplication.mReadCardOptV2;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.card_test_ic);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> onBackPressed()
        );

        mTvUUID = findViewById(R.id.tv_uuid);
        mTvATR = findViewById(R.id.tv_atr);
    }

    @Override
    public void onBackPressed() {
        cancelCheckCard();
        super.onBackPressed();
    }

    private void checkCard() {
        try {
            int cardType = AidlConstantsV2.CardType.NFC.getValue() | AidlConstantsV2.CardType.IC.getValue();
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

    private CheckCardCallbackV2 mCheckCardCallback = new CheckCardCallbackV2.Stub() {

        @Override
        public void findMagCard(Bundle bundle) throws RemoteException {

        }

        @Override
        public void findICCard(String atr) throws RemoteException {
            LogUtil.e(Constant.TAG, "findICCard:" + atr);
            handleResult(false, atr);
        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {
            LogUtil.e(Constant.TAG, "findRFCard:" + uuid);
            handleResult(true, uuid);
        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            String error = "onError:" + message + " -- " + code;
            LogUtil.e(Constant.TAG, error);
            showToast(error);
            handleResult(false, "");
        }

    };

    private void handleResult(boolean nfc, String value) {
        runOnUiThread(
                () -> {
                    if (nfc) {
                        String temp = getString(R.string.card_uuid) + " " + value;
                        mTvUUID.setText(temp);
                        mTvATR.setText(R.string.card_atr);
                    } else {
                        mTvUUID.setText(R.string.card_uuid);
                        String temp = getString(R.string.card_atr) + " " + value;
                        mTvATR.setText(temp);
                    }
                    // 继续检卡
                    checkCard();
                }
        );
    }


}
