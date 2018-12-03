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

public class GetEncryptBySerialNumberActivity extends BaseAppCompatActivity {

    private EditText mEditData;

    private TextView mTvInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_get_encrypt_serial_number);
        initToolbarBringBack(R.string.security_get_encrypt_sn);
        initView();
    }

    private void initView() {
        mTvInfo = findViewById(R.id.tv_info);
        mEditData = findViewById(R.id.source_data);

        findViewById(R.id.mb_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.mb_ok:
                getTUSNEncryptData();
                break;
        }
    }

    private void getTUSNEncryptData() {
        try {
            SecurityOptV2 securityOptV2 = MyApplication.mSecurityOptV2;

            String dataStr = mEditData.getText().toString();
            if (dataStr.trim().length() == 0) {
                showToast(R.string.security_source_data_hint);
                return;
            }

            byte[] dataOut = new byte[4];
            int result = securityOptV2.getTUSNEncryptData(dataStr, dataOut);
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
