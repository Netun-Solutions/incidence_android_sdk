package es.incidence.core.fragment.common;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.e510.commons.utils.Utils;
import com.e510.commons.utils.config.AppConfiguration;
import com.e510.incidencelibrary.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.ResponseInfo;
import com.mapbox.search.SearchEngine;
import com.mapbox.search.SearchOptions;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.SearchSelectionCallback;
import com.mapbox.search.result.SearchResult;
import com.mapbox.search.result.SearchSuggestion;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.incidence.core.utils.view.IField;
import es.incidence.core.utils.view.INavigation;
import es.incidence.library.IncidenceLibraryManager;

public class MapFullFragment extends MapBoxFragment
{
    private INavigation navigation;
    public IField searchField;
    private ConstraintLayout layoutAutocomplete;
    private ListView autocompleteListview;
    private ArrayAdapter<String> autocompleteAdapter;
    public View bottomSheetLayoutBlur;
    //public BottomSheetLayout bottomSheetLayout;
    private ArrayList<String> items;
    private ArrayList<SearchSuggestion> itemsSearch;
    public ImageView imgCenterMap;

    public boolean enableSearch = true;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 7007;


    private SearchEngine searchEngine = MapboxSearchSdk.createSearchEngine();
    private final SearchOptions options = new SearchOptions.Builder()
            .limit(10)
            .build();
    private SearchRequestTask searchRequestTask;

    public static MapFullFragment newInstance()
    {
        MapFullFragment fragment = new MapFullFragment();

        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
        }

        Mapbox.getInstance(getContext(), IncidenceLibraryManager.instance.getMapboxAccessToken());
    }

    @Override
    public int getTitleId() {
        return R.string.incidence_key_empty;
    }

    @Override
    public int getLayoutRootId() {
        return R.id.layoutRootMap;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        setupUI(view);
        overrideOnCreateView(view, savedInstanceState);

        return view;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (enableSearch)
                {
                    String text = searchField.getText();
                    search(text);
                }
            }
        };

        navigation = rootView.findViewById(R.id.inavigation);
        navigation.init(this, getString(R.string.incidence_key_back), true);
        navigation.setTitleColor(Utils.getColor(getContext(), android.R.color.white));
        navigation.setBackColor(Utils.getColor(getContext(), android.R.color.white));

        searchField = rootView.findViewById(R.id.field);
        searchField.setWithGradient(false);
        searchField.setWithValidation(false);
        searchField.setImageLeft(Utils.getDrawable(getContext(), R.drawable.icon_search));
        searchField.setHint(getString(R.string.incidence_key_search_or_move_map));
        searchField.setVisibility(View.VISIBLE);
        layoutAutocomplete = rootView.findViewById(R.id.layoutAutocomplete);
        int radius = Integer.parseInt(AppConfiguration.getInstance().appearance.roundedFields);
        GradientDrawable back =  Utils.createGradientDrawable(getContext(), android.R.color.white, 0);
        back.setCornerRadii(new float [] {
                0, 0,
                0, 0,
                radius, radius,
                radius, radius});
        layoutAutocomplete.setBackground(back);
        autocompleteListview = rootView.findViewById(R.id.autocompleteListview);
        items = new ArrayList<>();
        autocompleteAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
        autocompleteListview.setAdapter(autocompleteAdapter);
        autocompleteListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (itemsSearch != null && itemsSearch.size() > i)
                {
                    SearchSuggestion ss = itemsSearch.get(i);
                    searchRequestTask = searchEngine.select(ss, new SearchSelectionCallback() {
                        @Override
                        public void onResult(@NotNull SearchSuggestion searchSuggestion, @NotNull SearchResult searchResult, @NotNull ResponseInfo responseInfo) {

                            if (searchResult != null)
                            {
                                drawPoint(searchResult.getCoordinate());

                                searchField.setTextWatcher(null);
                                searchField.setText(searchResult.getName());
                                searchField.showOK();
                                searchField.setTextWatcher(textWatcher);

                                items.clear();
                                autocompleteAdapter.notifyDataSetChanged();
                                layoutAutocomplete.setVisibility(View.GONE);
                                hideKeyboard();

                                onSearched(searchResult);
                            }
                        }
                        @Override
                        public void onCategoryResult(@NotNull SearchSuggestion searchSuggestion, @NotNull List<? extends SearchResult> list, @NotNull ResponseInfo responseInfo) {
                        }
                        @Override
                        public void onSuggestions(@NotNull List<? extends SearchSuggestion> list, @NotNull ResponseInfo responseInfo) {
                        }
                        @Override
                        public void onError(@NotNull Exception e) {
                        }
                    });
                }
            }
        });

        searchField.setTextWatcher(textWatcher);

        //bottomSheetLayoutBlur = rootView.findViewById(R.id.bottomSheetLayoutBlur);
        //bottomSheetLayout = rootView.findViewById(R.id.bottomSheetLayout);
        imgCenterMap = rootView.findViewById(R.id.imgCenterMap);
        imgCenterMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgCenterMap.setVisibility(View.GONE);
                centerMap();
            }
        });
        addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                imgCenterMap.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onSearched(SearchResult searchResult)
    {
    }

    private void search(String text)
    {
        if (text != null && text.length() > 0)
        {
            if (searchRequestTask != null) {
                searchRequestTask.cancel();
            }

            searchRequestTask = searchEngine.search(text, options, new SearchSelectionCallback() {
                @Override
                public void onResult(@NotNull SearchSuggestion searchSuggestion, @NotNull SearchResult searchResult, @NotNull ResponseInfo responseInfo) {
                }
                @Override
                public void onCategoryResult(@NotNull SearchSuggestion searchSuggestion, @NotNull List<? extends SearchResult> list, @NotNull ResponseInfo responseInfo) {
                }
                @Override
                public void onSuggestions(@NotNull List<? extends SearchSuggestion> list, @NotNull ResponseInfo responseInfo) {

                    ArrayList<SearchSuggestion> tempSearch = new ArrayList<>();
                    ArrayList<String> temp = new ArrayList<>();
                    if (list != null)
                    {
                        for (int i = 0; i < list.size(); i++)
                        {
                            SearchSuggestion s = list.get(i);
                            temp.add(s.getName());
                            tempSearch.add(s);
                        }
                    }

                    itemsSearch = tempSearch;
                    items.clear();
                    items.addAll(temp);
                    autocompleteAdapter.notifyDataSetChanged();
                    layoutAutocomplete.setVisibility(items.size() > 0 ? View.VISIBLE : View.GONE);
                }

                @Override
                public void onError(@NotNull Exception e) {
                }
            });
        }
        else
        {
            items.clear();
            autocompleteAdapter.notifyDataSetChanged();
            layoutAutocomplete.setVisibility(View.GONE);
        }
    }
}
