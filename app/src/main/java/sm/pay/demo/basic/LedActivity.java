package sm.pay.demo.basic;

import android.os.Bundle;
import android.view.View;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.Constant;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;
import sm.pay.demo.utils.LogUtil;

public class LedActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_led);
        initToolbarBringBack(R.string.basic_led);
        initView();
    }

    private void initView() {
        findViewById(R.id.mb_blue_open).setOnClickListener(this);
        findViewById(R.id.mb_blue_close).setOnClickListener(this);

        findViewById(R.id.mb_yellow_open).setOnClickListener(this);
        findViewById(R.id.mb_yellow_close).setOnClickListener(this);

        findViewById(R.id.mb_green_open).setOnClickListener(this);
        findViewById(R.id.mb_green_close).setOnClickListener(this);

        findViewById(R.id.mb_red_open).setOnClickListener(this);
        findViewById(R.id.mb_red_close).setOnClickListener(this);

        findViewById(R.id.mb_all_open).setOnClickListener(this);
        findViewById(R.id.mb_all_close).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.mb_blue_open:
                ledStatus(AidlConstantsV2.LedLight.BLUE_LIGHT, 0);
                break;
            case R.id.mb_blue_close:
                ledStatus(AidlConstantsV2.LedLight.BLUE_LIGHT, 1);
                break;

            case R.id.mb_yellow_open:
                ledStatus(AidlConstantsV2.LedLight.YELLOW_LIGHT, 0);
                break;
            case R.id.mb_yellow_close:
                ledStatus(AidlConstantsV2.LedLight.YELLOW_LIGHT, 1);
                break;

            case R.id.mb_green_open:
                ledStatus(AidlConstantsV2.LedLight.GREEN_LIGHT, 0);
                break;
            case R.id.mb_green_close:
                ledStatus(AidlConstantsV2.LedLight.GREEN_LIGHT, 1);
                break;

            case R.id.mb_red_open:
                ledStatus(AidlConstantsV2.LedLight.RED_LIGHT, 0);
                break;
            case R.id.mb_red_close:
                ledStatus(AidlConstantsV2.LedLight.RED_LIGHT, 1);
                break;

            case R.id.mb_all_open:
                ledStatus(AidlConstantsV2.LedLight.BLUE_LIGHT, 0);
                ledStatus(AidlConstantsV2.LedLight.YELLOW_LIGHT, 0);
                ledStatus(AidlConstantsV2.LedLight.GREEN_LIGHT, 0);
                ledStatus(AidlConstantsV2.LedLight.RED_LIGHT, 0);
                break;
            case R.id.mb_all_close:
                ledStatus(AidlConstantsV2.LedLight.BLUE_LIGHT, 1);
                ledStatus(AidlConstantsV2.LedLight.YELLOW_LIGHT, 1);
                ledStatus(AidlConstantsV2.LedLight.GREEN_LIGHT, 1);
                ledStatus(AidlConstantsV2.LedLight.RED_LIGHT, 1);
                break;
        }
    }

    /**
     * 控制LED灯
     * ledIndex: 设备上的LED索引，1~4；1-红，2-绿，3-黄，4-蓝
     * ledStatus：LED状态，1表示LED灭，0表示LED亮；
     */
    private void ledStatus(int ledIndex, int ledStatus) {
        try {
            int result = MyApplication.mBasicOptV2.ledStatusOnDevice(ledIndex, ledStatus);
            LogUtil.e(Constant.TAG, "result:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
