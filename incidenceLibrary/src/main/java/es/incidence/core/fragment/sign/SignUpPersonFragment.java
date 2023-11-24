package es.incidence.core.fragment.sign;

import android.os.Bundle;

import java.util.ArrayList;

import es.incidence.core.Constants;
import com.e510.incidencelibrary.R;
import es.incidence.core.domain.IdentityType;
import es.incidence.core.domain.User;
import es.incidence.core.entity.sign.SignStep;
import es.incidence.core.entity.sign.SignStepType;
import es.incidence.core.entity.sign.SignStepValidation;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.utils.view.IDropField;
import es.incidence.core.utils.view.IField;
import es.incidence.core.utils.view.TermsView;

import static com.e510.commons.utils.LogUtil.makeLogTag;

public class SignUpPersonFragment extends SignUpFragment
{
    private static final String TAG = makeLogTag(SignUpPersonFragment.class);

    public User getUserCreating()
    {
        User user = new User();

        SignStep step = getStep(ID_NAME);
        if (step != null && step.customView != null)
        {
            user.name = ((IField) step.customView).getText();
        }
        step = getStep(ID_PHONE);
        if (step != null && step.customView != null)
        {
            user.phone = ((IField) step.customView).getText();
        }
        step = getStep(ID_DNI);
        if (step != null && step.customView != null)
        {
            IdentityType identityType = new IdentityType();
            identityType.name = ((IDropField) step.customView).getMenuTitle();
            if (identityType.name != null && identityType.name.equals("DNI")) {
                identityType.id = 1;
            } else if (identityType.name != null && identityType.name.equals("NIE")) {
                identityType.id = 2;
            } else if (identityType.name != null && identityType.name.equals("CIF")) {
                identityType.id = 3;
            } else {
                identityType.id = 1;
            }
            user.identityType = identityType;
            user.dni = ((IDropField) step.customView).getText();
        }
        step = getStep(ID_EMAIL);
        if (step != null && step.customView != null)
        {
            user.email = ((IField) step.customView).getText();
            if (user.email != null) {
                user.email = user.email.trim();
            }
        }

        return user;
    }

