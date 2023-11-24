package es.incidence.core.fragment.sign;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import java.util.ArrayList;

import es.incidence.core.Constants;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.entity.sign.SignStep;
import es.incidence.core.entity.sign.SignStepType;
import es.incidence.core.fragment.IFragment;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.utils.view.IButton;
import es.incidence.core.utils.view.IDropField;
import es.incidence.core.utils.view.IField;
import es.incidence.core.utils.view.INavigation;
import es.incidence.core.utils.view.IStepper;
import es.incidence.core.utils.view.SMSView;
import es.incidence.core.utils.view.TermsView;

public class SignUpFragment extends IFragment
{
    private static final String TAG = makeLogTag(SignUpFragment.class);

    private static final String STEP_TAG = "STEP_TAG";

    public INavigation navigation;
    public IStepper stepper;

    public RelativeLayout layoutRootSignUp;
    private RelativeLayout layoutContainer;
    private TextView txtHeader;
    private LinearLayout layoutFields;
    private IButton btnContinue;
    private RelativeLayout layoutSuccessSignUp;


    public ArrayList<SignStep> steps = new ArrayList<>();
    public int positionStep;
    public SignStep currentStep;
    public Vehicle newVehicleCreated;

    //fields person
    public static final int ID_NAME = 1001;
    public static final int ID_PHONE = 1002;
    public static final int ID_TERMS = 1003;
    public static final int ID_SMS = 1004;
    public static final int ID_DNI = 1005;
    public static final int ID_EMAIL = 1006;

    //fields vehicle
    public static final int ID_VEHICLE_TYPE = 2001;
    public static final int ID_VEHICLE_MATRICULA = 2002;
    public static final int ID_VEHICLE_COMPRUEBA = 2003;
    public static final int ID_VEHICLE_COLOR = 2004;
    public static final int ID_VEHICLE_LIST_INSURANCE = 2005;
    public static final int ID_VEHICLE_COMPLETE_INSURANCE = 2006;

    //fields beacon
    public static final int ID_BEACON_SELECT_TYPE = 3001;
    public static final int ID_BEACON_POWER = 3002;

    @Override
    public int getTitleId() {
        return R.string.empty;
    }

    @Override
    public int getLayoutRootId() {
        return R.id.layoutRootSignUp;
    }

    public int getStepBlock()
    {
        return 0;
    }

    public Boolean fromAddBeacon()
    {
        return false;
    }

    public int getTotalSteps() {
        return 3;
    }

    public boolean isRegistration()
    {
        return true;
    }

    @Override
    public boolean onBackPressed() {
        boolean res = false;

        if (positionStep > 0)
        {
            res = true;
            printPreviousStep();
        }

        return res;
    }

