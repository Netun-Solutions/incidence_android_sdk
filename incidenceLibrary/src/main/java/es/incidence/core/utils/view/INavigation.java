package es.incidence.core.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e510.commons.fragment.BaseFragment;
import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import es.incidence.core.Constants;
import es.incidence.library.IncidenceLibraryManager;

public class INavigation extends RelativeLayout
{
    private BaseFragment baseFragment;

    private RelativeLayout layoutRoot;
    private View naviClickBack;
    private ImageView imgBack;
    private TextView txtTitle;

    private OnClickListener listenerClickBack;

    public INavigation(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public INavigation(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public INavigation(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.layout_inavigation, this, true);

        layoutRoot = view.findViewById(R.id.layoutNaviRoot);
        imgBack = view.findViewById(R.id.naviImgBack);
        naviClickBack = view.findViewById(R.id.naviClickBack);
        naviClickBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBack();
            }
        });
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText(R.string.create_account);

        FontUtils.setTypeValueText(txtTitle, Constants.FONT_SEMIBOLD, getContext());

        IncidenceLibraryManager.instance.setViewBackground(layoutRoot);
        IncidenceLibraryManager.instance.setTextColor(txtTitle);

        setBackHeight();
    }

    public void init(BaseFragment baseFragment, String title, boolean backVisible)
    {
        setBaseFragment(baseFragment);
        setTitle(title);
        FontUtils.setTypeValueText(txtTitle, Constants.FONT_SEMIBOLD, getContext());
        if (backVisible) {
            showBack();
        } else {
            hideBack();
        }
    }

    private void setBackHeight() {

        ViewTreeObserver viewTreeObserver = layoutRoot.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {

            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {

                    // This will be called once the layout is finished, prior to displaying.

                    LayoutParams params = (LayoutParams) naviClickBack.getLayoutParams();
                    params.height = layoutRoot.getHeight();
                    naviClickBack.setLayoutParams(params);

                    // don't need the listener any more
                    layoutRoot.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }
    }

    public void setTitle(String title)
    {
        txtTitle.setText(title);
    }

    public void setTitleColor(int color)
    {
        txtTitle.setTextColor(color);
    }

    public void clearBackground()
    {
        layoutRoot.setBackgroundColor(Utils.getColor(getContext(), android.R.color.transparent));
    }

    public void setBackClickListener(OnClickListener listener)
    {
        naviClickBack.setOnClickListener(listener);
    }

    public void setBaseFragment(BaseFragment baseFragment)
    {
        this.baseFragment = baseFragment;
    }

    public void setListenerClickBack(OnClickListener listener) {
        listenerClickBack = listener;
    }
    private void onClickBack()
    {
        if (listenerClickBack != null)
        {
            listenerClickBack.onClick(null);
        }
        else if (baseFragment != null)
        {
            baseFragment.closeThis();
        }
    }

    public void showBack()
    {
        imgBack.setVisibility(View.VISIBLE);
    }

    public void hideBack()
    {
        imgBack.setVisibility(View.GONE);
    }

    public void setBackColor(int color)
    {
        imgBack.setColorFilter(color);

    }

    public void show() {
        this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        this.setVisibility(View.GONE);
    }
}
