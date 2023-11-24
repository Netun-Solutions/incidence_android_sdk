package com.e510.incidencelibraryandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

    private Button btnDeviceCreate;
    private Button btnDeviceDelete;
    private Button btnDeviceReview;
    private Button btnIncidenceCreate;
    private Button btnIncidenceClose;
    private Button btnEcommerce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String dni = "15111111T";
        String phone = "650010001";
        String externalUserId = "15001";
        String externalVehicleId = "15001";
        String licensePlate = "1511XXX";
        String externalIncidenceId = "15001";

        IdentityType dniIdentityType = new IdentityType();
        dniIdentityType.name = "dni"; // (tipo de documento de identidad: dni, nie, cif)

        VehicleType vehicleType = new VehicleType();
        vehicleType.name = "Coche";

        ColorType color = new ColorType();
        color.name = "Rojo";

        Policy policy = new Policy();
        policy.policyNumber = "111111111"; // (número de la póliza)
        policy.policyEnd = "2024-10-09"; // (fecha caducidad de la póliza)
        policy.identityType = dniIdentityType; // (tipo de documento identidad del asegurador)
        policy.dni = dni; // (documento de identidad del asegurador)

        User user = new User();
        user.externalUserId = externalUserId; // (identificador externo del usuario)
        user.name = "Nombre TEST"; // (nombre del usuario)
        user.phone = phone; // (teléfono)
        user.email = "sdkm1@tridenia.com"; // (e-mail)
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
        incidenceType.externalId = "B10"; // Pinchazo

        Incidence incidence = new Incidence();
        incidence.incidenceType = incidenceType;
        incidence.street = "Carrer Major, 2";
        incidence.city = "Barcelona";
        incidence.country = "España";
        incidence.latitude = 41.4435945;
        incidence.longitude = 2.2319534;
        incidence.externalIncidenceId = externalIncidenceId;

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
                    } else {
                        //MAKE KO ACTIONS
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
                } else {
                    //MAKE KO ACTIONS
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
                    } else {
                        //MAKE KO ACTIONS
                    }
                });

            }
        });

        btnEcommerce = findViewById(R.id.btnEcommerce);
        btnEcommerce.setOnClickListener(v -> {
            Intent activity = IncidenceLibraryManager.instance.getEcommerceViewController(user, vehicle);
            startActivity(activity);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}