    @Override
    public void closeThis() {
        positionStep = 0;
        super.closeThis();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        setupUI(view);

        return view;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        navigation = rootView.findViewById(R.id.inavigation);
        navigation.init(this, getString(R.string.create_account_step1), true);
        navigation.setBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onBackPressed()) {
                    closeThis();
                }
            }
        });

        layoutRootSignUp = rootView.findViewById(R.id.layoutRootSignUp);

        stepper = rootView.findViewById(R.id.istepper);
        float[] steps = new float[]{0.5f, 0f, 0f};
        stepper.init(this, steps);

        layoutContainer = rootView.findViewById(R.id.layoutContainer);
        txtHeader = rootView.findViewById(R.id.txtHeader);
        layoutFields = rootView.findViewById(R.id.layoutFields);
        btnContinue = rootView.findViewById(R.id.btnContinue);
        btnContinue.setText(getString(R.string.continuar));
        validateContinue(false);
        FontUtils.setTypeValueText(btnContinue, Constants.FONT_SEMIBOLD, getContext());

        rootView.findViewById(R.id.layoutScroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.checkAndCloseKeyboard();
            }
        });

        layoutSuccessSignUp = rootView.findViewById(R.id.layoutSuccessSignUp);
        IButton btnSuccessSignUp = layoutSuccessSignUp.findViewById(R.id.btnSuccessSignUp);
        FontUtils.setTypeValueText(btnSuccessSignUp, Constants.FONT_SEMIBOLD, getContext());
        btnSuccessSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go home
                //Core.startApp(getBaseActivity());
            }
        });
        TextView txtHeader = layoutSuccessSignUp.findViewById(R.id.txtHeader);
        FontUtils.setTypeValueText(txtHeader, Constants.FONT_SEMIBOLD, getContext());
        ImageView txtStep1check = layoutSuccessSignUp.findViewById(R.id.txtStep1check);
        DrawableCompat.setTint(txtStep1check.getDrawable(), Utils.getColor(getContext(), android.R.color.white));
        ImageView txtStep2check = layoutSuccessSignUp.findViewById(R.id.txtStep2check);
        txtStep2check.setImageDrawable(txtStep1check.getDrawable());
        ImageView txtStep3check = layoutSuccessSignUp.findViewById(R.id.txtStep3check);
        txtStep3check.setImageDrawable(txtStep1check.getDrawable());
    }

    public void validateContinue(boolean canClick)
    {
        if (canClick)
        {
            btnContinue.setPrimaryColors();
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if (currentStep.validation != null)
                    {
                        currentStep.validation.validate();
                    }
                    else
                    {
                        printNextStep();
                    }
                }
            });
        }
        else
        {
            btnContinue.setDisabledColors();
            btnContinue.setOnClickListener(null);
        }
    }

    public SignStep getStep(int id)
    {
        SignStep step = null;
        if (steps != null)
        {
            for (int i = 0; i < steps.size(); i++)
            {
                SignStep s = steps.get(i);
                if (s.id == id)
                {
                    step = s;
                    break;
                }
            }
        }
        return step;
    }

    public void printNextStep()
    {
        if (steps.size() > positionStep+1)
        {
            positionStep++;
            printStep(false);
        }
        else
        {
            printNextBlock();
        }
    }

    public void printPreviousStep()
    {
        if (positionStep > 0)
        {
            positionStep--;
            printStep(true);
        }
    }

    public void printNextBlock()
    {
        int nextBlock = getStepBlock()+1;
        if (nextBlock < getTotalSteps())
        {
            //mListener.addFullFragmentAnimated(SignHomeFragment.newInstance(nextBlock, newVehicleCreated));
        }
        else
        {
            //registro completo.
            getBaseActivity().setToolbarColor(Utils.getColor(getContext(), R.color.incidencePrincipal), Utils.getColor(getContext(), android.R.color.white), false);
            layoutSuccessSignUp.setVisibility(View.VISIBLE);
        }
    }

    private Animation getStepAnimation(boolean isBack)
    {
        View viewOld1 = layoutFields.findViewWithTag(STEP_TAG);
        View viewOld2 = layoutContainer.findViewWithTag(STEP_TAG);

        Animation slide = AnimationUtils.loadAnimation(getActivity(), isBack ? R.anim.enter_from_left : R.anim.enter_from_right);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (viewOld1 != null) {
                    viewOld1.setVisibility(View.GONE);
                }
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (viewOld1 != null)
                {
                    layoutFields.removeView(viewOld1);
                }
                if (viewOld2 != null)
                {
                    layoutContainer.removeView(viewOld2);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        return slide;
    }

    public void printStep(boolean isBack)
    {
        printStep(isBack, true);
    }
    public void printStep(boolean isBack, boolean animated)
    {
        if (steps.size() > positionStep)
        {
            Animation slide = getStepAnimation(isBack);
            SignStep signStep = steps.get(positionStep);
            currentStep = signStep;

            if (signStep.type == SignStepType.FIELD)
            {
                navigation.setTitle(signStep.navigationTitle);
                layoutContainer.setVisibility(View.GONE);

                txtHeader.setText(signStep.title);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) txtHeader.getLayoutParams();
                params.bottomMargin = Utils.dpToPx(120);
                txtHeader.setLayoutParams(params);

                //layoutFields.removeAllViews();
                //layoutContainer.removeAllViews();

                IField field = (IField) signStep.customView;
                if (field == null)
                {
                    IField tField = new IField(getContext());
                    field = tField;
                    signStep.customView = field;
                    tField.setTag(STEP_TAG);
                    tField.setTitle(signStep.titleField);
                    tField.setHint(signStep.hintField);
                    tField.setType(signStep.typeField);
                    tField.setTextWatcher(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {
                            String text = tField.getText();
                            validateContinue((text != null && text.length() > 0));
                        }
                    });
                }

                String text = field.getText();
                validateContinue((text != null && text.length() > 0));
                field.setVisibility(View.VISIBLE);

                if(field.getParent() != null) {
                    ((ViewGroup)field.getParent()).removeView(field); // <- fix
                }
                layoutFields.addView(field);

                if (animated)
                {
                    field.startAnimation(slide);
                    txtHeader.startAnimation(slide);
                    btnContinue.startAnimation(slide);
                }
            }
            else if (signStep.type == SignStepType.DROPFIELD)
            {
                navigation.setTitle(signStep.navigationTitle);
                layoutContainer.setVisibility(View.GONE);

                txtHeader.setText(signStep.title);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) txtHeader.getLayoutParams();
                params.bottomMargin = Utils.dpToPx(120);
                txtHeader.setLayoutParams(params);

                //layoutFields.removeAllViews();
                //layoutContainer.removeAllViews();

                IDropField field = (IDropField) signStep.customView;

                if (field == null)
                {
                    IDropField tField = new IDropField(getContext());
                    field = tField;
                    signStep.customView = field;
                    tField.setTag(STEP_TAG);
                    tField.setTitle(signStep.titleField);
                    tField.setHint(signStep.hintField);
                    tField.setMenu(signStep.typeField);
                    tField.setMenuTitle(signStep.titleMenuField);
                    tField.setTextWatcher(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {
                            String text = tField.getText();
                            validateContinue((text != null && text.length() > 0));
                        }
                    });
                }

                String text = field.getText();
                validateContinue((text != null && text.length() > 0));
                field.setVisibility(View.VISIBLE);

                if(field.getParent() != null) {
                    ((ViewGroup)field.getParent()).removeView(field); // <- fix
                }
                layoutFields.addView(field);

                if (animated)
                {
                    field.startAnimation(slide);
                    txtHeader.startAnimation(slide);
                    btnContinue.startAnimation(slide);
                }
            }
            else if (signStep.type == SignStepType.TERMS)
            {
                navigation.setTitle(signStep.navigationTitle);
                layoutContainer.setVisibility(View.VISIBLE);

                //layoutFields.removeAllViews();
                //layoutContainer.removeAllViews();

                TermsView termsView = new TermsView(getContext());
                signStep.customView = termsView;
                termsView.setTag(STEP_TAG);
                termsView.setOnClickAcceptListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        if (signStep.validation != null)
                        {
                            signStep.validation.validate();
                        }
                        else
                        {
                            printNextStep();
                        }
                    }
                });

                if(termsView.getParent() != null) {
                    ((ViewGroup)termsView.getParent()).removeView(termsView); // <- fix
                }
                layoutContainer.addView(termsView);

                if (animated)
                {
                    termsView.startAnimation(slide);
                }
            }
            else if (signStep.type == SignStepType.SMS)
            {
                navigation.setTitle(signStep.navigationTitle);
                layoutContainer.setVisibility(View.VISIBLE);

                //layoutFields.removeAllViews();
                //layoutContainer.removeAllViews();

                SMSView smsView = new SMSView(getContext());
                signStep.customView = smsView;
                smsView.setTitle(getString(R.string.hola));
                smsView.setTag(STEP_TAG);
                smsView.setOnClickAcceptListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String valor = smsView.getText();
                        showHud();
                        Api.validateCode(new IRequestListener() {
                            @Override
                            public void onFinish(IResponse response) {
                                hideHud();

                                if (response.isSuccess())
                                {
                                    printNextStep();
                                }
                                else
                                {
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
                        }, false);
                    }
                });

                if(smsView.getParent() != null) {
                    ((ViewGroup)smsView.getParent()).removeView(smsView); // <- fix
                }
                layoutContainer.addView(smsView);

                if (animated)
                {
                    smsView.startAnimation(slide);
                }
            }
            else if (signStep.type == SignStepType.CUSTOM)
            {
                navigation.setTitle(signStep.navigationTitle);


                //layoutFields.removeAllViews();
                //layoutContainer.removeAllViews();

                if (signStep.customFullView != null)
                {
                    layoutContainer.setVisibility(View.VISIBLE);

                    signStep.customFullView.setVisibility(View.VISIBLE);
                    signStep.customFullView.setTag(STEP_TAG);

                    if(signStep.customFullView.getParent() != null) {
                        ((ViewGroup)signStep.customFullView.getParent()).removeView(signStep.customFullView); // <- fix
                    }
                    layoutContainer.addView(signStep.customFullView);

                    if (animated)
                    {
                        signStep.customFullView.startAnimation(slide);
                    }
                }
                else if (signStep.customView != null)
                {
                    layoutContainer.setVisibility(View.GONE);
                    txtHeader.setText(signStep.title);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) txtHeader.getLayoutParams();
                    params.bottomMargin = 0;
                    txtHeader.setLayoutParams(params);

                    signStep.customView.setVisibility(View.VISIBLE);
                    signStep.customView.setTag(STEP_TAG);

                    if(signStep.customView.getParent() != null) {
                        ((ViewGroup)signStep.customView.getParent()).removeView(signStep.customView); // <- fix
                    }
                    layoutFields.addView(signStep.customView);

                    if (animated)
                    {
                        signStep.customView.startAnimation(slide);
                        txtHeader.startAnimation(slide);
                        btnContinue.startAnimation(slide);
                    }
                }
            }

            float progress =  (float)(positionStep+1) / (float)(steps.size()+1);
            float[] steps = new float[getTotalSteps()];
            for (int i = 0; i < getTotalSteps(); i++)
            {
                if (i < getStepBlock())
                {
                    steps[i] = 1.0f;
                }
                else if (i == getStepBlock())
                {
                    steps[i] = progress;
                }
                else
                {
                    steps[i] = 0f;
                }
            }
            stepper.setSteps(steps);
        }
    }
}
