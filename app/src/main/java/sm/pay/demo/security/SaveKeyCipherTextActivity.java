package sm.pay.demo.security;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;
import sm.pay.demo.utils.ByteUtil;

public class SaveKeyCipherTextActivity extends BaseAppCompatActivity {

    private EditText mEditKeyIndex;
    private EditText mEditKeyValue;
    private EditText mEditCheckValue;
    private EditText mEditEncryptIndex;

    private int mKeyType = AidlConstantsV2.Security.KEY_TYPE_TMK;
    private int mKeyAlgType = AidlConstantsV2.Security.KEY_ALG_TYPE_3DES;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_save_key_cipher_text);
        initToolbarBringBack(R.string.security_save_cipher_text_key);
        initView();
    }

    private void initView() {
        RadioGroup keyTypeRadioGroup = findViewById(R.id.key_type);
        keyTypeRadioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    switch (checkedId) {
                        case R.id.rb_tmk:
                            mKeyType = AidlConstantsV2.Security.KEY_TYPE_TMK;
                            break;
                        case R.id.rb_pik:
                            mKeyType = AidlConstantsV2.Security.KEY_TYPE_PIK;
                            break;
                        case R.id.rb_tdk:
                            mKeyType = AidlConstantsV2.Security.KEY_TYPE_TDK;
                            break;
                        case R.id.rb_mak:
                            mKeyType = AidlConstantsV2.Security.KEY_TYPE_MAK;
                            break;
                        case R.id.rb_rec_key:
                            mKeyType = AidlConstantsV2.Security.KEY_TYPE_REC;
                            break;
                    }
                }
        );

        RadioGroup keyAlgTypeRadioGroup = findViewById(R.id.key_alg_type);
        keyAlgTypeRadioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    switch (checkedId) {
                        case R.id.rb_3des:
                            mKeyAlgType = AidlConstantsV2.Security.KEY_ALG_TYPE_3DES;
                            break;
                        case R.id.rb_sm4:
                            mKeyAlgType = AidlConstantsV2.Security.KEY_ALG_TYPE_SM4;
                            break;
                    }
                }
        );

        mEditKeyIndex = findViewById(R.id.key_index);
        mEditKeyValue = findViewById(R.id.key_value);
        mEditCheckValue = findViewById(R.id.check_value);
        mEditEncryptIndex = findViewById(R.id.encrypt_index);

        findViewById(R.id.mb_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.mb_ok:
                saveCipherTextKey();
                break;
        }
    }

    private void saveCipherTextKey() {
        try {
            SecurityOptV2 securityOptV2 = MyApplication.mSecurityOptV2;

            String keyValueStr = mEditKeyValue.getText().toString();
            String keyIndexStr = mEditKeyIndex.getText().toString();
            String checkValueStr = mEditCheckValue.getText().toString();
            String encryptIndexStr = mEditEncryptIndex.getText().toString();

            if (keyValueStr.trim().length() == 0 || keyValueStr.trim().length() % 8 != 0) {
                showToast(R.string.security_key_value_hint);
                return;
            }

            if (mKeyAlgType == AidlConstantsV2.Security.KEY_ALG_TYPE_SM4) {
                if (checkValueStr.trim().length() == 0 || checkValueStr.trim().length() > 32 || checkValueStr.trim().length() % 2 != 0) {
                    showToast(R.string.security_check_value_hint);
                    return;
                }
            } else {
                if (checkValueStr.trim().length() == 0 || checkValueStr.trim().length() > 16 || checkValueStr.trim().length() % 2 != 0) {
                    showToast(R.string.security_check_value_hint);
                    return;
                }
            }

            int encryptIndex;
            try {
                encryptIndex = Integer.valueOf(encryptIndexStr);
                if (encryptIndex > 19 || encryptIndex < 0) {
                    showToast(R.string.security_decrypt_index_hint);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast(R.string.security_decrypt_index_hint);
                return;
            }

            int keyIndex;
            try {
                keyIndex = Integer.valueOf(keyIndexStr);
                if (keyIndex > 19 || keyIndex < 0) {
                    showToast(R.string.security_key_index_hint);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast(R.string.security_key_index_hint);
                return;
            }

            byte[] keyValue = ByteUtil.hexStr2Bytes(keyValueStr);
            byte[] checkValue = ByteUtil.hexStr2Bytes(checkValueStr);
            int result = securityOptV2.saveCiphertextKey(mKeyType, keyValue, checkValue, encryptIndex, mKeyAlgType, keyIndex);
            toastHint(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
