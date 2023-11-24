package es.incidence.core.fragment.sign;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.e510.commons.utils.DateUtils;
import com.e510.incidencelibrary.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;

import es.incidence.core.domain.ColorType;
import es.incidence.core.domain.IdentityType;
import es.incidence.core.domain.Insurance;
import es.incidence.core.domain.Policy;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.domain.VehicleType;
import es.incidence.core.entity.event.Event;
import es.incidence.core.entity.event.EventCode;
import es.incidence.core.entity.sign.SignStep;
import es.incidence.core.utils.view.IButton;
import es.incidence.core.utils.view.IDropField;
import es.incidence.core.utils.view.IField;
import es.incidence.core.utils.view.VehicleColorView;

public class SignUpVehicleFragment extends SignUpFragment
{
    private static final String TAG = makeLogTag(SignUpVehicleFragment.class);

    private IField txtMatricula;
    private IField txtMatriculaYear;
    private IField txtMarca;
    private IField txtModelo;

    private VehicleColorView vehicleColorView;

    private IField txtSearchInsurance;
    private ListView listViewInsurances;
    private ScrollView scrollInsuranceNotFound;
    private TextView txtInsuranceNotFound;
    private IField txtNewInsurance;
    private IButton btnNewInsurance;
    private IField txtInsuranceName;
    private IField txtInsuranceNumber;
    private IDropField txtInsuranceDni;
    private IField txtInsuranceCaducity;


    private VehicleType vehicleType = null;
    private ColorType vehicleColor = null;
    private Insurance insurance;
    private Policy temporalPolicy;
    private String vehicleId;
    private boolean vehicleAddedCalled;

    private ArrayList<Insurance> allInsurances;
    private ArrayList<Object> insurances = new ArrayList<>();
    //private InsuranceListAdapter insuranceListAdapter;

    public Policy getInsurancePolicyCreating()
    {
        Policy policy = new Policy();

        policy.policyNumber = txtInsuranceNumber.getText();

        IdentityType identityType = new IdentityType();
        identityType.name = txtInsuranceDni.getMenuTitle();
        if (identityType.name != null && identityType.name.equals("DNI")) {
            identityType.id = 1;
        } else if (identityType.name != null && identityType.name.equals("NIE")) {
            identityType.id = 2;
        } else if (identityType.name != null && identityType.name.equals("CIF")) {
            identityType.id = 3;
        } else {
            identityType.id = 1;
        }
        policy.identityType = identityType;
        policy.dni = txtInsuranceDni.getText();

        String caducity = txtInsuranceCaducity.getText();
        if (caducity != null)
        {
            Date date = DateUtils.parseDate(caducity, DateUtils.DATE_ES);
            caducity = DateUtils.dateToString(date, DateUtils.DATE);
        }
        policy.policyEnd = caducity;

        return policy;
    }
    public Vehicle getVehicleCreating()
    {
        Vehicle vehicle = new Vehicle();
        vehicle.id = vehicleId;
        vehicle.vehicleType = vehicleType;
        SignStep step = getStep(ID_VEHICLE_MATRICULA);
        if (step != null && step.customView != null)
        {
            vehicle.licensePlate = ((IField) step.customView).getText();
        }
        vehicle.registrationYear = txtMatriculaYear.getText();
        vehicle.brand = txtMarca.getText();
        vehicle.model = txtModelo.getText();
        vehicle.color = vehicleColor;

        vehicle.insurance = insurance;
        vehicle.policy = temporalPolicy;

        return vehicle;
    }

    @Override
    public boolean onBackPressed()
    {
        boolean res = super.onBackPressed();

        if (res == false && isRegistration())
        {
            //Core.startApp(getBaseActivity());
            res = true;
        }
        else if (res == false && vehicleId != null && !vehicleAddedCalled)
        {
            vehicleAddedCalled = true;
            EventBus.getDefault().post(new Event(EventCode.VEHICLE_ADDED));
        }

        return res;
    }

    public static SignUpVehicleFragment newInstance()
    {
        SignUpVehicleFragment fragment = new SignUpVehicleFragment();

        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getStepBlock() {
        return 1;
    }

    @Override
    public void onClickRow(Object object) {
        if (object instanceof Insurance)
        {
            insurance = (Insurance) object;
            printNextStep();
        }
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);
        navigation.setTitle(getString(R.string.create_account_step2));
    }

