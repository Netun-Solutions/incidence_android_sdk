package es.incidence.core.fragment;

import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e510.commons.fragment.BaseFragment;
import com.e510.commons.utils.FontUtils;
import com.e510.incidencelibrary.R;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import es.incidence.core.interfaces.OnHtmlMessageClickListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.utils.IUtils;
import es.incidence.core.utils.view.IButton;
import es.incidence.core.utils.view.INotification;

public class IFragment extends BaseFragment {

    protected View popupError;

    @Override
    public void setupUI(View rootView) {
        FontUtils.setTypeValueText(rootView, Constants.FONT_REGULAR, getContext());
    }

    @Override
    public void showHud() {
        hideKeyboard();
        super.showHud();
    }

    public void onBadResponse(IResponse response) {
        onBadResponse(response, null);
    }
    public void onBadResponse(IResponse response, DialogInterface.OnClickListener listener)
    {
        if (response != null)
        {

            if (response.action != null)
            {
                if (response.action.equals(Constants.WS_RESPONSE_ACTION_INVALID_SESSION))
                {
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Core.signOut();
                        }
                    };
                }
            }

            if (response.message != null)
            {
                showAlert(IUtils.getAppLable(getContext()), response.message, listener);
            }
            else if (response.status != null && response.status.equals(IResponse.RESPONSE_ERROR_CONNECTION))
            {
                showAlert(IUtils.getAppLable(getContext()), getString(R.string.incidence_key_alert_error_ws_connection), listener);
            }
            else
            {
                showAlert(IUtils.getAppLable(getContext()), getString(R.string.incidence_key_alert_error_ws), listener);
            }
        }
    }

    public void showAlertHtml(String title, String message) {
        showAlertHtml(title, message, null);
    }
    public void showAlertHtml(String title, String message, OnHtmlMessageClickListener listener)
    {
        /*
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.nombre_app))
                .setCancelable(false)
                .setNegativeButton(listenerTitle, listener)
                .create();

        TextView view = new TextView(dialog.getContext());
        view.setText(Html.fromHtml(message));
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setPadding(80, 30, 80, 30);
        view.setTextColor(getColor(R.color.black600));
        view.setTextSize(16);

        dialog.setView(view);


        if (listener == null)
        {
            listener = new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.cancel();
                }
            };
        }


        dialog.show();
        */

        LayoutInflater inflater = LayoutInflater.from(getContext());
        RelativeLayout layoutToShow = getBaseActivity().findViewById(R.id.layout_popup);
        popupError = inflater.inflate(R.layout.layout_popup_error, null);


        RelativeLayout popupContainer = popupError.findViewById(R.id.popupContainer);
        TextView txtTitle = popupContainer.findViewById(R.id.txtTitle);
        txtTitle.setText(title);
        TextView txtSubTitle = popupContainer.findViewById(R.id.txtSubTitle);
        txtSubTitle.setText(Html.fromHtml(message));
        txtSubTitle.setMovementMethod(LinkMovementMethod.getInstance());

        IButton btnBlue = popupContainer.findViewById(R.id.btnBlue);

        FontUtils.setTypeValueText(txtTitle, Constants.FONT_SEMIBOLD, getContext());
        FontUtils.setTypeValueText(txtSubTitle, Constants.FONT_REGULAR, getContext());

        btnBlue.setVisibility(View.VISIBLE);
        btnBlue.setText(getString(R.string.incidence_key_accept));
        btnBlue.setPrimaryColors();
        FontUtils.setTypeValueText(btnBlue, Constants.FONT_SEMIBOLD, getContext());
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutToShow.removeView(popupError);
                if (listener != null) {
                    listener.htmlMessageClick();
                }
            }
        });

        layoutToShow.addView(popupError);
        //INotification.shared(getContext()).showNotification(layoutToShow, message);
    }

    @Override
    public boolean onBackPressed() {

        if (INotification.shared(getContext()).isShowing())
        {
            INotification.shared(getContext()).hide();
            return true;
        }

        return super.onBackPressed();
    }
}
