package es.incidence.core.fragment.beacon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.e510.commons.fragment.BaseFragment;
import com.e510.commons.utils.DateUtils;
import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import com.e510.incidencelibrary.R;
import es.incidence.core.adapter.ListAdapter;
import es.incidence.core.domain.Beacon;
import es.incidence.core.entity.ListItem;
import es.incidence.core.entity.ListItemListener;
import es.incidence.core.entity.event.Event;
import es.incidence.core.entity.event.EventCode;
import es.incidence.core.fragment.common.ListFragment;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.utils.view.IButton;
import es.incidence.core.utils.view.INotification;

public class BeaconDataEditFragment extends ListFragment
{
    public static final String KEY_BEACON = "KEY_BEACON";
    public Beacon beacon;

    private boolean hasChanges = false;
    private IButton btnContinue;

    private static final int ROW_NAME = 0;

    public static BeaconDataEditFragment newInstance(Beacon beacon)
    {
        BeaconDataEditFragment fragment = new BeaconDataEditFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_BEACON, beacon);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            beacon = getArguments().getParcelable(KEY_BEACON);
        }
    }

    @Override
    public ListAdapter.Type getType() {
        return ListAdapter.Type.EDITABLE;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        layoutBottom.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_continue_dismiss, null);
        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());
        TextView txtAddLater = view.findViewById(R.id.txtAddLater);
        txtAddLater.setText(getString(R.string.cancel));
        txtAddLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelClose();
            }
        });
        FontUtils.setTypeValueText(txtAddLater, Constants.FONT_SEMIBOLD, getContext());

        btnContinue = view.findViewById(R.id.btnContinueColor);
        btnContinue.setPrimaryColors();
        btnContinue.setText(getString(R.string.save));
        FontUtils.setTypeValueText(btnContinue, Constants.FONT_SEMIBOLD, getContext());
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(false);
            }
        });

        layoutBottom.addView(view);
    }

    @Override
    public void loadData() {
        super.loadData();

        setNavigationTitle(getString(R.string.beacon));
        setNavigationButtonRight(R.drawable.icon_close);
        setNavigationButtonRightTintColor(Utils.getColor(getContext(), R.color.black600));
        setOnNavigationButtonRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRightNavButton();
            }
        });

        if (beacon != null)
        {
            ArrayList<ListItem> temp = new ArrayList<>();

            ListItem l1 = new ListItem(getString(R.string.name), beacon.name);
            l1.object = ROW_NAME;
            l1.listItemListener = new ListItemListener() {
                @Override
                public void onChangeValue() {

                    String name = getItem(ROW_NAME).subtitle;

                    if (name != null && name.length() > 0)
                    {
                        hasChanges = true;
                        btnContinue.setPrimaryColors();
                        btnContinue.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                save(false);
                            }
                        });
                    }
                    else
                    {
                        btnContinue.setDisabledColors();
                        btnContinue.setOnClickListener(null);
                    }
                }
            };
            temp.add(l1);

            renewItems(temp);
        }
    }

    public void onClickRightNavButton()
    {
        if (hasChanges && !btnContinue.isDisabled())
        {
            showSavePopUp(true);
        }
        else
        {
            mListener.cleanAllBackStackEntries();
        }
    }

    @Override
    public boolean onBackPressed()
    {
        if (hasChanges && !btnContinue.isDisabled())
        {
            showSavePopUp(false);

            return true;
        }

        return super.onBackPressed();
    }

    private void cancelClose()
    {
        if (hasChanges && !btnContinue.isDisabled())
        {
            showSavePopUp(false);
        }
        else
        {
            closeThis();
        }
    }

    private void save(boolean cleanAll)
    {
        String name = getItem(ROW_NAME).subtitle;

        showHud();
        Api.updateVehicleBeacon(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {
                hideHud();
                if (response.isSuccess())
                {
                    hasChanges = false;

                    beacon.name = name;
                    EventBus.getDefault().post(new Event(EventCode.BEACON_UPDATED, beacon));
                    if (cleanAll) {
                        mListener.cleanAllBackStackEntries();
                    } else {
                        closeThis();
                    }
                }
                else
                {
                    onBadResponse(response);
                }
            }
        }, beacon, name);
    }

    private void showSavePopUp(boolean cleanAll)
    {
        hideKeyboard();

        String title = getString(R.string.wish_continue);;
        String message = getString(R.string.no_saved_changes);
        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.save_and_close));
        options.add(getString(R.string.no_save));
        ArrayList<Integer> optionsColors = new ArrayList<>();
        optionsColors.add(Utils.getColor(getContext(), R.color.black600));
        optionsColors.add(Utils.getColor(getContext(), R.color.error));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag() != null)
                {
                    int index = (int)view.getTag();
                    if (index == 0)
                    {
                        //save_and_close
                        save(cleanAll);
                    }
                    else if (index == 1)
                    {
                        //no_save
                        hasChanges = false;
                        if (cleanAll) {
                            mListener.cleanAllBackStackEntries();
                        } else {
                            closeThis();
                        }
                    }
                }
            }
        };

        RelativeLayout layoutToShow = getBaseActivity().findViewById(R.id.mainBaseLayout);
        INotification.shared(getContext()).showOptionsNotification(layoutToShow, title, message, options, optionsColors, listener);
    }
}
