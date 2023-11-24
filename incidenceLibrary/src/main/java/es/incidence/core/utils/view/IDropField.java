package es.incidence.core.utils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.commons.utils.config.AppConfiguration;
import com.e510.commons.view.FloatLabeled.FloatEditText;
import com.e510.incidencelibrary.R;

import es.incidence.core.Constants;

public class IDropField extends LinearLayout
{
    private float radius;

    private int menuId;
    private LinearLayout layoutRoot;
    private RelativeLayout layoutDrop;
    private TextView txtDrop;
    private FloatEditText floatEditText;
    private TextView txtError;
    private RelativeLayout layoutClick;
    private DropMenuListener dropMenuListener;
    private OnFocusChangeListener onFocusChangeListener;

    public IDropField(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public IDropField(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public IDropField(Context context, AttributeSet attrs, int defStyleAttr)
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
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.layout_idropfield, this, true);

        layoutRoot = view.findViewById(R.id.layoutRoot);
        txtDrop = view.findViewById(R.id.txtDrop);
        layoutDrop = view.findViewById(R.id.layoutDrop);
        layoutDrop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDrop();
            }
        });
        floatEditText = view.findViewById(R.id.textInputLayout);
        floatEditText.setTitleColor(Utils.getColor(getContext(), R.color.black500));
        floatEditText.setHintColor(Utils.getColor(getContext(), R.color.black400));
        floatEditText.setTextColor(Utils.getColor(getContext(), R.color.black600));
        floatEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                {
                    GradientDrawable drawable = (GradientDrawable)layoutRoot.getBackground();
                    drawable.setStroke(0, Utils.getColor(getContext(), R.color.incidence400));
                }
                else
                {
                    hideOK();
                    hideError();
                    
                    GradientDrawable drawable = (GradientDrawable)layoutRoot.getBackground();
                    drawable.setStroke(Utils.dpToPx(1), Utils.getColor(getContext(), R.color.incidence400));
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

    public void setBackground(Context context, int backgroundColor)
    {
        GradientDrawable back =  Utils.createGradientDrawable(context, backgroundColor, (int) radius);
        layoutRoot.setBackground(back);
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

    public void setTitle(String title)
    {
        floatEditText.setHintTitle(title);

    }

    public void setHint(String hint)
    {
        floatEditText.setHintInput(hint);
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

    public void showOK()
    {
        floatEditText.showOK();
    }

    public void hideOK()
    {
        floatEditText.hideOK();
    }

    public void setMenu(int menu)
    {
        this.menuId = menu;
    }

    public void setMenuTitle(String title)
    {
        this.txtDrop.setText(title);
    }
    public String getMenuTitle()
    {
        return this.txtDrop.getText().toString();
    }

    private void showDrop()
    {
        PopupMenu popup = new PopupMenu(getContext(), txtDrop);
        popup.getMenuInflater().inflate(menuId, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                txtDrop.setText(item.getTitle());
                if (dropMenuListener != null)
                {
                    dropMenuListener.onItemSelected(txtDrop.getText().toString());
                }
                return true;
            }
        });

        popup.show();
    }

    public interface DropMenuListener
    {
        void onItemSelected(String value);
    }

    public void setOnMenuChangeListener(DropMenuListener dropMenuListener)
    {
        this.dropMenuListener = dropMenuListener;
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

    public String getText() {
        return floatEditText.getText();
    }
    public void setText(String value)
    {
        floatEditText.setText(value);
    }

    public void setTextWatcher(TextWatcher textWatcher)
    {
        floatEditText.setTextWatcher(textWatcher);
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onKeyListener)
    {
        floatEditText.setOnEditorActionListener(onKeyListener);
    }

    public void setImeOptions(int options)
    {
        floatEditText.setImeOptions(options);
    }
}
