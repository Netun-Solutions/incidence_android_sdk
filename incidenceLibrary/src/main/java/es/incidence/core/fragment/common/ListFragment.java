package es.incidence.core.fragment.common;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import java.util.ArrayList;

import es.incidence.core.Constants;
import es.incidence.core.adapter.ListAdapter;
import es.incidence.core.entity.ListItem;
import es.incidence.core.fragment.IFragment;
import es.incidence.core.utils.view.INavigation;
import es.incidence.library.IncidenceLibraryManager;

public class ListFragment extends IFragment
{
    private static final String TAG = makeLogTag(ListFragment.class);

    private RelativeLayout layoutRootFragList;
    private RelativeLayout layoutNavRight;
    private TextView txtNavTitleRight;
    private ImageView imgNavTitleRight;
    private INavigation navigation;

    public RelativeLayout layoutContent;
    public RelativeLayout layoutTopListView;
    private ListView listView;
    public LinearLayout layoutBottom;

    public ArrayList<ListItem> items;
    private ListAdapter adapter;

    public static ListFragment newInstance()
    {
        ListFragment fragment = new ListFragment();

        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
        }
    }

    @Override
    public int getTitleId() {
        return R.string.empty;
    }

    @Override
    public int getLayoutRootId() {
        return R.id.layoutRootFragList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        setupUI(view);

        return view;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        layoutRootFragList = rootView.findViewById(R.id.layoutRootFragList);
        layoutNavRight = rootView.findViewById(R.id.layoutNavRight);
        imgNavTitleRight = rootView.findViewById(R.id.imgNavTitleRight);
        txtNavTitleRight = rootView.findViewById(R.id.txtNavTitleRight);
        FontUtils.setTypeValueText(txtNavTitleRight, Constants.FONT_SEMIBOLD, getContext());

        navigation = rootView.findViewById(R.id.inavigation);
        navigation.init(this, getString(getTitleId()), true);

        layoutContent = rootView.findViewById(R.id.layoutContent);
        layoutTopListView = rootView.findViewById(R.id.layoutTopListView);
        listView = rootView.findViewById(R.id.listView);
        items = new ArrayList<>();
        adapter = new ListAdapter(this, getType(), items);
        listView.setAdapter(adapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                return false;
            }
        });

        layoutBottom = rootView.findViewById(R.id.layoutBottom);

        IncidenceLibraryManager.instance.setViewBackground(layoutRootFragList);
    }

    @Override
    public void loadData()
    {
    }

    @Override
    public void reloadData()
    {
        adapter.notifyDataSetChanged();
    }

    public ListAdapter.Type getType()
    {
        return ListAdapter.Type.TITLE;
    }

    public void renewItems(ArrayList<ListItem> newItems)
    {
        items.clear();
        items.addAll(newItems);
        adapter.notifyDataSetChanged();
    }

    public ListItem getItem(int rowId)
    {
        ListItem res = null;
        for (int i = 0; i < items.size(); i++)
        {
            ListItem listItem = items.get(i);
            if (listItem.object != null && listItem.object instanceof Integer)
            {
                int id = (int) listItem.object;
                if (id == rowId)
                {
                    res = listItem;
                    break;
                }
            }
        }
        return res;
    }

    public void setNavigationTitle(String title)
    {
        navigation.setTitle(title);
    }

    public void setNavigationTitleRight(String title)
    {
        layoutNavRight.setVisibility(View.VISIBLE);
        txtNavTitleRight.setVisibility(View.VISIBLE);
        txtNavTitleRight.setText(title);
    }

    public void setNavigationButtonRight(int drawableImage)
    {
        layoutNavRight.setVisibility(View.VISIBLE);
        imgNavTitleRight.setVisibility(View.VISIBLE);
        imgNavTitleRight.setImageDrawable(Utils.getDrawable(getContext(), drawableImage));
    }

    public void hideNavigationButtonRight()
    {
        layoutNavRight.setVisibility(View.GONE);
        imgNavTitleRight.setVisibility(View.GONE);
    }

    public void setNavigationButtonRightTintColor(int color)
    {
        imgNavTitleRight.setColorFilter(color);
    }

    public void setOnNavigationButtonRightClickListener(View.OnClickListener listener)
    {
        layoutNavRight.setOnClickListener(listener);
    }

    public void clearLayoutBottom()
    {
        layoutBottom.removeAllViews();
    }

    public void addRowBottom(ListItem listItem)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rowView = inflater.inflate(R.layout.row_list, null);
        adapter.createViewHolder(rowView);
        adapter.populateView(rowView, listItem);

        layoutBottom.addView(rowView);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutBottom.getLayoutParams();
        params.bottomMargin = Utils.dpToPx(24);
        layoutBottom.setLayoutParams(params);
    }
}
