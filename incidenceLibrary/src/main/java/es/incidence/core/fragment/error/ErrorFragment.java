package es.incidence.core.fragment.error;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.e510.commons.utils.FontUtils;
import com.e510.incidencelibrary.R;

import es.incidence.core.Constants;
import es.incidence.core.fragment.IFragment;
import es.incidence.core.utils.view.INavigation;
import es.incidence.library.IncidenceLibraryManager;

public class ErrorFragment extends IFragment
{
    private static final String TAG = makeLogTag(ErrorFragment.class);
    public static final String KEY_ERROR = "KEY_ERROR";

    private String error;

    public static ErrorFragment newInstance(String error)
    {
        ErrorFragment fragment = new ErrorFragment();

        Bundle bundle = new Bundle();
        bundle.putString(KEY_ERROR, error);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getTitleId() {
        return R.string.error;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            error = getArguments().getString(KEY_ERROR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_error, container, false);
        setupUI(view);

        return view;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        INavigation navigation = rootView.findViewById(R.id.inavigation);
        navigation.init(this, getString(getTitleId()), true);

        RelativeLayout layoutRootReportIncidence = rootView.findViewById(R.id.layoutRootReportIncidence);

        TextView txtError = rootView.findViewById(R.id.txtError);
        txtError.setText(error);
        FontUtils.setTypeValueText(txtError, Constants.FONT_MEDIUM, getContext());

        IncidenceLibraryManager.instance.setViewBackground(layoutRootReportIncidence);
    }
}