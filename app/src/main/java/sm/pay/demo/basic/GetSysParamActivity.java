package sm.pay.demo.basic;

import android.os.Bundle;
import android.widget.TextView;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;

public class GetSysParamActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_get_sys_param);
        initToolbarBringBack(R.string.basic_get_sys_param);
        initView();
    }

    private void initView() {
        TextView tvInfo = findViewById(R.id.tv_info);
        try {
            BasicOptV2 basicOptV2 = MyApplication.mBasicOptV2;
            String info = AidlConstantsV2.SysParam.SDK_VERSION + ": " + basicOptV2.getSysParam(AidlConstantsV2.SysParam.SDK_VERSION) + "\n";
            info += AidlConstantsV2.SysParam.HARDWARE_VERSION + ": " + basicOptV2.getSysParam(AidlConstantsV2.SysParam.HARDWARE_VERSION) + "\n";
            info += AidlConstantsV2.SysParam.FIRMWARE_VERSION + ": " + basicOptV2.getSysParam(AidlConstantsV2.SysParam.FIRMWARE_VERSION) + "\n";
            info += AidlConstantsV2.SysParam.BootVersion + ": " + basicOptV2.getSysParam(AidlConstantsV2.SysParam.BootVersion) + "\n";
            info += AidlConstantsV2.SysParam.SN + ": " + basicOptV2.getSysParam(AidlConstantsV2.SysParam.SN) + "\n";
            info += AidlConstantsV2.SysParam.PN + ": " + basicOptV2.getSysParam(AidlConstantsV2.SysParam.PN) + "\n";
            info += AidlConstantsV2.SysParam.TUSN + ": " + basicOptV2.getSysParam(AidlConstantsV2.SysParam.TUSN) + "\n";
            info += AidlConstantsV2.SysParam.DEVICE_CODE + ": " + basicOptV2.getSysParam(AidlConstantsV2.SysParam.DEVICE_CODE) + "\n";
            info += AidlConstantsV2.SysParam.DEVICE_MODEL + ": " + basicOptV2.getSysParam(AidlConstantsV2.SysParam.DEVICE_MODEL) + "\n";
            info += AidlConstantsV2.SysParam.RESERVED + ": " + basicOptV2.getSysParam(AidlConstantsV2.SysParam.RESERVED);
            tvInfo.setText(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
