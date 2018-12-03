package sm.pay.demo.other;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import sm.pay.demo.BaseAppCompatActivity;
import sm.pay.demo.R;

public class OtherActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        initToolbarBringBack(R.string.other);
        initView();
    }

    private void initView() {
        View item = findViewById(R.id.other_language);
        TextView leftText = item.findViewById(R.id.left_text);
        leftText.setText(R.string.other_language);
        item.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.other_language:
                openActivity(LanguageActivity.class);
                break;
        }
    }


}
