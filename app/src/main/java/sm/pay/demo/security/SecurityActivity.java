package sm.pay.demo.security;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.R;

public class SecurityActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_security));
        initToolbarBringBack(R.string.security);
        initView();
    }

    private void initView() {
        View view = findViewById(R.id.security_save_plaintext_key);
        TextView leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.security_save_plaintext_key);

        view = findViewById(R.id.security_save_cipher_text_key);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.security_save_cipher_text_key);

        view = findViewById(R.id.security_calc_mac);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.security_calc_mac);

        view = findViewById(R.id.security_data_encrypt);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.security_data_encrypt);

        view = findViewById(R.id.security_data_decrypt);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.security_data_decrypt);

        view = findViewById(R.id.security_get_encrypt_sn);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.security_get_encrypt_sn);

        view = findViewById(R.id.security_DuKpt_save_key);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.security_DuKpt_save_key);

        view = findViewById(R.id.security_DuKpt_calc_mac);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.security_DuKpt_calc_mac);

        view = findViewById(R.id.security_DuKpt_data_encrypt);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.security_DuKpt_data_encrypt);

        view = findViewById(R.id.security_DuKpt_data_decrypt);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.security_DuKpt_data_decrypt);

        view = findViewById(R.id.security_DuKpt_current_ksn);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.security_DuKpt_current_ksn);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.security_save_plaintext_key:
                openActivity(SaveKeyPlainTextActivity.class);
                break;
            case R.id.security_save_cipher_text_key:
                openActivity(SaveKeyCipherTextActivity.class);
                break;
            case R.id.security_calc_mac:
                openActivity(CalcMacActivity.class);
                break;
            case R.id.security_data_encrypt:
                openActivity(DataEncryptActivity.class);
                break;
            case R.id.security_data_decrypt:
                openActivity(DataDecryptActivity.class);
                break;
            case R.id.security_get_encrypt_sn:
                openActivity(GetEncryptBySerialNumberActivity.class);
                break;
            case R.id.security_DuKpt_save_key:
                openActivity(DuKptSaveKeyActivity.class);
                break;
            case R.id.security_DuKpt_calc_mac:
                openActivity(DuKptCalcMacActivity.class);
                break;
            case R.id.security_DuKpt_data_encrypt:
                openActivity(DuKptDataEncryptActivity.class);
                break;
            case R.id.security_DuKpt_data_decrypt:
                openActivity(DuKptDataDecryptActivity.class);
                break;
            case R.id.security_DuKpt_current_ksn:
                openActivity(DuKptKSNOperateActivity.class);
                break;
        }
    }


}
