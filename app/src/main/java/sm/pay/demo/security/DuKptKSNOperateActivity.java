package sm.pay.demo.security;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;
import sm.pay.demo.utils.ByteUtil;

public class DuKptKSNOperateActivity extends BaseAppCompatActivity {

    private TextView mTvInfo;

    private EditText mEditKeyIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_dukpt_ksn);
        initToolbarBringBack(R.string.security_duKpt_ksn_control);
        initView();
    }

    private void initView() {
        mEditKeyIndex = findViewById(R.id.key_index);
        mTvInfo = findViewById(R.id.tv_info);

        findViewById(R.id.mb_get_ksn).setOnClickListener(this);
        findViewById(R.id.mb_ksn_increased).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.mb_ksn_increased:
                ksnIncreased();
                break;
            case R.id.mb_get_ksn:
                getKsn();
                break;
        }
    }

    private void ksnIncreased() {
        try {
            SecurityOptV2 securityOptV2 = MyApplication.mSecurityOptV2;
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
            int result = securityOptV2.dukptIncreaseKSN(keyIndex);
            toastHint(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getKsn() {
        try {
            SecurityOptV2 securityOptV2 = MyApplication.mSecurityOptV2;
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
            byte[] dataOut = new byte[10];
            int result = securityOptV2.dukptCurrentKSN(keyIndex, dataOut);
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