    @Override
    public void loadData()
    {
        mListener.showHud();
        /*
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                steps = new ArrayList<>();

                SignStep s1 = new SignStep(ID_VEHICLE_TYPE);
                s1.type = SignStepType.CUSTOM;
                s1.navigationTitle = getString(R.string.create_account_step2);
                s1.title = getString(R.string.select_type_vechicle);
                s1.customFullView = getStep1View();
                steps.add(s1);

                SignStep s2 = new SignStep(ID_VEHICLE_MATRICULA);
                s2.type = SignStepType.FIELD;
                s2.navigationTitle = getString(R.string.create_account_step2);
                s2.title = getString(R.string.add_matricula_title);
                s2.titleField = getString(R.string.matricula);
                s2.hintField = getString(R.string.matricula);
                s2.typeField = IField.TYPE_TEXT;
                s2.validation = new SignStepValidation() {
                    @Override
                    public void validate() {

                        Vehicle vehicle = getVehicleCreating();

                        showHud();
                        Api.validateLicensePlate(new IRequestListener() {
                            @Override
                            public void onFinish(IResponse response) {
                                hideHud();
                                if (response.isSuccess())
                                {
                                    printNextStep();
                                }
                                else if (response.action != null && response.action.equals("license_plate_exists"))
                                {
                                    String vehicleId = response.get("vehicleId");
                                    showTypeDriverPopUp(vehicleId);
                                }
                                else
                                {
                                    onBadResponse(response);
                                }
                            }
                        }, vehicle.licensePlate);
                    }
                };
                steps.add(s2);

                SignStep s3 = new SignStep(ID_VEHICLE_COMPRUEBA);
                s3.type = SignStepType.CUSTOM;
                s3.navigationTitle = getString(R.string.create_account_step2);
                s3.title = getString(R.string.check_vehicle_data_correct);
                s3.customView = getStep3View();
                s3.validation = new SignStepValidation() {
                    @Override
                    public void validate() {

                        Vehicle vehicle = getVehicleCreating();
                        String registrationYear = vehicle.registrationYear;
                        if (registrationYear != null && registrationYear.length() > 0)
                        {
                            showHud();
                            Api.validateYear(new IRequestListener() {
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
                            }, registrationYear);
                        }
                        else
                        {
                            printNextStep();
                        }
                    }
                };
                steps.add(s3);

                SignStep s4 = new SignStep(ID_VEHICLE_COLOR);
                s4.type = SignStepType.CUSTOM;
                s4.navigationTitle = getString(R.string.create_account_step2);
                s4.title = getString(R.string.select_vehicle_color);
                s4.validation = new SignStepValidation() {
                    @Override
                    public void validate() {
                        addVehicle();
                    }
                };
                s4.customFullView = getStep4View(s4.validation);
                steps.add(s4);

                SignStep s5 = new SignStep(ID_VEHICLE_LIST_INSURANCE);
                s5.type = SignStepType.CUSTOM;
                s5.navigationTitle = getString(R.string.create_account_step2);
                s5.title = getString(R.string.list_insurance);
                s5.customFullView = getStep5View();
                steps.add(s5);

                SignStep s6 = new SignStep(ID_VEHICLE_COMPLETE_INSURANCE);
                s6.type = SignStepType.CUSTOM;
                s6.navigationTitle = getString(R.string.create_account_step2);
                s6.title = getString(R.string.complete_insurance_data);
                s6.validation = new SignStepValidation() {
                    @Override
                    public void validate() {

                        Vehicle vehicle = getVehicleCreating();
                        Policy policy = getInsurancePolicyCreating();

                        showHud();

                        if (policy.identityType.id == 1) {
                            Api.validateDNI(new IRequestListener() {
                                @Override
                                public void onFinish(IResponse response) {
                                    if (response.isSuccess())
                                    {
                                        Api.addVehiclePolicy(new IRequestListener() {
                                            @Override
                                            public void onFinish(IResponse response) {
                                                hideHud();
                                                if (response.isSuccess())
                                                {
                                                    vehicleAddedCalled = true;
                                                    EventBus.getDefault().post(new Event(EventCode.VEHICLE_ADDED));
                                                    printNextStep();
                                                }
                                                else
                                                {
                                                    onBadResponse(response);
                                                }
                                            }
                                        }, vehicle, policy);
                                    }
                                    else
                                    {
                                        hideHud();
                                        onBadResponse(response);
                                    }
                                }
                            }, policy.dni);
                        }
                        else if (policy.identityType.id == 3) {
                            Api.validateCIF(new IRequestListener() {
                                @Override
                                public void onFinish(IResponse response) {
                                    if (response.isSuccess())
                                    {
                                        Api.addVehiclePolicy(new IRequestListener() {
                                            @Override
                                            public void onFinish(IResponse response) {
                                                hideHud();
                                                if (response.isSuccess())
                                                {
                                                    vehicleAddedCalled = true;
                                                    EventBus.getDefault().post(new Event(EventCode.VEHICLE_ADDED));
                                                    printNextStep();
                                                }
                                                else
                                                {
                                                    onBadResponse(response);
                                                }
                                            }
                                        }, vehicle, policy);
                                    }
                                    else
                                    {
                                        hideHud();
                                        onBadResponse(response);
                                    }
                                }
                            }, policy.dni);
                        }
                        else {
                            Api.validateNIE(new IRequestListener() {
                                @Override
                                public void onFinish(IResponse response) {
                                    if (response.isSuccess())
                                    {
                                        Api.addVehiclePolicy(new IRequestListener() {
                                            @Override
                                            public void onFinish(IResponse response) {
                                                hideHud();
                                                if (response.isSuccess())
                                                {
                                                    vehicleAddedCalled = true;
                                                    EventBus.getDefault().post(new Event(EventCode.VEHICLE_ADDED));
                                                    printNextStep();
                                                }
                                                else
                                                {
                                                    onBadResponse(response);
                                                }
                                            }
                                        }, vehicle, policy);
                                    }
                                    else
                                    {
                                        hideHud();
                                        onBadResponse(response);
                                    }
                                }
                            }, policy.dni);
                        }
                    }
                };
                s6.customFullView = getStep6View(s6.validation);
                steps.add(s6);

                mListener.hideHud();
                printStep(false, false);
            }
        }, 300);
        */
    }

