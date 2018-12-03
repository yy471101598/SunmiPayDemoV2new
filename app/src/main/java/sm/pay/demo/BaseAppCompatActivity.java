package sm.pay.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.sunmi.pay.hardware.aidlv2.AidlErrorCodeV2;

public class BaseAppCompatActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        MyApplication.initLocaleLanguage();
    }

    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 初始化带返回的Toolbar
     */
    public void initToolbarBringBack() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Navigation Icon 要设定在 setSupportActionBar 才有作用 否则会出現 back button
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> finish()
        );
    }

    /**
     * 初始化带返回的Toolbar
     *
     * @param redId Toolbar的标题
     */
    public void initToolbarBringBack(int redId) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(redId);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> finish()
        );
    }

    public void showToast(int redId) {
        runOnUiThread(
                () -> Toast.makeText(this, redId, Toast.LENGTH_SHORT).show()
        );
    }

    public void showToast(String msg) {
        runOnUiThread(
                () -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );
    }

    public void toastHint(int code) {
        if (code == 0) {
            showToast(R.string.success);
        } else {
            String msg = AidlErrorCodeV2.valueOf(code).getMsg();
            String error = msg + ":" + code;
            showToast(error);
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void openActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

}
