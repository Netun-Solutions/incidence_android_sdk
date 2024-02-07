package es.incidence.core.fragment;

import android.content.DialogInterface;
import android.view.View;

import com.e510.commons.fragment.BaseFragment;
import com.e510.commons.utils.FontUtils;
import com.e510.incidencelibrary.R;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import es.incidence.core.manager.IResponse;
import es.incidence.core.utils.IUtils;
import es.incidence.core.utils.view.INotification;

public class IFragment extends BaseFragment {

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