    public static SignUpPersonFragment newInstance()
    {
        SignUpPersonFragment fragment = new SignUpPersonFragment();

        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getStepBlock() {
        return 0;
    }

    @Override
    public void loadData()
    {
        steps = new ArrayList<>();

        SignStep s1 = new SignStep(ID_NAME);
        s1.type = SignStepType.FIELD;
        s1.navigationTitle = getString(R.string.create_account_step1);
        s1.title = getString(R.string.name_surname_title);
        s1.titleField = getString(R.string.name_surname);
        s1.hintField = getString(R.string.name_surname);
        s1.typeField = IField.TYPE_TEXT;
        steps.add(s1);

        SignStep s2 = new SignStep(ID_PHONE);
        s2.type = SignStepType.FIELD;
        s2.navigationTitle = getString(R.string.create_account_step1);
        s2.title = getString(R.string.phone_title);
        s2.titleField = getString(R.string.phone);
        s2.hintField = getString(R.string.hint_phone);
        s2.typeField = IField.TYPE_PHONE;
        s2.validation = new SignStepValidation() {
            @Override
            public void validate() {
                showHud();
                Api.validatePhone(new IRequestListener() {
                    @Override
                    public void onFinish(IResponse response) {
                        hideHud();
                        if (response.isSuccess())
                        {
                            String action = response.get("action");

                            if (action != null && action.equals(Constants.VALIDATE_USER_PHONE_EXISTS))
                            {
                                String message = response.get("message");
                                ((IField) s2.customView).showError(message);
                            }
                            else
                            {
                                printNextStep();
                            }
                        }
                        else
                        {
                            //onBadResponse(response);
                            if (response != null && response.message != null)
                            {
                                ((IField) s2.customView).showError(response.message);
                            }
                        }
                    }
                }, getUserCreating().phone);
            }
        };
        steps.add(s2);

        SignStep s3 = new SignStep(ID_TERMS);
        s3.navigationTitle = getString(R.string.privacy);
        s3.type = SignStepType.TERMS;
        s3.validation = new SignStepValidation() {
            @Override
            public void validate() {

                User user = getUserCreating();

                showHud();
                Api.signUp(new IRequestListener() {
                    @Override
                    public void onFinish(IResponse response) {
                        hideHud();
                        if (response.isSuccess())
                        {
                            printNextStep();
                        }
                        else
                        {
                            onBadResponse(response);
                        }
                    }
                }, user.name, user.phone);
            }
        };
        steps.add(s3);

        SignStep s4 = new SignStep(ID_SMS);
        s4.navigationTitle = getString(R.string.create_account_step1);
        s4.type = SignStepType.SMS;
        steps.add(s4);

        SignStep s5 = new SignStep(ID_DNI);
        s5.navigationTitle = getString(R.string.create_account_step1);
        s5.type = SignStepType.DROPFIELD;
        s5.title = getString(R.string.nif_title);
        s5.titleField = getString(R.string.nif_doc_identity);
        s5.hintField = getString(R.string.hint_dni);
        s5.typeField = R.menu.popup_menu_nif;
        s5.titleMenuField = getString(R.string.nif);
        s5.validation = new SignStepValidation() {
            @Override
            public void validate() {
                showHud();

                User user = getUserCreating();
                if (user.identityType.id == 1) {
                    Api.validateDNI(new IRequestListener() {
                        @Override
                        public void onFinish(IResponse response) {
                            hideHud();
                            if (response.isSuccess())
                            {
                                String action = response.get("action");

                                if (action != null && action.equals(Constants.VALIDATE_USER_DNI_EXISTS))
                                {
                                    String message = response.get("message");
                                    ((IDropField) s5.customView).showError(message);
                                }
                                else
                                {
                                    printNextStep();
                                }
                            }
                            else
                            {
                                //onBadResponse(response);
                                if (response != null && response.message != null)
                                {
                                    ((IDropField) s5.customView).showError(response.message);
                                }
                            }
                        }
                    }, user.dni);
                }
                else {
                    Api.validateNIE(new IRequestListener() {
                        @Override
                        public void onFinish(IResponse response) {
                            hideHud();
                            if (response.isSuccess())
                            {
                                String action = response.get("action");

                                if (action != null && action.equals(Constants.VALIDATE_USER_NIE_EXISTS))
                                {
                                    String message = response.get("message");
                                    ((IDropField) s5.customView).showError(message);
                                }
                                else
                                {
                                    printNextStep();
                                }
                            }
                            else
                            {
                                //onBadResponse(response);
                                if (response != null && response.message != null)
                                {
                                    ((IDropField) s5.customView).showError(response.message);
                                }
                            }
                        }
                    }, user.dni);
                }
            }
        };
        steps.add(s5);

        SignStep s6 = new SignStep(ID_EMAIL);
        s6.navigationTitle = getString(R.string.create_account_step1);
        s6.type = SignStepType.FIELD;
        s6.title = getString(R.string.email_title_signup);
        s6.titleField = getString(R.string.email);
        s6.hintField = getString(R.string.hint_email);
        s6.typeField = IField.TYPE_EMAIL;
        s6.validation = new SignStepValidation() {
            @Override
            public void validate() {

                User user = getUserCreating();

                showHud();
                Api.validateEmail(new IRequestListener() {
                    @Override
                    public void onFinish(IResponse response) {

                        if (response.isSuccess())
                        {
                            String action = response.get("action");

                            if (action != null && action.equals(Constants.VALIDATE_USER_EMAIL_EXISTS))
                            {
                                String message = response.get("message");

                                hideHud();
                                ((IField) s6.customView).showError(message);
                            }
                            else
                            {
                                String checkAdvertising = "0";
                                SignStep step = getStep(ID_TERMS);
                                if (step.customView != null &&step.customView instanceof TermsView) {
                                    TermsView termsView = (TermsView) step.customView;
                                    if (termsView.isAdvertisingChecked())
                                    {
                                        checkAdvertising = "1";
                                    }
                                }

                                Api.updateUser(new IRequestListener() {
                                    @Override
                                    public void onFinish(IResponse response) {
                                        hideHud();
                                        if (response.isSuccess())
                                        {
                                            printNextStep();
                                        }
                                        else
                                        {
                                            onBadResponse(response);
                                        }
                                    }
                                }, user.name, user.phone, user.identityType.id+"", user.dni, user.email, null, checkAdvertising);
                            }
                        }
                        else
                        {
                            hideHud();
                            //onBadResponse(response);
                            if (response != null && response.message != null)
                            {
                                ((IField) s6.customView).showError(response.message);
                            }
                        }
                    }
                }, getUserCreating().email);
            }
        };
        steps.add(s6);

        printStep(false);
    }
}
