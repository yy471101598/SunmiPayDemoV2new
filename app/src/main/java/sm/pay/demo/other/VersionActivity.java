package sm.pay.demo.other;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2;

import java.lang.reflect.Method;
import java.util.List;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.BuildConfig;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;

public class VersionActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_version);
        initToolbarBringBack(R.string.version);
        initView();
    }

    private void initView() {
        TextView tvInfo = findViewById(R.id.tv_info);

        String serviceVersion = "应用未安装";
        try {
            List<PackageInfo> list = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
            for (int i = 0; i < list.size(); i++) {
                PackageInfo packageInfo = list.get(i);
                boolean contains = packageInfo.packageName.contains("pay.hardware_v3");
                if (contains) {
                    serviceVersion = packageInfo.versionName;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BasicOptV2 basicOptV2 = MyApplication.mBasicOptV2;
            String info = getString(R.string.other_version_device) + basicOptV2.getSysParam(AidlConstantsV2.SysParam.DEVICE_MODEL) + "\n";
            info += getString(R.string.other_version_rom) + getRomVersionName() + "\n";
            info += getString(R.string.other_version_sn) + basicOptV2.getSysParam(AidlConstantsV2.SysParam.SN) + "\n";
            info += getString(R.string.other_version_demo) + BuildConfig.VERSION_NAME + "\n";
            info += getString(R.string.other_version_service) + serviceVersion;
            tvInfo.setText(info);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("PrivateApi")
    private String getRomVersionName() {
        try {
            String filed = "ro.version.SunMi_VersionName".toLowerCase();
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method get = clazz.getMethod("get", String.class);
            return (String) get.invoke(clazz, filed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
