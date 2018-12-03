package sm.pay.demo.card;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.Constant;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;
import sm.pay.demo.utils.LogUtil;

public class MagActivity extends BaseAppCompatActivity {

    private MaterialButton mBtnTotal;
    private MaterialButton mBtnSuccess;
    private MaterialButton mBtnFail;

    private TextView mTvTrack1;
    private TextView mTvTrack2;
    private TextView mTvTrack3;

    private ReadCardOptV2 mReadCardOptV2;

    private int mTotalTime;
    private int mSuccessTime;
    private int mFailTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_mag);
        initView();
        checkCard();
    }

    private void initView() {
        mReadCardOptV2 = MyApplication.mReadCardOptV2;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.card_test_mag);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> onBackPressed()
        );

        mBtnTotal = findViewById(R.id.mb_total);
        mBtnSuccess = findViewById(R.id.mb_success);
        mBtnFail = findViewById(R.id.mb_fail);

        mTvTrack1 = findViewById(R.id.tv_track1);
        mTvTrack2 = findViewById(R.id.tv_track2);
        mTvTrack3 = findViewById(R.id.tv_track3);
    }

    @Override
    public void onBackPressed() {
        cancelCheckCard();
        super.onBackPressed();
    }

    /**
     * start check card
     */
    private void checkCard() {
        try {
            mReadCardOptV2.checkCard(AidlConstantsV2.CardType.MAGNETIC.getValue(), mCheckCardCallback, 60);
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
            LogUtil.e(Constant.TAG, "findMagCard");
            handleResult(bundle);
        }

        @Override
        public void findICCard(String s) throws RemoteException {

        }

        @Override
        public void findRFCard(String s) throws RemoteException {

        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            String error = "onError:" + message + " -- " + code;
            LogUtil.e(Constant.TAG, error);
            showToast(error);
            handleResult(null);
        }

    };

    private void handleResult(Bundle bundle) {
        runOnUiThread(
                () -> {
                    if (bundle == null) {
                        failed();
                    } else {
                        String track1 = bundle.getString("TRACK1");
                        String track2 = bundle.getString("TRACK2");
                        String track3 = bundle.getString("TRACK3");
                        boolean isEmpty = TextUtils.isEmpty(track1) && TextUtils.isEmpty(track2) && TextUtils.isEmpty(track3);
                        if (isEmpty) {
                            failed();
                        } else {
                            success(track1, track2, track3);
                        }
                    }
                    // 继续检卡
                    checkCard();
                }
        );
    }

    private void failed() {
        mTotalTime += 1;
        mFailTime += 1;

        mTvTrack1.setText(R.string.card_track1);
        mTvTrack2.setText(R.string.card_track2);
        mTvTrack3.setText(R.string.card_track3);

        String temp = getString(R.string.card_total) + " " + mTotalTime;
        mBtnTotal.setText(temp);
        temp = getString(R.string.card_success) + " " + mSuccessTime;
        mBtnSuccess.setText(temp);
        temp = getString(R.string.card_fail) + " " + mFailTime;
        mBtnFail.setText(temp);
    }

    private void success(String track1, String track2, String track3) {
        mTotalTime += 1;
        mSuccessTime += 1;

        String temp = getString(R.string.card_track1) + " " + track1;
        mTvTrack1.setText(temp);
        temp = getString(R.string.card_track2) + " " + track2;
        mTvTrack2.setText(temp);
        temp = getString(R.string.card_track3) + " " + track3;
        mTvTrack3.setText(temp);

        temp = getString(R.string.card_total) + " " + mTotalTime;
        mBtnTotal.setText(temp);
        temp = getString(R.string.card_success) + " " + mSuccessTime;
        mBtnSuccess.setText(temp);
        temp = getString(R.string.card_fail) + " " + mFailTime;
        mBtnFail.setText(temp);
    }


}
