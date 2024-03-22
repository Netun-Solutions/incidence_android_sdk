package com.e510.incidencelibraryandroid;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;

import es.incidence.core.domain.ColorType;
import es.incidence.core.domain.IdentityType;
import es.incidence.core.domain.Incidence;
import es.incidence.core.domain.IncidenceType;
import es.incidence.core.domain.Policy;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.domain.VehicleType;
import es.incidence.library.IncidenceLibraryManager;

public class MainActivity extends AppCompatActivity {

    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    private static final String TAG = makeLogTag(MainActivity.class);

    private Button btnGetDevice;
    private Button btnDeviceCreate;
    private Button btnDeviceDelete;
    private Button btnDeviceReview;
    private Button btnIncidenceCreate;
    private Button btnIncidenceClose;
    private Button btnEcommerce;
    private Button btnReportInc;
    private Button btnReportIncSimple;
    private Button btnReportIncSimpleOp1;
    private Button btnGetGeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String dni = "15111115T";
        String phone = "650010005";
        String externalUserId = "15005";
        String externalVehicleId = "15005";
        String licensePlate = "1511XX5";
        String externalIncidenceId = "15005";

        IdentityType dniIdentityType = new IdentityType();
        dniIdentityType.name = "dni"; // (tipo de documento de identidad: dni, nie, cif)

        VehicleType vehicleType = new VehicleType();
        vehicleType.name = "Coche";

        ColorType color = new ColorType();
        color.name = "Rojo";

        Policy policy = new Policy();
        policy.policyNumber = "111111115"; // (número de la póliza)
        policy.policyEnd = "2024-10-09"; // (fecha caducidad de la póliza)
        policy.identityType = dniIdentityType; // (tipo de documento identidad del asegurador)
        policy.dni = dni; // (documento de identidad del asegurador)

        User user = new User();
        user.externalUserId = externalUserId; // (identificador externo del usuario)
        user.name = "Nombre TEST"; // (nombre del usuario)
        user.phone = phone; // (teléfono)
        user.email = "sdkm5@tridenia.com"; // (e-mail)
        user.identityType = dniIdentityType;
        user.dni = dni; // (número del documento de identidad)
        user.birthday = "1979-09-29"; // (fecha de Nacimiento)
        user.checkTerms = "1"; // (aceptación de la privacidad)

        Vehicle vehicle = new Vehicle();
        vehicle.externalVehicleId = externalVehicleId;
        vehicle.licensePlate = licensePlate; // (matrícula del vehículo)
        vehicle.registrationYear = "2022"; // (fecha de matriculación)
        vehicle.vehicleType = vehicleType; // (tipo del vehículo)
        vehicle.brand = "Seat"; // (marca del vehículo)
        vehicle.model = "Laguna"; // (modelo del vehículo)
        vehicle.color = color; // (color del vehículo)
        vehicle.policy = policy;

        IncidenceType incidenceType = new IncidenceType();
        incidenceType.id = 5; // Pinchazo
        incidenceType.externalId = "B1"; // Pinchazo

        Incidence incidence = new Incidence();
        incidence.incidenceType = incidenceType;
        incidence.street = "Carrer Major, 2";
        incidence.city = "Barcelona";
        incidence.country = "España";
        incidence.latitude = 41.4435945;
        incidence.longitude = 2.2319534;
        incidence.externalIncidenceId = externalIncidenceId;

