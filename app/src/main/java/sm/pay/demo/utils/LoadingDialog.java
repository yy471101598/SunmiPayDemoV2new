package sm.pay.demo.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;

import sm.pay.demo.R;

public class LoadingDialog extends Dialog {

    private TextView mTvMessage;

    public LoadingDialog(Context context, String text) {
        this(context, R.style.DefaultDialogStyle, text);
    }

    private LoadingDialog(Context context, int theme, String text) {
        super(context, theme);
        init(text);
    }

    private void init(String msg) {
        setContentView(R.layout.dialog_loading);
        Window window = getWindow();
        if (window != null) {
            window.getAttributes().gravity = Gravity.CENTER;
        }
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        mTvMessage = findViewById(R.id.tv_message);
        setMessage(msg);
    }

    public void setMessage(String msg) {
        msg = TextUtils.isEmpty(msg) ? getContext().getString(R.string.loading) : msg;
        mTvMessage.setText(msg);
    }


}
