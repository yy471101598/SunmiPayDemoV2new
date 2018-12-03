package sm.pay.demo.card;

import android.os.Bundle;
import android.support.annotation.Nullable;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.R;

public class FELICAActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_felica);
        initToolbarBringBack(R.string.card_test_FELICA);
    }

}
