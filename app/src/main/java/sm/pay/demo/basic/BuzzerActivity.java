package sm.pay.demo.basic;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.MyApplication;
import sm.pay.demo.R;

public class BuzzerActivity extends BaseAppCompatActivity {

    private EditText mEditTimeDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_buzzer);
        initToolbarBringBack(R.string.basic_buzzer);
        initView();
    }

    private void initView() {
        mEditTimeDelay = findViewById(R.id.edit_time_delay);
        RadioGroup radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    switch (checkedId) {
                        case R.id.rb_1:
                            buzzer(1);
                            break;
                        case R.id.rb_2:
                            buzzer(2);
                            break;
                        case R.id.rb_3:
                            buzzer(3);
                            break;
                        case R.id.rb_4:
                            buzzer(4);
                            break;
                        case R.id.rb_5:
                            buzzer(5);
                            break;
                        case R.id.rb_6:
                            buzzer(6);
                            break;
                    }
                }
        );
    }

    /**
     * 控制蜂鸣器
     * times: 设备上的蜂鸣器响的次数，1~10
     */
    private void buzzer(int time) {
        new Thread(
                () -> {
                    try {
                        int delay;
                        String delayStr = mEditTimeDelay.getText().toString();
                        try {
                            delay = Integer.parseInt(delayStr);
                            if (delay < 200 || delay > 10000) {
                                showToast(R.string.basic_buzzer_time_delay_hint);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast(R.string.basic_buzzer_time_delay_hint);
                            return;
                        }
                        MyApplication.mBasicOptV2.buzzerOnDevice(time, delay);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }


}
