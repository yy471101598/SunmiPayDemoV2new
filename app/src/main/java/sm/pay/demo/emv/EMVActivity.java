package sm.pay.demo.emv;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.R;

public class EMVActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emv);
        initToolbarBringBack(R.string.emv);
        initView();
    }

    private void initView() {
        View item = findViewById(R.id.item_ic);
        TextView leftText = item.findViewById(R.id.left_text);
        leftText.setText(R.string.emv_ic_process);
        item.setOnClickListener(this);

        item = findViewById(R.id.item_mag);
        leftText = item.findViewById(R.id.left_text);
        leftText.setText(R.string.emv_mag_process);
        item.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.item_ic:
                openActivity(ICProcessActivity.class);
                break;
            case R.id.item_mag:
                openActivity(MagProcessActivity.class);
                break;
        }
    }


}
