package es.incidence.core.utils.view;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import es.incidence.core.Constants;
import es.incidence.core.Core;

public class TermsView extends RelativeLayout
{
    private RelativeLayout layoutRoot;
    private RelativeLayout layoutBtn;
    private RelativeLayout layoutcheck1;
    private RelativeLayout layoutcheck2;

    private TextView txtSubtitle;
    private TextView txtDescription;
    private ImageView check1;
    private ImageView check2;

    private boolean check1Checked;
    private boolean check2Checked;

    private IButton btnAccept;
    private OnClickListener listener;

    public TermsView(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public TermsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TermsView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.layout_terms, this, true);

        layoutRoot = view.findViewById(R.id.layoutRoot);
        layoutBtn = view.findViewById(R.id.layoutBtn);
        layoutcheck1 = view.findViewById(R.id.layoutcheck1);
        check1 = view.findViewById(R.id.check1);
        check1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                check1Checked = !check1Checked;
                check1.setImageDrawable(Utils.getDrawable(getContext(), check1Checked ? R.drawable.checkbox_on : R.drawable.checkbox_off));
                checkAccept();
            }
        });
        layoutcheck2 = view.findViewById(R.id.layoutcheck2);
        check2 = view.findViewById(R.id.check2);
        check2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                check2Checked = !check2Checked;
                check2.setImageDrawable(Utils.getDrawable(getContext(), check2Checked ? R.drawable.checkbox_on : R.drawable.checkbox_off));
                checkAccept();
            }
        });

        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());

        txtSubtitle = view.findViewById(R.id.txtSubtitle);
        FontUtils.setTypeValueText(txtSubtitle, Constants.FONT_SEMIBOLD, getContext());

        //Necesitamos activity para que agarre el string en el idioma del usuario.
        String strDesc = Core.getString(R.string.terms_description, context);
        String strAccept = Core.getString(R.string.acepto, context);

        txtDescription = view.findViewById(R.id.txtDescription);
        txtSubtitle.setVisibility(View.INVISIBLE);
        txtDescription.setText(Html.fromHtml(strDesc));
        txtDescription.setMovementMethod(LinkMovementMethod.getInstance());

        btnAccept = view.findViewById(R.id.btnAccept);
        btnAccept.setText(strAccept);
        FontUtils.setTypeValueText(btnAccept, Constants.FONT_SEMIBOLD, getContext());
        checkAccept();
        btnAccept.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllChecked())
                {
                    if(listener != null) {
                        listener.onClick(view);
                    }
                }
            }
        });
    }

    public boolean isAllChecked()
    {
        return check1Checked;// && check2Checked;
    }

    public boolean isAdvertisingChecked()
    {
        return check2Checked;// && check2Checked;
    }

    private void checkAccept()
    {
        if (isAllChecked())
        {
            btnAccept.setPrimaryColors();
            btnAccept.setClickable(true);
        }
        else
        {
            btnAccept.setDisabledColors();
            btnAccept.setClickable(false);
        }
    }

    public void setOnClickAcceptListener(OnClickListener listener)
    {
        this.listener = listener;
    }

    public void setOnlyRead()
    {
        txtSubtitle.setVisibility(View.GONE);
        layoutBtn.setVisibility(View.GONE);
        layoutcheck1.setVisibility(View.GONE);
        layoutcheck2.setVisibility(View.GONE);
    }
}