    @Override
    public void printStep(boolean isBack)
    {
        /*
        if (positionStep == 2)
        {
            txtMatricula.setText("1234AAA");
            txtMatriculaYear.setText("2010");
            txtMarca.setText("Volkswagen");
            txtModelo.setText("Tiguan");
        }
        else if (positionStep == 3)
        {
            vehicleColorView.setVehicleType("");
        }
        else if (positionStep == 5)
        {
            txtInsuranceName.setText(insurance.name);
        }
        */

        super.printStep(isBack);

        if (currentStep.id == ID_VEHICLE_COMPRUEBA)
        {
            Vehicle vehicle = getVehicleCreating();
            txtMatricula.setText(vehicle.licensePlate);
        }
        else if (currentStep.id == ID_VEHICLE_COLOR)
        {
            vehicleColorView.setVehicleType(vehicleType);
        }
        else if (currentStep.id == ID_VEHICLE_LIST_INSURANCE)
        {
            //loadInsurances();
        }
        else if (currentStep.id == ID_VEHICLE_COMPLETE_INSURANCE)
        {
            txtInsuranceName.setText(insurance.name);
        }
    }
/*
    private View getStep1View()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_vehicle_type, null);
        LinearLayout layoutSelect = view.findViewById(R.id.layoutSelect);
        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());


        ArrayList<VehicleType> list = Core.getVehiclesTypes();
        if (list != null)
        {
            for (int i = 0; i < list.size() - 1; i+=2)
            {
                VehicleType first = list.get(i);
                VehicleType tSecond = null;
                if (list.size() > i+1)
                {
                    tSecond = list.get(i + 1);
                }
                VehicleType second = tSecond;

                View row = inflater.inflate(R.layout.row_vehicle_type, null);
                RelativeLayout layoutLeft = row.findViewById(R.id.select_left);
                ImageView imgLeft = row.findViewById(R.id.select_left_img);
                RelativeLayout layoutRight = row.findViewById(R.id.select_right);
                ImageView imgRight = row.findViewById(R.id.select_right_img);


                layoutLeft.setVisibility(View.VISIBLE);
                if (first.colors != null && first.colors.size() > 0)
                    ImageManager.loadImage(getContext(), first.colors.get(0).image, imgLeft);
                layoutLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vehicleType = first;
                        printNextStep();
                    }
                });

                if (second != null)
                {
                    layoutRight.setVisibility(View.VISIBLE);
                    if (second.colors != null && second.colors.size() > 0)
                        ImageManager.loadImage(getContext(), second.colors.get(0).image, imgRight);
                    layoutRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            vehicleType = second;
                            printNextStep();
                        }
                    });
                }

                layoutSelect.addView(row);
            }
        }

        return view;
    }

    private View getStep3View()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_vehicle_check_data, null);
        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String res1 = txtMatricula.getText();
                //String res2 = txtMatriculaYear.getText();
                String res3 = txtMarca.getText();
                String res4 = txtModelo.getText();
                if (res1 != null && res1.length() > 0
                        //&& res2 != null && res2.length() > 0
                        && res3 != null && res3.length() > 0
                        && res4 != null && res4.length() > 0)
                {
                    validateContinue(true);
                }
                else
                {
                    validateContinue(false);
                }
            }
        };

        txtMatricula = view.findViewById(R.id.txtMatricula);
        txtMatricula.setTitle(getString(R.string.matricula));
        txtMatricula.setHint(getString(R.string.matricula));
        txtMatricula.setType(IField.TYPE_TEXT);
        txtMatricula.setTextWatcher(textWatcher);
        txtMatricula.disable();
        txtMatricula.showOK();

        txtMatriculaYear = view.findViewById(R.id.txtMatriculaYear);
        txtMatriculaYear.setTitle(getString(R.string.matricula_year_optional));
        txtMatriculaYear.setHint(getString(R.string.matricula_year_optional));
        txtMatriculaYear.setType(IField.TYPE_NUMBER);
        txtMatriculaYear.setMaxLength(4);
        txtMatriculaYear.setTextWatcher(textWatcher);
        //txtMatriculaYear.disable();
        //txtMatriculaYear.showOK();

        txtMarca = view.findViewById(R.id.txtMarca);
        txtMarca.setTitle(getString(R.string.brand));
        txtMarca.setHint(getString(R.string.brand));
        txtMarca.setType(IField.TYPE_TEXT);
        txtMarca.setTextWatcher(textWatcher);
        //txtMarca.disable();
        //txtMarca.showOK();

        txtModelo = view.findViewById(R.id.txtModelo);
        txtModelo.setTitle(getString(R.string.model));
        txtModelo.setHint(getString(R.string.model));
        txtModelo.setType(IField.TYPE_TEXT);
        txtModelo.setTextWatcher(textWatcher);
        txtModelo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                    handled = true;
                }
                return handled;
            }
        });
        //txtModelo.disable();
        //txtModelo.showOK();

        return view;
    }

    private View getStep4View(SignStepValidation validation)
    {
        vehicleColorView = new VehicleColorView(getContext());
        //vehicleColorView.setVehicleType("car");
        vehicleColorView.onDismissClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //printNextStep();
                //añadimos vehiculo sin color
                addVehicle();
            }
        });
        vehicleColorView.onContinueClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorType color = vehicleColorView.getVehicleColor();
                vehicleColor = color;
                validation.validate();
            }
        });

        return vehicleColorView;
    }

    private View getStep5View()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_insurance_list, null);
        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());

        txtSearchInsurance = view.findViewById(R.id.field);
        txtSearchInsurance.setWithGradient(false);
        txtSearchInsurance.setWithValidation(false);
        txtSearchInsurance.setImageLeft(Utils.getDrawable(getContext(), R.drawable.icon_search));
        txtSearchInsurance.setHint(getString(R.string.search_insurance));

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = txtSearchInsurance.getText();
                reloadInsurances(text);
            }
        };
        txtSearchInsurance.setTextWatcher(textWatcher);


        listViewInsurances = view.findViewById(R.id.listView);
        insuranceListAdapter = new InsuranceListAdapter(this, insurances);
        listViewInsurances.setAdapter(insuranceListAdapter);

        scrollInsuranceNotFound = view.findViewById(R.id.scrollInsuranceNotFound);
        view.findViewById(R.id.layoutInsuListRoot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();}
        });
        scrollInsuranceNotFound.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {
            }
            public void onSwipeLeft() {
            }
            @Override
            public void onClick() {
                super.onClick();
                hideKeyboard();
            }
        });
        txtInsuranceNotFound = view.findViewById(R.id.txtNotFound);
        txtNewInsurance = view.findViewById(R.id.field_new);
        txtNewInsurance.setHint(getString(R.string.new_insurance));
        txtNewInsurance.setTitle(getString(R.string.new_insurance));
        btnNewInsurance = view.findViewById(R.id.btnAddInsurance);
        btnNewInsurance.setPrimaryColors();
        btnNewInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewInsurance();
            }
        });
        FontUtils.setTypeValueText(btnNewInsurance, Constants.FONT_SEMIBOLD, getContext());

        return view;
    }

    private View getStep6View(SignStepValidation validation)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_vehicle_insurance_data, null);
        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());

        IButton btnContinue = view.findViewById(R.id.btnContinueColor);
        btnContinue.setText(getString(R.string.continuar));

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String res1 = txtInsuranceName.getText();
                String res2 = txtInsuranceNumber.getText();
                String res3 = txtInsuranceDni.getText();
                String res4 = txtInsuranceCaducity.getText();
                if (res1 != null && res1.length() > 0
                        && res2 != null && res2.length() > 0
                        && res3 != null && res3.length() > 0
                        && res4 != null && res4.length() > 0)
                {
                    btnContinue.setPrimaryColors();
                    btnContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (insurance != null)
                            {
                                validation.validate();
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
        };

        txtInsuranceName = view.findViewById(R.id.txtInsuranceName);
        txtInsuranceName.setTitle(getString(R.string.company_insurance));
        txtInsuranceName.setHint(getString(R.string.company_insurance));
        txtInsuranceName.setType(IField.TYPE_TEXT);
        txtInsuranceName.setTextWatcher(textWatcher);
        txtInsuranceName.disable();
        txtInsuranceName.showOK();

        txtInsuranceNumber = view.findViewById(R.id.txtInsuranceNumber);
        txtInsuranceNumber.setTitle(getString(R.string.company_insurance_number));
        txtInsuranceNumber.setHint(getString(R.string.company_insurance_number));
        txtInsuranceNumber.setType(IField.TYPE_TEXT);
        txtInsuranceNumber.setTextWatcher(textWatcher);

        txtInsuranceDni = view.findViewById(R.id.txtInsuranceDni);
        txtInsuranceDni.setTitle(getString(R.string.company_insurance_titular));
        txtInsuranceDni.setHint(getString(R.string.company_insurance_titular));
        txtInsuranceDni.setMenuTitle(getString(R.string.dni));
        txtInsuranceDni.setMenu(R.menu.popup_menu_nif);
        txtInsuranceDni.setTextWatcher(textWatcher);
        txtInsuranceDni.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        txtInsuranceDni.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    hideKeyboard();
                    txtInsuranceCaducity.requestFocus();
                    txtInsuranceCaducity.showDatePickerDialog();
                    handled = true;
                }
                return handled;
            }
        });

        txtInsuranceCaducity = view.findViewById(R.id.txtInsuranceCaducity);
        txtInsuranceCaducity.setTitle(getString(R.string.caducity));
        txtInsuranceCaducity.setHint(getString(R.string.caducity));
        txtInsuranceCaducity.setType(IField.TYPE_DATE);
        txtInsuranceCaducity.setTextWatcher(textWatcher);

        TextView txtAddLater = view.findViewById(R.id.txtAddLater);
        txtAddLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Vehicle vehicle = getVehicleCreating();

                if (vehicle.insurance != null)
                {
                    showHud();
                    Api.addVehicleInsurance(new IRequestListener() {
                        @Override
                        public void onFinish(IResponse response) {
                            hideHud();
                            if (response.isSuccess())
                            {
                                vehicleAddedCalled = true;
                                EventBus.getDefault().post(new Event(EventCode.VEHICLE_ADDED));
                                printNextStep();
                            }
                            else
                            {
                                onBadResponse(response);
                            }
                        }
                    }, vehicle);
                }
                else
                {
                    vehicleAddedCalled = true;
                    EventBus.getDefault().post(new Event(EventCode.VEHICLE_ADDED));
                    printNextStep();
                }
            }
        });
        FontUtils.setTypeValueText(txtAddLater, Constants.FONT_SEMIBOLD, getContext());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
        view.findViewById(R.id.layout_items).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
        btnContinue.setDisabledColors();
        btnContinue.setOnClickListener(null);
        FontUtils.setTypeValueText(btnContinue, Constants.FONT_SEMIBOLD, getContext());

        return view;
    }

    private void reloadInsurances(String searchString)
    {
        ArrayList<Object> tempFeatured = new ArrayList<>();
        if (allInsurances != null)
        {
            for (int i = 0; i < allInsurances.size(); i++)
            {
                Insurance insurance = allInsurances.get(i);

                if (searchString != null && searchString.length() > 0)
                {
                    if (!insurance.name.toLowerCase().contains(searchString.toLowerCase()))
                    {
                        continue;
                    }
                }

                if (insurance.relation == 1) {
                    tempFeatured.add(insurance);
                }
            }
        }

        ArrayList<Object> temp = new ArrayList<>();
        if (allInsurances != null)
        {
            if (tempFeatured.size() > 0)
            {
                temp.add(getString(R.string.featured_insurance));
                temp.addAll(tempFeatured);
            }

            ArrayList<Object> temp2 = new ArrayList<>();

            for (int i = 0; i < allInsurances.size(); i++)
            {
                Insurance insurance = allInsurances.get(i);

                if (searchString != null && searchString.length() > 0)
                {
                    if (!insurance.name.toLowerCase().contains(searchString.toLowerCase()))
                    {
                        continue;
                    }
                }

                temp2.add(insurance);
            }

            if (temp2.size() > 0)
            {
                temp.add(getString(R.string.list_insurance));
                temp.addAll(temp2);
            }
        }

        insurances.clear();
        insurances.addAll(temp);
        insuranceListAdapter.notifyDataSetChanged();

        if (insurances.size() == 0 && searchString != null && searchString.length() > 0)
        {
            scrollInsuranceNotFound.setVisibility(View.VISIBLE);
            txtInsuranceNotFound.setVisibility(View.VISIBLE);
            txtNewInsurance.setVisibility(View.VISIBLE);
            txtNewInsurance.setText(searchString);
            txtNewInsurance.showOK();
            btnNewInsurance.setVisibility(View.VISIBLE);
            listViewInsurances.setVisibility(View.GONE);
        }
        else
        {
            scrollInsuranceNotFound.setVisibility(View.GONE);
            txtInsuranceNotFound.setVisibility(View.GONE);
            txtNewInsurance.setVisibility(View.GONE);
            btnNewInsurance.setVisibility(View.GONE);
            listViewInsurances.setVisibility(View.VISIBLE);
        }
    }
    private void loadInsurances()
    {
        showHud();
        Api.getInsurances(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {
                hideHud();
                if (response.isSuccess())
                {
                    allInsurances = response.getList("insurances", Insurance.class);

                    reloadInsurances(txtSearchInsurance.getText());
                }
                else
                {
                    onBadResponse(response);
                }
            }
        });
    }

    private void addNewInsurance()
    {
        String name = txtNewInsurance.getText();
        if (name != null && name.length() > 0)
        {
            Vehicle vehicle = getVehicleCreating();

            showHud();
            Api.addInsurance(new IRequestListener() {
                @Override
                public void onFinish(IResponse response) {
                    hideHud();
                    if (response.isSuccess())
                    {
                        insurance = (Insurance) response.get("insurance", Insurance.class);

                        printNextStep();
                    }
                    else
                    {
                        onBadResponse(response);
                    }
                }
            }, vehicle.policy.id, name);
        }
    }

    private void addVehicle()
    {
        Vehicle vehicle = getVehicleCreating();
        showHud();
        Api.addVehicle(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {
                hideHud();
                if (response.isSuccess())
                {
                    Vehicle v = (Vehicle) response.get("vehicle", Vehicle.class);
                    temporalPolicy = v.policy;
                    vehicleId = v.id;
                    newVehicleCreated = v;
                    printNextStep();

                    //EventBus.getDefault().post(new Event(EventCode.VEHICLE_ADDED, v));
                }
                else
                {
                    onBadResponse(response);
                }
            }
        }, vehicle);
    }

    private void showTypeDriverPopUp(String vehicleId)
    {
        String title = getString(R.string.ask_you_are_this_vehicle_driver);
        String message = getString(R.string.ask_you_are_this_vehicle_driver_desc);
        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.ask_you_are_this_vehicle_driver_yes));
        options.add(getString(R.string.ask_you_are_this_vehicle_driver_no));
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
                        //yes
                        if (isRegistration())
                        {
                            mListener.addFragment(R.id.layout_activity_main, SelectVehicleDriverTypeFragment.newInstance(vehicleId), FragmentAnimation.PUSH);
                        }
                        else
                        {
                            mListener.addFragmentAnimated(SelectVehicleDriverTypeFragment.newInstance(vehicleId));
                        }

                    }
                    else if (index == 1)
                    {
                        //no
                        SignStep step = getStep(ID_VEHICLE_MATRICULA);
                        if (step != null && step.customView != null)
                        {
                            ((IField) step.customView).setText("");
                        }
                    }
                }
            }
        };

        RelativeLayout layoutToShow = getBaseActivity().findViewById(R.id.mainBaseLayout);
        INotification.shared(getContext()).showOptionsNotification(layoutToShow, title, message, options, optionsColors, listener);
    }
 */
}
