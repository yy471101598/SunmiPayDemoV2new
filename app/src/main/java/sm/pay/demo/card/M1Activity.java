package sm.pay.demo.card;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.Constant;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;
import sm.pay.demo.utils.ByteUtil;
import sm.pay.demo.utils.LogUtil;
import sm.pay.demo.utils.SwingCardHintDialog;

public class M1Activity extends BaseAppCompatActivity {

    private EditText mEditSector1;
    private EditText mEditKeyA1;
    private EditText mEditKeyB1;
    private EditText mEditBlock0;
    private EditText mEditBlock1;
    private EditText mEditBlock2;

    private EditText mEditSector2;
    private EditText mEditBlock;
    private EditText mEditKeyA2;
    private EditText mEditKeyB2;
    private EditText mEditCost;

    private TextView mTvBalance;

    private ReadCardOptV2 mReadCardOptV2;

    private int block;
    private int sector;
    private int keyType;    // 密钥类型，0表示KEY A、1表示 KEY B
    private byte[] keyBytes;

    private SwingCardHintDialog mHintDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_m1);
        initView();
        checkCard();
    }

    private void initView() {
        mReadCardOptV2 = MyApplication.mReadCardOptV2;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.card_test_m1);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> onBackPressed()
        );

        mEditSector1 = findViewById(R.id.edit_sector_1);
        mEditKeyA1 = findViewById(R.id.edit_keyA_1);
        mEditKeyB1 = findViewById(R.id.edit_keyB_1);
        mEditBlock0 = findViewById(R.id.edit_block_0);
        mEditBlock1 = findViewById(R.id.edit_block_1);
        mEditBlock2 = findViewById(R.id.edit_block_2);

        mEditSector2 = findViewById(R.id.edit_sector_2);
        mEditKeyA2 = findViewById(R.id.edit_keyA_2);
        mEditKeyB2 = findViewById(R.id.edit_keyB_2);
        mEditBlock = findViewById(R.id.edit_block);
        mEditCost = findViewById(R.id.edit_cost);

        mTvBalance = findViewById(R.id.tv_balance);

        findViewById(R.id.mb_read).setOnClickListener(this);
        findViewById(R.id.mb_write).setOnClickListener(this);
        findViewById(R.id.mb_init).setOnClickListener(this);
        findViewById(R.id.mb_balance).setOnClickListener(this);
        findViewById(R.id.mb_add).setOnClickListener(this);
        findViewById(R.id.mb_reduce).setOnClickListener(this);

        mHintDialog = new SwingCardHintDialog(this);
        mHintDialog.setOwnerActivity(this);
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
            case R.id.mb_read:
                boolean check = checkParams();
                if (check) {
                    readAllSector();
                }
                break;
            case R.id.mb_write:
                check = checkParams();
                if (check) {
                    writeAllSector();
                }
                break;
            case R.id.mb_init:
                check = checkParamsWallet();
                if (check) {
                    initWallet();
                }
                break;
            case R.id.mb_balance:
                check = checkParamsWallet();
                if (check) {
                    getBalanceWallet();
                }
                break;
            case R.id.mb_add:
                check = checkParamsWallet();
                if (check) {
                    increaseValueWallet();
                }
                break;
            case R.id.mb_reduce:
                check = checkParamsWallet();
                if (check) {
                    decreaseValueWallet();
                }
                break;
        }
    }

    private void showHintDialog() {
        runOnUiThread(
                () -> {
                    boolean b = mHintDialog.isShowing() || isDestroyed();
                    if (b) return;
                    mHintDialog.show();
                }
        );
    }

    private void dismissHintDialog() {
        runOnUiThread(
                () -> {
                    if (mHintDialog != null) {
                        mHintDialog.dismiss();
                    }
                }
        );
    }

    private void checkCard() {
        try {
            showHintDialog();
            mReadCardOptV2.checkCard(AidlConstantsV2.CardType.MIFARE.getValue(), mCheckCardCallback, 60);
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

        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {
            LogUtil.e(Constant.TAG, "findRFCard:" + uuid);
            dismissHintDialog();
        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            checkCard();
        }

    };

    private void readAllSector() {
        boolean result = m1Auth(keyType, sector, keyBytes);
        if (result) {
            byte[] outData = new byte[128];
            int res = m1ReadBlock(sector, outData);
            if (res >= 0 && res <= 16) {
                String hexStr = ByteUtil.bytes2HexStr(outData).substring(0, res * 2);
                LogUtil.e(Constant.TAG, "read outData:" + hexStr);
                mEditBlock0.setText(hexStr);
            } else {
                mEditBlock0.setText(R.string.fail);
            }

            outData = new byte[128];
            res = m1ReadBlock(sector + 1, outData);
            if (res >= 0 && res <= 16) {
                String hexStr = ByteUtil.bytes2HexStr(outData).substring(0, res * 2);
                LogUtil.e(Constant.TAG, "read outData:" + hexStr);
                mEditBlock1.setText(hexStr);
            } else {
                mEditBlock1.setText(R.string.fail);
            }

            outData = new byte[128];
            res = m1ReadBlock(sector + 2, outData);
            if (res >= 0 && res <= 16) {
                String hexStr = ByteUtil.bytes2HexStr(outData).substring(0, res * 2);
                LogUtil.e(Constant.TAG, "read outData:" + hexStr);
                mEditBlock2.setText(hexStr);
            } else {
                mEditBlock2.setText(R.string.fail);
            }
        }
    }

    private void writeAllSector() {
        boolean result = m1Auth(keyType, sector, keyBytes);
        if (result) {
            String val = mEditBlock0.getText().toString();
            if (val.length() == 32) {
                byte[] inData = ByteUtil.hexStr2Bytes(val);
                int res = m1WriteBlock(sector, inData);
                if (res == 0) {
                    mEditBlock0.setText("");
                } else {
                    mEditBlock0.setText(R.string.fail);
                }
            }

            val = mEditBlock1.getText().toString();
            if (val.length() == 32) {
                byte[] inData = ByteUtil.hexStr2Bytes(val);
                int res = m1WriteBlock(sector + 1, inData);
                if (res == 0) {
                    mEditBlock1.setText("");
                } else {
                    mEditBlock1.setText(R.string.fail);
                }
            }

            val = mEditBlock2.getText().toString();
            if (val.length() == 32) {
                byte[] inData = ByteUtil.hexStr2Bytes(val);
                int res = m1WriteBlock(sector + 2, inData);
                if (res == 0) {
                    mEditBlock2.setText("");
                } else {
                    mEditBlock2.setText(R.string.fail);
                }
            }
        }
    }

    /**
     * init wallet format
     */
    private void initWallet() {
        boolean result = m1Auth(keyType, block, keyBytes);
        if (result) {
            byte[] inData = getInitFormatData(block);
            String hexStr = ByteUtil.bytes2HexStr(inData);
            LogUtil.e(Constant.TAG, "init wallet format inData:" + hexStr);
            int res = m1WriteBlock(block, inData);
            if (res == 0) {
                showToast(R.string.card_wallet_init_success);
                getBalanceWallet();
            } else {
                String error = getString(R.string.card_wallet_init_fail) + ":" + res;
                showToast(error);
            }
        }
    }

    /**
     * get wallet balance
     */
    private void getBalanceWallet() {
        boolean result = m1Auth(keyType, block, keyBytes);
        if (result) {
            byte[] outData = new byte[128];
            int res = m1ReadBlock(block, outData);
            if (res >= 0 && res <= 16) {
                String hexStr = ByteUtil.bytes2HexStr(outData).substring(0, res * 2);
                LogUtil.e(Constant.TAG, "get wallet balance outData:" + hexStr);
                long balance = outData[0] + (outData[1] << 8) + (outData[2] << 16) + (outData[3] << 24);
                String val = getString(R.string.card_balance_symbol) + balance;
                mTvBalance.setText(val);
            } else {
                String error = getString(R.string.card_wallet_balance_fail) + ":" + res;
                showToast(error);
            }
        }
    }

    /**
     * increase wallet value
     */
    private void increaseValueWallet() {
        String costStr = mEditCost.getText().toString();
        long amount;
        try {
            amount = Long.parseLong(costStr);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.card_cost_hint);
            return;
        }
        boolean result = m1Auth(keyType, block, keyBytes);
        if (result) {
            byte[] inData = ByteUtil.long2Bytes(amount);
            int res = m1IncValue(block, inData);
            if (res == 0) {
                // showToast(R.string.card_wallet_add_value_success);
                getBalanceWallet();
            } else {
                String error = getString(R.string.card_wallet_add_value_fail) + ":" + res;
                showToast(error);
            }
        }
    }

    /**
     * decrease wallet value
     */
    private void decreaseValueWallet() {
        String costStr = mEditCost.getText().toString();
        long amount;
        try {
            amount = Integer.parseInt(costStr);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.card_cost_hint);
            return;
        }
        boolean result = m1Auth(keyType, block, keyBytes);
        if (result) {
            byte[] inData = ByteUtil.long2Bytes(amount);
            int res = m1DecValue(block, inData);
            if (res == 0) {
                // showToast(R.string.card_wallet_dec_value_success);
                getBalanceWallet();
            } else {
                String error = getString(R.string.card_wallet_dec_value_fail) + ":" + res;
                showToast(error);
            }
        }
    }

    private boolean checkParams() {
        String sectorStr = mEditSector1.getText().toString();
        String keyAStr = mEditKeyA1.getText().toString();
        String keyBStr = mEditKeyB1.getText().toString();
        try {
            sector = Integer.parseInt(sectorStr);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.card_sector_hint);
            return false;
        }
        if (keyAStr.length() == 12) {
            keyType = 0;
            keyBytes = ByteUtil.hexStr2Bytes(keyAStr);
        }
        if (keyBStr.length() == 12) {
            keyType = 1;
            keyBytes = ByteUtil.hexStr2Bytes(keyBStr);
        }
        if (keyBytes == null) {
            showToast(R.string.card_key_hint);
            return false;
        }

        return true;
    }

    private boolean checkParamsWallet() {
        String sectorStr = mEditSector2.getText().toString();
        String blockStr = mEditBlock.getText().toString();
        String keyAStr = mEditKeyA2.getText().toString();
        String keyBStr = mEditKeyB2.getText().toString();
        try {
            sector = Integer.parseInt(sectorStr);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.card_sector_hint);
            return false;
        }
        try {
            block = Integer.parseInt(blockStr);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.card_block_hint);
            return false;
        }
        if (keyAStr.length() == 12) {
            keyType = 0;
            keyBytes = ByteUtil.hexStr2Bytes(keyAStr);
        }
        if (keyBStr.length() == 12) {
            keyType = 1;
            keyBytes = ByteUtil.hexStr2Bytes(keyBStr);
        }
        if (keyBytes == null) {
            showToast(R.string.card_key_hint);
            return false;
        }

        // calculate block
        block = sector * 4 + block;

        return true;
    }

    /**
     * init wallet format data
     */
    private byte[] getInitFormatData(int blockIndex) {
        byte[] result = {
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        };
        result[12] = (byte) (blockIndex & 0xFF);
        result[13] = (byte) ~(blockIndex & 0xFF);
        result[14] = (byte) (blockIndex & 0xFF);
        result[15] = (byte) ~(blockIndex & 0xFF);
        return result;
    }

    /**
     * m1 card auth
     */
    private boolean m1Auth(int keyType, int block, byte[] keyData) {
        boolean val = false;
        try {
            String hexStr = ByteUtil.bytes2HexStr(keyData);
            LogUtil.e(Constant.TAG, "block:" + block + " keyType:" + keyType + " keyBytes:" + hexStr);

            int result = mReadCardOptV2.mifareAuth(keyType, block, keyData);
            LogUtil.e(Constant.TAG, "m1Auth result:" + result);
            val = result == 0;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (val) {
            return true;
        } else {
            showToast(R.string.card_auth_fail);
            checkCard();
            return false;
        }
    }

    /**
     * m1 write block data
     */
    private int m1WriteBlock(int block, byte[] blockData) {
        try {
            int result = mReadCardOptV2.mifareWriteBlock(block, blockData);
            LogUtil.e(Constant.TAG, "m1WriteBlock result:" + result);
            return result;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -123;
    }

    /**
     * m1 read block data
     */
    private int m1ReadBlock(int block, byte[] blockData) {
        try {
            int result = mReadCardOptV2.mifareReadBlock(block, blockData);
            LogUtil.e(Constant.TAG, "m1ReadBlock result:" + result);
            return result;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -123;
    }

    /**
     * m1 increase value
     */
    private int m1IncValue(int block, byte[] blockData) {
        try {
            int result = mReadCardOptV2.mifareIncValue(block, blockData);
            LogUtil.e(Constant.TAG, "m1IncValue result:" + result);
            return result;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -123;
    }

    /**
     * m1 decrease value
     */
    private int m1DecValue(int block, byte[] blockData) {
        try {
            int result = mReadCardOptV2.mifareDecValue(block, blockData);
            LogUtil.e(Constant.TAG, "m1DecValue result:" + result);
            return result;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -123;
    }


}
