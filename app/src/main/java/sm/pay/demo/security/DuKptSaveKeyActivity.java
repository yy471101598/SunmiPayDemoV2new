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

public class DuKptSaveKeyActivity extends BaseAppCompatActivity {

    private EditText mEditKSN;
    private EditText mEditKeyIndex;
    private EditText mEditKeyValue;
    private EditText mEditCheckValue;

    private int mKeyType = AidlConstantsV2.Security.KEY_TYPE_DUPKT_IPEK;
    private int mKeyAlgType = AidlConstantsV2.Security.KEY_ALG_TYPE_3DES;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_dukpt_save_key);
        initToolbarBringBack(R.string.security_DuKpt_save_key);
        initView();
    }

    private void initView() {
        RadioGroup keyTypeRadioGroup = findViewById(R.id.key_type);
        keyTypeRadioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    switch (checkedId) {
                        case R.id.rb_pek:
                            mKeyType = AidlConstantsV2.Security.KEY_TYPE_DUPKT_IPEK;
                            break;
                        case R.id.rb_bdk:
                            mKeyType = AidlConstantsV2.Security.KEY_TYPE_DUPKT_BDK;
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
                    }
                }
        );

        mEditKSN = findViewById(R.id.ksn);
        mEditKeyIndex = findViewById(R.id.key_index);
        mEditKeyValue = findViewById(R.id.key_value);
        mEditCheckValue = findViewById(R.id.check_value);

        findViewById(R.id.mb_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.mb_ok:
                saveKey();
                break;
        }
    }

    private void saveKey() {
        try {
            SecurityOptV2 securityOptV2 = MyApplication.mSecurityOptV2;

            String ksnStr = mEditKSN.getText().toString();
            String keyValueStr = mEditKeyValue.getText().toString();
            String keyIndexStr = mEditKeyIndex.getText().toString();
            String checkValueStr = mEditCheckValue.getText().toString();

            if (keyValueStr.trim().length() == 0 || keyValueStr.trim().length() % 8 != 0) {
                showToast(R.string.security_key_value_hint);
                return;
            }

            if (mKeyType == AidlConstantsV2.Security.KEY_TYPE_DUPKT_IPEK) {
                if (checkValueStr.trim().length() == 0 || checkValueStr.trim().length() % 2 != 0) {
                    showToast(R.string.security_check_value_hint);
                    return;
                }
            }

            if (ksnStr.length() != 20) {
                showToast(R.string.security_ksn_hint);
                return;
            }

            int keyIndex;
            try {
                keyIndex = Integer.parseInt(keyIndexStr);
                if (keyIndex > 9 || keyIndex < 0) {
                    showToast(R.string.security_duKpt_key_hint);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast(R.string.security_duKpt_key_hint);
                return;
            }

            byte[] ksnBytes = ByteUtil.hexStr2Bytes(ksnStr);
            byte[] keyValue = ByteUtil.hexStr2Bytes(keyValueStr);
            byte[] checkValue = ByteUtil.hexStr2Bytes(checkValueStr);
            int result = securityOptV2.saveKeyDukpt(mKeyType, keyValue, checkValue, ksnBytes, mKeyAlgType, keyIndex);
            toastHint(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
