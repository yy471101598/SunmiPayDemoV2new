package sm.pay.demo.security;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;
import sm.pay.demo.utils.ByteUtil;

public class DuKptDataDecryptActivity extends BaseAppCompatActivity {

    private EditText mEditData;
    private EditText mEditInitIV;
    private EditText mEditKeyIndex;

    private TextView mTvInfo;

    private int mDecryptType = AidlConstantsV2.Security.DATA_MODE_ECB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_data_decrypt);
        initToolbarBringBack(R.string.security_DuKpt_data_decrypt);
        initView();
    }

    private void initView() {
        RadioGroup keyTypeRadioGroup = findViewById(R.id.mac_type);
        keyTypeRadioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    switch (checkedId) {
                        case R.id.rb_decrypt_type1:
                            mDecryptType = AidlConstantsV2.Security.DATA_MODE_ECB;
                            break;
                        case R.id.rb_decrypt_type2:
                            mDecryptType = AidlConstantsV2.Security.DATA_MODE_CBC;
                            break;
                        case R.id.rb_decrypt_type3:
                            mDecryptType = AidlConstantsV2.Security.DATA_MODE_OFB;
                            break;
                        case R.id.rb_decrypt_type4:
                            mDecryptType = AidlConstantsV2.Security.DATA_MODE_CFB;
                            break;
                    }
                }
        );

        mEditData = findViewById(R.id.source_data);
        mEditKeyIndex = findViewById(R.id.key_index);
        mEditInitIV = findViewById(R.id.initialization_vector);

        mTvInfo = findViewById(R.id.tv_info);

        mEditData.setText("FC0D53B7EA1FDA9EE68AAF2E70D9B9506229BE2AA993F04F");

        findViewById(R.id.mb_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.mb_ok:
                dataDecrypt();
                break;
        }
    }

    private void dataDecrypt() {
        try {
            SecurityOptV2 securityOptV2 = MyApplication.mSecurityOptV2;

            String ivStr = mEditInitIV.getText().toString();
            String dataStr = mEditData.getText().toString();
            String keyIndexStr = mEditKeyIndex.getText().toString();

            int keyIndex;
            try {
                keyIndex = Integer.valueOf(keyIndexStr);
                if (keyIndex > 9 || keyIndex < 0) {
                    showToast(R.string.security_duKpt_key_hint);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast(R.string.security_duKpt_key_hint);
                return;
            }

            if (mDecryptType != AidlConstantsV2.Security.DATA_MODE_ECB && ivStr.length() != 16) {
                showToast(R.string.security_init_vector_hint);
                return;
            }

            if (dataStr.trim().length() == 0 || dataStr.length() % 16 != 0) {
                showToast(R.string.security_source_data_hint);
                return;
            }

            byte[] dataIn = ByteUtil.hexStr2Bytes(dataStr);
            byte[] dataOut = new byte[dataIn.length];
            byte[] ivByte;
            if (mDecryptType != AidlConstantsV2.Security.DATA_MODE_ECB) {
                ivByte = ByteUtil.hexStr2Bytes(ivStr);
            } else {
                ivByte = null;
            }
            int result = securityOptV2.dataDecryptDukpt(keyIndex, dataIn, mDecryptType, ivByte, dataOut);
            if (result == 0) {
                String hexStr = ByteUtil.bytes2HexStr(dataOut);
                mTvInfo.setText(hexStr);
            } else {
                toastHint(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}