package sm.pay.demo.card;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.R;

public class CardActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_card));
        initToolbarBringBack(R.string.read_card);
        initView();
    }

    private void initView() {
        View view = findViewById(R.id.card_mag);
        TextView leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.card_test_mag);

        view = findViewById(R.id.card_ic);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.card_test_ic);

        view = findViewById(R.id.card_m1);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.card_test_m1);

        view = findViewById(R.id.card_sam);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.card_test_sam);

        view = findViewById(R.id.card_MIFARE_Ultralight);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.card_test_MIFARE_Ultralight);

        view = findViewById(R.id.card_MIFARE_Ultralight_ev1);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.card_test_MIFARE_Ultralight_ev1);

        view = findViewById(R.id.card_FELICA);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.card_test_FELICA);

        view = findViewById(R.id.card_NISO);
        leftText = view.findViewById(R.id.left_text);
        view.setOnClickListener(this);
        leftText.setText(R.string.card_test_NISO);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.card_mag:
                openActivity(MagActivity.class);
                break;
            case R.id.card_ic:
                openActivity(ICActivity.class);
                break;
            case R.id.card_m1:
                openActivity(M1Activity.class);
                break;
            case R.id.card_sam:
                openActivity(SAMActivity.class);
                break;
            case R.id.card_MIFARE_Ultralight:
                openActivity(MIFAREUltralightCActivity.class);
                break;
            case R.id.card_MIFARE_Ultralight_ev1:
                openActivity(MifareUtralightEv1Activity.class);
                break;
            case R.id.card_FELICA:
                openActivity(FELICAActivity.class);
                break;
            case R.id.card_NISO:
                openActivity(NISOActivity.class);
                break;
        }
    }


}