        btnGetDevice = findViewById(R.id.btnGetDevice);
        btnGetDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncidenceLibraryManager.instance.getBeaconFunc(user, vehicle, response -> {
                    if (response.isSuccess()) {
                        //MAKE OK ACTIONS
                        Log.d(TAG, "SUCCESS");
                        Toast.makeText(MainActivity.this, "SI hay baliza asociada", Toast.LENGTH_SHORT).show();
                    } else {
                        //MAKE KO ACTIONS
                        Log.d(TAG, "ERROR: " + response.message);
                        Toast.makeText(MainActivity.this, "NO hay baliza asociada", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnGetGeo = findViewById(R.id.btnGetGeo);
        btnGetGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncidenceLibraryManager.instance.getGeoFunc(user, vehicle, response -> {
                    if (response.isSuccess() && response.data != null) {
                        //MAKE OK ACTIONS
                        Log.d(TAG, "SUCCESS");
                        Toast.makeText(MainActivity.this, "SI hay coordenadas " + response.data, Toast.LENGTH_SHORT).show();
                    } else {
                        //MAKE KO ACTIONS
                        Log.d(TAG, "ERROR: " + response.message);
                        Toast.makeText(MainActivity.this, "NO hay coordenadas", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnDeviceCreate = findViewById(R.id.btnDeviceCreate);
        btnDeviceCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = IncidenceLibraryManager.instance.getDeviceCreateViewController(user, vehicle);
                startActivity(activity);
            }
        });

        btnDeviceDelete = findViewById(R.id.btnDeviceDelete);
        btnDeviceDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncidenceLibraryManager.instance.deleteBeaconFunc(user, vehicle, response -> {
                    if (response.isSuccess()) {
                        //MAKE OK ACTIONS
                        Log.d(TAG, "SUCCESS");
                        Toast.makeText(MainActivity.this, "Baliza desvinculada con éxito", Toast.LENGTH_SHORT).show();
                    } else {
                        //MAKE KO ACTIONS
                        Log.d(TAG, "ERROR: " + response.message);
                        Toast.makeText(MainActivity.this, "Baliza desvinculada con error: " + response.message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnDeviceReview = findViewById(R.id.btnDeviceReview);
        btnDeviceReview.setOnClickListener(v -> {
            Intent activity = IncidenceLibraryManager.instance.getDeviceReviewViewController(user, vehicle);
            startActivity(activity);
        });

        btnIncidenceCreate = findViewById(R.id.btnIncidenceCreate);
        btnIncidenceCreate.setOnClickListener(v ->
            IncidenceLibraryManager.instance.createIncidenceFunc(user, vehicle, incidence, response -> {
                if (response.isSuccess()) {
                    //MAKE OK ACTIONS
                    Log.d(TAG, "SUCCESS");
                    Toast.makeText(MainActivity.this, "Incidencia creada con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    //MAKE KO ACTIONS
                    Log.d(TAG, "ERROR: " + response.message);
                    Toast.makeText(MainActivity.this, "Incidencia creada con error: " + response.message, Toast.LENGTH_SHORT).show();
                }
            }
        ));

        btnIncidenceClose = findViewById(R.id.btnIncidenceClose);
        btnIncidenceClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncidenceLibraryManager.instance.closeIncidenceFunc(user, vehicle, incidence, response -> {
                    if (response.isSuccess()) {
                        //MAKE OK ACTIONS
                        Log.d(TAG, "SUCCESS");
                        Toast.makeText(MainActivity.this, "Incidencia cerrada con éxito", Toast.LENGTH_SHORT).show();
                    } else {
                        //MAKE KO ACTIONS
                        Log.d(TAG, "ERROR: " + response.message);
                        Toast.makeText(MainActivity.this, "Incidencia cerrada con error: " + response.message, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        btnEcommerce = findViewById(R.id.btnEcommerce);
        btnEcommerce.setOnClickListener(v -> {
            Intent activity = IncidenceLibraryManager.instance.getEcommerceViewController(user, vehicle);
            startActivity(activity);
        });

        btnReportInc = findViewById(R.id.btnReportInc);
        btnReportInc.setOnClickListener(v -> {
            Intent activity = IncidenceLibraryManager.instance.getReportIncViewControllerFlowComplete(user, vehicle);
            //startActivity(activity);
            activityLauncher.launch(activity, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    Incidence incidence1=data.getParcelableExtra("incidence");
                    Log.e(TAG, "Incidencia creada con éxito: " + incidence1.externalIncidenceId);
                    // doSomeOperations();

                    Toast.makeText(MainActivity.this, "Incidencia creada con éxito: " + incidence1.externalIncidenceId, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No se ha podido crear la incidencia", Toast.LENGTH_SHORT).show();
                }
            });
        });
        //btnReportInc.setVisibility(View.GONE);

        btnReportIncSimple = findViewById(R.id.btnReportIncSimple);
        btnReportIncSimple.setOnClickListener(v -> {
            Intent activity = IncidenceLibraryManager.instance.getReportIncViewControllerFlowSimple(user, vehicle);
            //startActivity(activity);
            activityLauncher.launch(activity, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    Incidence incidence1=data.getParcelableExtra("incidence");
                    Log.e(TAG, "Incidencia creada con éxito: " + incidence1.externalIncidenceId);
                    // doSomeOperations();
                    Toast.makeText(MainActivity.this, "Incidencia creada con éxito: " + incidence1.externalIncidenceId, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No se ha podido crear la incidencia", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnReportIncSimpleOp1 = findViewById(R.id.btnReportIncSimpleOp1);
        btnReportIncSimpleOp1.setOnClickListener(v -> {
            Intent activity = IncidenceLibraryManager.instance.getReportIncViewControllerFlowSimpleOp1(user, vehicle);
            //startActivity(activity);
            activityLauncher.launch(activity, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    Incidence incidence1=data.getParcelableExtra("incidence");
                    Log.e(TAG, "Incidencia creada con éxito: " + incidence1.externalIncidenceId);
                    // doSomeOperations();
                    Toast.makeText(MainActivity.this, "Incidencia creada con éxito: " + incidence1.externalIncidenceId, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No se ha podido crear la incidencia", Toast.LENGTH_SHORT).show();
                }
            });
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Do your task here.
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    */
}