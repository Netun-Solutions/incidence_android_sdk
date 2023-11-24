package es.incidence.core.utils.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import java.util.concurrent.TimeUnit;

import es.incidence.core.Constants;

public class SMSView extends RelativeLayout
{
    private RelativeLayout layoutRoot;
    private TextView txtTitleHeaderSms;
    private TextView txtHeaderSms;

    private PinEditText txtCode1;
    private PinEditText txtCode2;
    private PinEditText txtCode3;
    private PinEditText txtCode4;
    private TextView txtError;

    private TextView txtResend;
    private TextView txtResendTime;
    private IButton btnAccept;

    private CountDownTimer countDownTimer;
    private OnClickListener onClickListener;

    public SMSView(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public SMSView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SMSView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.layout_sms, this, true);

        layoutRoot = view.findViewById(R.id.layoutRoot);
        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());

        txtTitleHeaderSms = view.findViewById(R.id.txtTitleHeaderSms);
        FontUtils.setTypeValueText(txtTitleHeaderSms, Constants.FONT_SEMIBOLD, getContext());

        txtHeaderSms = view.findViewById(R.id.txtHeaderSms);

        OnFocusChangeListener ofl = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                {
                    hideError();
                }
            }
        };
        txtCode1 = view.findViewById(R.id.txtCode1);
        txtCode1.setOnFocusChangeListener(ofl);
        txtCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable))
                    txtCode2.requestFocus();

                validateContinue();
            }
        });
        txtCode2 = view.findViewById(R.id.txtCode2);
        txtCode2.setOnFocusChangeListener(ofl);
        txtCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable))
                    txtCode3.requestFocus();

                validateContinue();
            }
        });
        txtCode3 = view.findViewById(R.id.txtCode3);
        txtCode3.setOnFocusChangeListener(ofl);
        txtCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable))
                    txtCode4.requestFocus();

                validateContinue();
            }
        });
        txtCode4 = view.findViewById(R.id.txtCode4);
        txtCode4.setOnFocusChangeListener(ofl);
        txtCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable)) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(txtCode4.getWindowToken(), 0);
                }

                validateContinue();
            }
        });

        txtError = view.findViewById(R.id.txtError);


        txtResend = view.findViewById(R.id.txtResend);
        FontUtils.setTypeValueText(txtResend, Constants.FONT_SEMIBOLD, getContext());

        txtResendTime = view.findViewById(R.id.txtResendTime);

        countDownTimer = new CountDownTimer(60000 * 12, 1000) {

            public void onTick(long millisUntilFinished) {

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                String time = String.format("00:%02d", seconds);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                if (minutes > 0)
                {
                    time = String.format("%02d:%02d", minutes, seconds);
                }

                txtResendTime.setText(getContext().getString(R.string.sms_caducity_time, time));
            }

            public void onFinish() {
                txtResendTime.setText("");
                showError(getContext().getString(R.string.sms_code_timed_out));
            }
        };
        countDownTimer.start();

        btnAccept = view.findViewById(R.id.btnAccept);
        btnAccept.setText(getContext().getString(R.string.sms_validate));
        btnAccept.setDisabledColors();
        FontUtils.setTypeValueText(btnAccept, Constants.FONT_SEMIBOLD, getContext());
    }

    public void setOnClickAcceptListener(OnClickListener listener)
    {
        onClickListener = listener;
    }

    public void setOnClickResendCodeListener(OnClickListener listener)
    {
        txtResend.setOnClickListener(listener);
    }

    public void restartCodeTimer()
    {
        countDownTimer.cancel();
        countDownTimer.start();

        txtCode1.setText("");
        txtCode2.setText("");
        txtCode3.setText("");
        txtCode4.setText("");

        hideError();
    }

    public void setTitle(String title)
    {
        txtTitleHeaderSms.setText(title);
        txtTitleHeaderSms.setVisibility(View.VISIBLE);
    }

    public void setHeader(String title)
    {
        txtHeaderSms.setText(title);
    }

    public void hideError()
    {
        txtError.setVisibility(View.INVISIBLE);
        GradientDrawable drawable = (GradientDrawable)txtCode1.getBackground();
        drawable.setStroke(0, Utils.getColor(getContext(), R.color.incidence400));

        GradientDrawable drawable2 = (GradientDrawable)txtCode2.getBackground();
        drawable2.setStroke(0, Utils.getColor(getContext(), R.color.incidence400));

        GradientDrawable drawable3 = (GradientDrawable)txtCode3.getBackground();
        drawable3.setStroke(0, Utils.getColor(getContext(), R.color.incidence400));

        GradientDrawable drawable4 = (GradientDrawable)txtCode4.getBackground();
        drawable4.setStroke(0, Utils.getColor(getContext(), R.color.incidence400));
    }

    public void showError(String error)
    {
        txtError.setVisibility(View.VISIBLE);
        txtError.setText(error);

        GradientDrawable drawable = (GradientDrawable)txtCode1.getBackground();
        drawable.setStroke(Utils.dpToPx(1), Utils.getColor(getContext(), R.color.error));

        GradientDrawable drawable2 = (GradientDrawable)txtCode2.getBackground();
        drawable2.setStroke(Utils.dpToPx(1), Utils.getColor(getContext(), R.color.error));

        GradientDrawable drawable3 = (GradientDrawable)txtCode3.getBackground();
        drawable3.setStroke(Utils.dpToPx(1), Utils.getColor(getContext(), R.color.error));

        GradientDrawable drawable4 = (GradientDrawable)txtCode4.getBackground();
        drawable4.setStroke(Utils.dpToPx(1), Utils.getColor(getContext(), R.color.error));
    }

    public String getText()
    {
        String res = txtCode1.getText().toString() + txtCode2.getText().toString() + txtCode3.getText().toString() + txtCode4.getText().toString();
        return res;
    }

    private void validateContinue()
    {
        String text = getText();
        if (text != null && text.length() == 4)
        {
            btnAccept.setPrimaryColors();
            btnAccept.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null) {
                        onClickListener.onClick(view);
                    }
                }
            });
        }
        else
        {
            btnAccept.setDisabledColors();
            btnAccept.setOnClickListener(null);
        }
    }
}
