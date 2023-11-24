package com.e510.commons.view.FloatLabeled;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e510.commons.view.VXEditText;
import com.e510.incidencelibrary.R;

public class FloatEditText extends RelativeLayout {

    private TextView txtLabel;
    private VXEditText txtInput;
    private RelativeLayout txtClose;
    private RelativeLayout txtOk;
    private ImageView imgOk;

    private OnFocusChangeListener onFocusChangeListener;
    private TextWatcher textWatcher;

    public FloatEditText(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public FloatEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public FloatEditText(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr)
    {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PrimaryField, defStyleAttr,0);

        try
        {
            //radius = a.getInt(com.e510.commons.R.styleable.PrimaryField_radius, Integer.parseInt(AppConfiguration.getInstance().appearance.roundedFields));
        }
        finally
        {
            a.recycle();
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.layout_float_edittext, this, true);

        txtOk = view.findViewById(R.id.txtOk);
        imgOk = view.findViewById(R.id.imgOk);
        txtClose = view.findViewById(R.id.txtCloseEditText);
        txtClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                txtInput.setText("");
            }
        });
        txtLabel = view.findViewById(R.id.txtLabel);
        txtInput = view.findViewById(R.id.txtInput);

        txtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                setShowHint(!TextUtils.isEmpty(s));
                if (textWatcher != null)
                    textWatcher.afterTextChanged(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (textWatcher != null)
                    textWatcher.beforeTextChanged(s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textWatcher != null)
                    textWatcher.onTextChanged(s, start, before, count);
            }
        });

        txtInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                {
                    txtClose.setVisibility(INVISIBLE);
                }
                else
                {
                    if (getText().length() > 0)
                    {
                        txtClose.setVisibility(VISIBLE);
                    }
                }

                if (onFocusChangeListener != null)
                {
                    onFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        });
    }

    private void setShowHint(final boolean show) {
        if (!TextUtils.isEmpty(txtLabel.getText()))
        {
            AnimatorSet animation = null;
            if ((txtLabel.getVisibility() == VISIBLE) && !show) {
                animation = new AnimatorSet();
                ObjectAnimator move = ObjectAnimator.ofFloat(txtLabel, "translationY", 0, txtLabel.getHeight() / 8);
                ObjectAnimator fade = ObjectAnimator.ofFloat(txtLabel, "alpha", 1, 0);
                animation.playTogether(move, fade);
            } else if ((txtLabel.getVisibility() != VISIBLE) && show) {
                animation = new AnimatorSet();
                ObjectAnimator move = ObjectAnimator.ofFloat(txtLabel, "translationY", txtLabel.getHeight() / 8, 0);
                ObjectAnimator fade;
                fade = ObjectAnimator.ofFloat(txtLabel, "alpha", 0, 1);

                animation.playTogether(move, fade);
            }

            if (animation != null) {
                animation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        txtLabel.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        txtLabel.setVisibility(show ? VISIBLE : GONE);
                        AnimatorProxy.wrap(txtLabel).setAlpha(show ? 1 : 0);
                    }
                });
                animation.start();
            }
        }

        txtClose.setVisibility(show ? VISIBLE : GONE);
    }

    public void setHintInput(String hint)
    {
        txtInput.setHint(hint);
    }

    public void setHintTitle(String hint)
    {
        txtLabel.setText(hint);
    }

    public void setTitleColor(int color)
    {
        txtLabel.setTextColor(color);
    }

    public void setHintColor(int color)
    {
        txtInput.setHintTextColor(color);
    }

    public void setTextColor(int color)
    {
        txtInput.setTextColor(color);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        onFocusChangeListener = l;
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onKeyListener)
    {
        txtInput.setOnEditorActionListener(onKeyListener);
    }

    public void setImeOptions(int options)
    {
        txtInput.setImeOptions(options);
    }

    public void setTextWatcher(TextWatcher t)
    {
        textWatcher = t;
    }

    public void requestInputFocus()
    {
        txtInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txtInput, InputMethodManager.SHOW_IMPLICIT);
    }

    public void setInputType(int type)
    {
        txtInput.setInputType(type);
    }

    public String getText()
    {
        return txtInput.getText().toString();
    }

    public void setText(String value)
    {
        txtInput.setText(value);
        txtClose.setVisibility(GONE);
    }

    public void showOK()
    {
        txtOk.setVisibility(VISIBLE);
    }

    public void setImageOK(Drawable drawable)
    {
        imgOk.setImageDrawable(drawable);
    }

    public void setImageOKSize(int size)
    {
        LayoutParams params = (LayoutParams) imgOk.getLayoutParams();
        params.width = size;
        params.height = size;
        imgOk.setLayoutParams(params);
    }

    public void setImageOKTint(int color)
    {
        imgOk.setColorFilter(color);
    }

    public void hideOK()
    {
        txtOk.setVisibility(GONE);
    }

    @Override
    public void setEnabled(boolean enabled) {
        txtInput.setEnabled(enabled);
    }

    public void setMaxLength(int length)
    {
        txtInput.setFilters(new InputFilter[] {new InputFilter.LengthFilter(length)});
    }

    public void setSelection(int index)
    {
        txtInput.setSelection(index);
    }
}
