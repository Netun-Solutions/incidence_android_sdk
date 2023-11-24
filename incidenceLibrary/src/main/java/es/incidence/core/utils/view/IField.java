package es.incidence.core.utils.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.StringUtils;
import com.e510.commons.utils.Utils;
import com.e510.commons.utils.config.AppConfiguration;
import com.e510.commons.view.FloatLabeled.FloatEditText;
import com.e510.incidencelibrary.R;

import java.util.Calendar;
import java.util.Locale;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import es.incidence.core.utils.IUtils;

public class IField extends RelativeLayout
{
    private float radius;
    private boolean withGradient = true;
    private boolean withValidation = true;

    private RelativeLayout layoutRoot;
    private ImageView imgLeft;
    private FloatEditText floatEditText;
    private TextView txtError;
    private RelativeLayout layoutClick;

    private int type;
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_EMAIL = 1;
    public static final int TYPE_PHONE = 2;
    public static final int TYPE_PASSWORD = 3;
    public static final int TYPE_DATE = 4;
    public static final int TYPE_NUMBER = 5;

    private TextWatcher textWatcher;
    private OnFocusChangeListener onFocusChangeListener;

    public IField(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public IField(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public IField(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr)
    {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PrimaryField, defStyleAttr,0);

        try
        {
            radius = a.getInt(R.styleable.PrimaryField_radius, Integer.parseInt(AppConfiguration.getInstance().appearance.roundedFields));
        }
        finally
        {
            a.recycle();
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.layout_ifield, this, true);

        layoutRoot = view.findViewById(R.id.layoutRoot);
        layoutRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                floatEditText.requestInputFocus();
            }
        });
        imgLeft = view.findViewById(R.id.imgLeft);
        floatEditText = view.findViewById(R.id.textInputLayout);
        floatEditText.setTitleColor(Utils.getColor(getContext(), R.color.black500));
        floatEditText.setHintColor(Utils.getColor(getContext(), R.color.black400));
        floatEditText.setTextColor(Utils.getColor(getContext(), R.color.black600));
        floatEditText.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (textWatcher != null)
                {
                    textWatcher.beforeTextChanged(charSequence, i, i1, i2);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if (type == TYPE_PHONE)
                {
                    if (IUtils.isFormattedPhone(charSequence.toString())) {
                        return;
                    }
                    String formatted = IUtils.formatPhoneNumber(charSequence.toString());
                    setText(formatted);
                    floatEditText.setSelection(formatted.length());
                }

                if (textWatcher != null)
                {
                    textWatcher.onTextChanged(charSequence, i, i1, i2);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (textWatcher != null)
                {
                    textWatcher.afterTextChanged(editable);
                }
            }
        });
        floatEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                {
                    if (withGradient)
                    {
                        GradientDrawable drawable = (GradientDrawable)layoutRoot.getBackground();
                        drawable.setStroke(0, Utils.getColor(getContext(), R.color.incidence400));
                    }

                    if (withValidation)
                    {
                        if (isValidated()) {
                            showOK();
                        } else {
                            hideOK();
                        }
                    }
                }
                else
                {
                    if (withValidation)
                    {
                        hideOK();
                        hideError();
                    }

                    if (withGradient)
                    {
                        GradientDrawable drawable = (GradientDrawable)layoutRoot.getBackground();
                        drawable.setStroke(Utils.dpToPx(1), Utils.getColor(getContext(), R.color.incidence400));
                    }
                }

                if (onFocusChangeListener != null)
                {
                    onFocusChangeListener.onFocusChange(view, b);
                }
            }
        });

        layoutClick = view.findViewById(R.id.layoutClick);
        txtError = view.findViewById(R.id.txtError);

        setBackground(context, android.R.color.white);

        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        onFocusChangeListener = l;
    }

    public void setWithGradient(boolean gradient)
    {
        withGradient = gradient;
    }

    public void setWithValidation(boolean validation)
    {
        withValidation = validation;
    }

    public void setImageLeft(Drawable drawable)
    {
        imgLeft.setImageDrawable(drawable);
        imgLeft.setVisibility(View.VISIBLE);
    }

    public void setImageOK(Drawable drawable)
    {
        floatEditText.setImageOK(drawable);
    }

    public void setImageOKSize(int size)
    {
        floatEditText.setImageOKSize(size);
    }

    public void setImageOKTint(int color)
    {
        floatEditText.setImageOKTint(color);
    }

    public void setBackground(Context context, int backgroundColor)
    {
        GradientDrawable back =  Utils.createGradientDrawable(context, backgroundColor, (int) radius);
        layoutRoot.setBackground(back);
    }

    public void setTitle(String title)
    {
        floatEditText.setHintTitle(title);
    }

    public void setHint(String hint)
    {
        floatEditText.setHintInput(hint);
    }

    public void setMaxLength(int length)
    {
        floatEditText.setMaxLength(length);
    }

    public void setTextColor(int idColor)
    {
        floatEditText.setTextColor(Utils.getColor(getContext(), idColor));
    }

    public void setType(int type)
    {
        this.type = type;
        if (type == TYPE_EMAIL)
        {
            floatEditText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
        else if (type == TYPE_PHONE)
        {
            floatEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        }
        else if (type == TYPE_PASSWORD)
        {
            floatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        else if (type == TYPE_DATE)
        {
            enableDatePicker();
        }
        else if (type == TYPE_NUMBER)
        {
            floatEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        else
        {
            floatEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        layoutClick.setOnClickListener(l);
    }

    public void disable()
    {
        layoutRoot.setOnClickListener(null);
        floatEditText.setEnabled(false);
        layoutClick.setVisibility(View.VISIBLE);
    }

    public void enable()
    {
        layoutRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                floatEditText.requestInputFocus();
            }
        });
        floatEditText.setEnabled(true);
        layoutClick.setVisibility(View.GONE);
    }

    public void enableDatePicker()
    {
        disable();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showDatePickerDialog();
            }
        });
    }

    public String getText() {

        String text = floatEditText.getText();

        if (type == TYPE_PHONE)
        {
            text = text.replaceAll(" ", "");
        }
        return text;
    }

    public void setText(String value)
    {
        floatEditText.setText(value);
    }
    public boolean isValidated()
    {
        boolean res = false;
        String text = getText();

        if (type == TYPE_EMAIL)
        {
            if (StringUtils.isEmailValid(text))
            {
                res = true;
            }
        }
        else if (type == TYPE_PHONE)
        {
            if (StringUtils.isValidMobileNumber(text))
            {
                res = true;
            }
        }

        return res;
    }

    public void showOK()
    {
        floatEditText.showOK();
    }

    public void hideOK()
    {
        floatEditText.hideOK();
    }

    public void hideError()
    {
        txtError.setVisibility(View.INVISIBLE);
        GradientDrawable drawable = (GradientDrawable)layoutRoot.getBackground();
        drawable.setStroke(0, Utils.getColor(getContext(), R.color.incidence400));
    }

    public void showError(String error)
    {
        txtError.setVisibility(View.VISIBLE);
        txtError.setText(error);

        GradientDrawable drawable = (GradientDrawable)layoutRoot.getBackground();
        drawable.setStroke(Utils.dpToPx(1), Utils.getColor(getContext(), R.color.error));
    }

    public void setTextWatcher(TextWatcher textWatcher)
    {
        this.textWatcher = textWatcher;
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onKeyListener)
    {
        floatEditText.setOnEditorActionListener(onKeyListener);
    }

    public void showDatePickerDialog() {
        Calendar currentCalendar = Calendar.getInstance();
        Locale coreLocale = Core.getLocaleLanguage();
        Locale.setDefault(coreLocale);

        Configuration config = new Configuration();
        config.locale = coreLocale;
        getContext().getResources().updateConfiguration(config, getContext().getResources().getDisplayMetrics());

        final DatePickerDialog dialog = new DatePickerDialog(getContext(), onDateSetListener(), currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogIn, int which) {
                //Your code
                dialog.onClick(dialog, which);
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getContext().getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Your code
                floatEditText.setText("");
            }
        });
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener() {

        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                int year = i;
                int month = i1 + 1;
                int day = i2;
                String fm = "" + month;
                String fd = "" + day;
                if(month < 10){
                    fm = "0" + month;
                }
                if (day < 10){
                    fd = "0" + day;
                }
                floatEditText.setText(fd + "/" + fm + "/" + year);
            }
        };
    }
}
