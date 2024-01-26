package es.incidence.core.fragment.ecommerce;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import java.util.ArrayList;

import es.incidence.core.Constants;
import es.incidence.core.domain.EcommerceItem;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.fragment.IFragment;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.manager.ImageManager;
import es.incidence.core.utils.CustomSuperscriptSpan;
import es.incidence.core.utils.view.IButton;
//import es.incidence.core.utils.view.ICirclePageIndicator;
import es.incidence.core.utils.view.INavigation;
import es.incidence.library.IncidenceLibraryManager;

public class EcommerceFragment extends IFragment
{
    private static final String TAG = makeLogTag(EcommerceFragment.class);

    public static final String KEY_AUTO_SELECTED_VEHICLE = "KEY_AUTO_SELECTED_VEHICLE";
    public static final String KEY_AUTO_SELECTED_USER = "KEY_AUTO_SELECTED_USER";

    private INavigation navigation;
    private IButton button;
    //private ICirclePageIndicator circlePageIndicator;
    private ViewPager viewPager;
    private SliderPagerAdapter adapter;
    private ArrayList<EcommerceItem> items = new ArrayList<>();
    private EcommerceItem selectedItem = null;

    public Vehicle autoSelectedVehicle;
    public User autoSelectedUser;

    public static EcommerceFragment newInstance(Vehicle vehicle, User user)
    {
        EcommerceFragment fragment = new EcommerceFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_AUTO_SELECTED_VEHICLE, vehicle);
        bundle.putParcelable(KEY_AUTO_SELECTED_USER, user);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getTitleId() {
        return R.string.incidence_key_buy_beacon;
    }

    @Override
    public int getLayoutRootId() {
        return R.id.layoutRootEcommerce;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            autoSelectedVehicle = getArguments().getParcelable(KEY_AUTO_SELECTED_VEHICLE);
            autoSelectedUser = getArguments().getParcelable(KEY_AUTO_SELECTED_USER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_ecommerce, container, false);
        setupUI(view);

        return view;
    }

    @Override
    public void setupUI(View rootView)
    {
        super.setupUI(rootView);

        navigation = rootView.findViewById(R.id.inavigation);
        navigation.init(this, getString(getTitleId()), true);

        button = rootView.findViewById(R.id.btn);
        button.setPrimaryColors();
        FontUtils.setTypeValueText(button, Constants.FONT_SEMIBOLD, getContext());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink();
            }
        });

        //circlePageIndicator = rootView.findViewById(R.id.circlePageIndicator);
        viewPager = rootView.findViewById(R.id.view_pager);

        adapter = new SliderPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position)
            {
                selectedItem = items.get(position);
                button.setText(selectedItem.title_button);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //circlePageIndicator.setViewPager(viewPager);

        IncidenceLibraryManager.instance.setViewBackground(rootView);
    }

    @Override
    public void loadData()
    {
        showHud();
        Api.getEcommercesSdk(new IRequestListener() {
            @Override
            public void onFinish(IResponse response)
            {
                hideHud();
                ArrayList<EcommerceItem> temp = new ArrayList<>();
                if ((response.isSuccess()))
                {
                    temp = response.getList("items", EcommerceItem.class);
                }

                items.clear();
                items.addAll(temp);
                adapter.notifyDataSetChanged();
                if (items.size() > 0) {
                    viewPager.setCurrentItem(0);
                    selectedItem = items.get(0);
                    button.setText(selectedItem.title_button);
                }
            }
        }, autoSelectedUser, autoSelectedVehicle);
    }

    private void openLink()
    {
        if (selectedItem != null && selectedItem.link != null)
        {
            //mListener.addFragmentAnimated(WebFragment.newInstance(selectedItem.link, getString(getTitleId())));
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedItem.link));
            startActivity(browserIntent);
        }
    }

    private class SliderPagerAdapter extends PagerAdapter {

        public SliderPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position)
        {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.slide_ecommerce, container, false);
            ScrollView layoutCard = view.findViewById(R.id.layoutCardEcom);
            ImageView imgView = view.findViewById(R.id.imgView);
            TextView txtTitle = view.findViewById(R.id.txtSlideTitle);
            TextView txtSubtitle = view.findViewById(R.id.txtSlideSubtitle);
            TextView txtPrice = view.findViewById(R.id.txtSlidePrice);
            LinearLayout txtSlidePriceOffer = view.findViewById(R.id.txtSlidePriceOffer);
            TextView txtSlidePriceOld = view.findViewById(R.id.txtSlidePriceOld);
            TextView txtSlidePriceNew = view.findViewById(R.id.txtSlidePriceNew);
            TextView txtSlidePriceSubtext = view.findViewById(R.id.txtSlidePriceSubtext);

            GradientDrawable back =  Utils.createGradientDrawable(getContext(), android.R.color.white, Utils.dpToPx(5));
            layoutCard.setBackground(back);

            FontUtils.setTypeValueText(txtTitle, Constants.FONT_SEMIBOLD, getContext());
            FontUtils.setTypeValueText(txtSubtitle, Constants.FONT_REGULAR, getContext());
            FontUtils.setTypeValueText(txtPrice, Constants.FONT_SEMIBOLD, getContext());
            FontUtils.setTypeValueText(txtSlidePriceOld, Constants.FONT_MEDIUM, getContext());
            FontUtils.setTypeValueText(txtSlidePriceNew, Constants.FONT_MEDIUM, getContext());
            FontUtils.setTypeValueText(txtSlidePriceSubtext, Constants.FONT_REGULAR, getContext());

            txtSlidePriceOld.setPaintFlags(txtSlidePriceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            EcommerceItem item = items.get(position);

            txtTitle.setText(item.title);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txtSubtitle.setText(Html.fromHtml(item.text, Html.FROM_HTML_MODE_COMPACT) );
            } else {
                txtSubtitle.setText(Html.fromHtml(item.text));
            }

            if (item.image != null)
            {
                ImageManager.loadFitImage(getContext(), item.image, imgView);
            }
            else
            {
                imgView.setImageDrawable(null);
            }

            if (!"0,00".equals(item.offer_price)) {
                txtSlidePriceOld.setText(item.price + " €");

                //String value = "10 €";
                //String value = "31,45 €";
                String value = item.offer_price + " €";
                String characterDecimal = ",";

                int indexOf = value.indexOf(characterDecimal);
                if (indexOf!=-1) {
                    final SpannableString text = new SpannableString(value);
                    text.setSpan(new RelativeSizeSpan(0.5f), indexOf, value.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //text.setSpan(new ForegroundColorSpan(Color.RED), value.indexOf(".")+1, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    text.setSpan(new CustomSuperscriptSpan(), indexOf, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    txtSlidePriceNew.setText(text);
                } else {
                    txtSlidePriceNew.setText(value);
                }

                txtPrice.setVisibility(View.GONE);
                txtSlidePriceOffer.setVisibility(View.VISIBLE);
            } else {
                txtPrice.setText(item.price + " €");

                txtPrice.setVisibility(View.VISIBLE);
                txtSlidePriceOffer.setVisibility(View.GONE);
            }

            container.addView(view);

            return view;
        }

        @Override
        public int getCount()
        {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
