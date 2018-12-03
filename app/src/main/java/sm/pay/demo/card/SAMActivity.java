package sm.pay.demo.card;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.bean.ApduRecvV2;
import com.sunmi.pay.hardware.aidlv2.bean.ApduSendV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;

import java.util.Arrays;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.Constant;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;
import sm.pay.demo.utils.ByteUtil;
import sm.pay.demo.utils.LogUtil;

public class SAMActivity extends BaseAppCompatActivity {

    private EditText mEditCommand;
    private EditText mEditData;
    private EditText mEditLc;
    private EditText mEditLe;
    private TextView mTvResultInfo;

    private ReadCardOptV2 mReadCardOptV2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_sam);
        initView();
        checkCard();
    }

    private void initView() {
        mReadCardOptV2 = MyApplication.mReadCardOptV2;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.card_test_sam);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> onBackPressed()
        );

        mEditCommand = findViewById(R.id.edit_command);
        mEditData = findViewById(R.id.edit_data);
        mEditLc = findViewById(R.id.edit_lc_length);
        mEditLe = findViewById(R.id.edit_le_length);
        mTvResultInfo = findViewById(R.id.tv_info);

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
                sendCommand();
                break;
        }
    }

    private void checkCard() {
        try {
            mReadCardOptV2.checkCard(AidlConstantsV2.CardType.PSAM0.getValue(), mCheckCardCallback, 20);
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
        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {

        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            String error = "onError:" + message + " -- " + code;
            LogUtil.e(Constant.TAG, error);
            showToast(error);
        }

    };

    // Recommend
    private void sendCommand() {
        String commandStr = mEditCommand.getText().toString();
        String lcStr = mEditLc.getText().toString();
        String leStr = mEditLe.getText().toString();
        String dataStr = mEditData.getText().toString();

        if (commandStr.length() != 8) {
            showToast(R.string.card_command_hint);
            return;
        }

        short lc;
        try {
            lc = Short.parseShort(lcStr);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.card_lc_length_hint);
            return;
        }

        if (dataStr.length() == 0) {
            showToast(R.string.card_data_hint);
            return;
        }

        short le;
        try {
            le = Short.parseShort(leStr);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.card_le_length_hint);
            return;
        }

        dataStr = dataStr.toLowerCase();
        commandStr = commandStr.toUpperCase();

        ApduSendV2 send = new ApduSendV2();
        send.command = ByteUtil.hexStr2Bytes(commandStr);
        send.lc = lc;
        send.le = le;
        send.dataIn = ByteUtil.hexStr2Bytes(dataStr);

        try {
            if (true) {
                sendCommand(send);
            } else {
                smartCardExchange(send);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            showToast(R.string.fail);
        }
    }

    private void sendCommand(ApduSendV2 send) throws RemoteException {
        ApduRecvV2 rec = new ApduRecvV2();
        int result = mReadCardOptV2.apduCommand(AidlConstantsV2.CardType.PSAM0.getValue(), send, rec);
        if (result == 0) {
            String temp = "SWA:" + ByteUtil.byte2HexStr(rec.swa) + "\n";
            temp += "SWB:" + ByteUtil.byte2HexStr(rec.swb) + "\n";
            if (rec.outlen > 0) {
                temp += "outData:" + ByteUtil.bytes2HexStr(rec.outData).substring(0, rec.outlen * 2);
            } else {
                temp += "outData:";
            }
            mTvResultInfo.setText(temp);
        } else {
            String error = getString(R.string.fail) + ":" + result;
            showToast(error);
        }
    }

    private void smartCardExchange(ApduSendV2 send) throws RemoteException {
        byte[] sendBytes = sendV2ToBytes(send);
        byte[] recBytes = new byte[260];
        int result = mReadCardOptV2.smartCardExchange(AidlConstantsV2.CardType.PSAM0.getValue(), sendBytes, recBytes);
        if (result == 0) {
            String hexStr = ByteUtil.bytes2HexStr(recBytes);
            LogUtil.e(Constant.TAG, "hexStr:" + hexStr);
            int outLen = ByteUtil.unsignedShort2IntBE(recBytes, 0);
            byte[] outData = Arrays.copyOfRange(recBytes, 2, 2 + outLen);
            byte swa = recBytes[2 + outLen];
            byte swb = recBytes[2 + outLen + 1];
            String temp = "SWA:" + ByteUtil.byte2HexStr(swa) + "\n";
            temp += "SWB:" + ByteUtil.byte2HexStr(swb) + "\n";
            if (outLen > 0) {
                temp += "outData:" + ByteUtil.bytes2HexStr(outData).substring(0, outLen * 2);
            } else {
                temp += "outData:";
            }
            mTvResultInfo.setText(temp);
        } else {
            String error = getString(R.string.fail) + ":" + result;
            showToast(error);
        }
    }

    private byte[] sendV2ToBytes(ApduSendV2 apdu) {
        byte[] command = apdu.command;
        byte[] lc = ByteUtil.short2BytesBE(apdu.lc);
        byte[] dataIn = {};
        if (apdu.lc > 0) {
            dataIn = Arrays.copyOf(apdu.dataIn, apdu.lc);
        }
        byte[] le = ByteUtil.short2BytesBE(apdu.le);
        return ByteUtil.concatByteArrays(command, lc, dataIn, le);
    }

}
