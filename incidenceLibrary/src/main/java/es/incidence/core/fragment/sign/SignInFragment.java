package es.incidence.core.fragment.sign;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.utils.view.IButton;
import es.incidence.core.utils.view.IField;
import es.incidence.core.utils.view.INavigation;
import es.incidence.core.utils.view.SMSView;

public class SignInFragment extends IFragment
{
    private static final String TAG = makeLogTag(SignInFragment.class);

    private static final String STEP_TAG = "STEP_TAG";

    private INavigation navigation;
    private RelativeLayout layoutContainer;
    private TextView txtHeader;
    private IField fieldLogin;
    private IButton btnContinue;
    private TextView txtChange;

    private IButton btnCreate;
    private TextView txtHaveAccount;
    private TextView txtSignIn;

    private final int TYPE_LOGIN_PHONE = 0;
    private final int TYPE_LOGIN_EMAIL = 1;
    private int typeLogin;
    private SMSView smsView;

    public static SignInFragment newInstance()
    {
        SignInFragment fragment = new SignInFragment();

        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getTitleId() {
        return R.string.empty;
    }

    @Override
    public int getLayoutRootId() {
        return R.id.layoutRootSignIn;
    }

    @Override
    public boolean onBackPressed() {
        boolean res = false;

        if (smsView != null)
        {
            layoutContainer.removeAllViews();
            smsView = null;

            res = true;
        }

        return res;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        setupUI(view);

        return view;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        navigation = rootView.findViewById(R.id.inavigation);
        navigation.init(this, getString(R.string.create_account_step1), false);

        layoutContainer = rootView.findViewById(R.id.layoutContainer);
        txtHeader = rootView.findViewById(R.id.txtHeader);
        txtHeader.setText(R.string.phone_title);
        fieldLogin = rootView.findViewById(R.id.fieldLogin);
        fieldLogin.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = fieldLogin.getText();
                if (text != null && text.length() > 0) {
                    btnContinue.setPrimaryColors();
                    btnContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            validateSignIn();
                        }
                    });
                }
                else
                {
                    btnContinue.setDisabledColors();
                    btnContinue.setOnClickListener(null);
                }
            }
        });
        btnContinue = rootView.findViewById(R.id.btnContinue);
        btnContinue.setDisabledColors();
        btnContinue.setText(R.string.sign_in);
        FontUtils.setTypeValueText(btnContinue, Constants.FONT_SEMIBOLD, getContext());


        txtChange = rootView.findViewById(R.id.txtChange);
        txtChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTypeSignIn();
            }
        });

        txtHaveAccount = rootView.findViewById(R.id.txtHaveAccount);
        txtSignIn = rootView.findViewById(R.id.txtSignIn);
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mListener.showInitialFragment(R.id.layout_activity_main, SignHomeFragment.newInstance(0, null));
            }
        });

        FontUtils.setTypeValueText(txtChange, Constants.FONT_SEMIBOLD, getContext());
        FontUtils.setTypeValueText(btnCreate, Constants.FONT_SEMIBOLD, getContext());
        FontUtils.setTypeValueText(txtSignIn, Constants.FONT_SEMIBOLD, getContext());

        printTypeSignIn();
    }

    private void changeTypeSignIn()
    {
        if (typeLogin == TYPE_LOGIN_PHONE)
        {
            typeLogin = TYPE_LOGIN_EMAIL;
        }
        else
        {
            typeLogin = TYPE_LOGIN_PHONE;
        }

        printTypeSignIn();
    }

    private void printTypeSignIn()
    {
        if (typeLogin == TYPE_LOGIN_PHONE)
        {
            navigation.setTitle(getString(R.string.sign_in_phone));
            txtHeader.setText(R.string.phone_title);
            fieldLogin.setHint(getString(R.string.mobile_phone));
            fieldLogin.setTitle(getString(R.string.mobile_phone));
            fieldLogin.setType(IField.TYPE_PHONE);
            txtChange.setText(R.string.sign_in_email_change);
        }
        else
        {
            navigation.setTitle(getString(R.string.sign_in_email));
            txtHeader.setText(R.string.email_title);
            fieldLogin.setHint(getString(R.string.email));
            fieldLogin.setTitle(getString(R.string.email));
            fieldLogin.setType(IField.TYPE_EMAIL);
            txtChange.setText(R.string.sign_in_phone_change);
        }
    }

    private void validateSignIn()
    {
        String valor = fieldLogin.getText();
        if (valor != null)
        {
            String phone = typeLogin == TYPE_LOGIN_PHONE ? valor : null;
            String email = typeLogin == TYPE_LOGIN_EMAIL ? valor : null;

            showHud();
            Api.signIn(new IRequestListener() {
                @Override
                public void onFinish(IResponse response) {
                    hideHud();

                    if (response.isSuccess())
                    {
                        showSMS();
                    }
                    else
                    {
                        //onBadResponse(response);
                        if (response != null && response.message != null)
                        {
                            fieldLogin.showError(response.message);
                        }

                    }
                }
            }, phone, email);
        }
    }

    private void showSMS()
    {
        navigation.showBack();
        smsView = new SMSView(getContext());
        smsView.setTag(STEP_TAG);
        smsView.setTitle(getString(R.string.hola));
        smsView.setHeader(typeLogin == TYPE_LOGIN_PHONE ? getString(R.string.sms_introduce) : getString(R.string.sms_introduce_email));

        smsView.setOnClickAcceptListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String valor = smsView.getText();
                showHud();
                Api.validateCode(new IRequestListener() {
                    @Override
                    public void onFinish(IResponse response) {

                        if (response.isSuccess())
                        {
                            //Core.startApp(getBaseActivity());
                        }
                        else
                        {
                            hideHud();
                            //onBadResponse(response);
                            if (response != null && response.message != null)
                            {
                                smsView.showError(response.message);
                            }
                        }
                    }
                }, valor);
            }
        });
        smsView.setOnClickResendCodeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean toEmail = typeLogin == TYPE_LOGIN_EMAIL ? true : false;

                showHud();
                Api.generateCode(new IRequestListener() {
                    @Override
                    public void onFinish(IResponse response) {
                        hideHud();
                        if (response.isSuccess()) {
                            smsView.restartCodeTimer();
                        }
                        else
                        {
                            onBadResponse(response);
                        }
                    }
                }, toEmail);
            }
        });
        layoutContainer.addView(smsView);
    }
}
