package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Vehicle implements Parcelable {
    public String id;
    public String externalVehicleId;
    public String licensePlate;
    public String registrationYear;
    public String brand;
    public String model;
    public VehicleType vehicleType;
    public ColorType color;
    public Policy policy;
    public Insurance insurance;
    public Beacon beacon;
    public ArrayList<Incidence> incidences;
    public ArrayList<Driver> drivers;

    //vehiculo ya creado

    public String image;

    public Vehicle()
    {
    }

    public Vehicle(Vehicle v)
    {
        this.id = v.id;
        this.externalVehicleId = externalVehicleId;
        this.licensePlate = v.licensePlate;
        this.registrationYear = v.registrationYear;
        this.brand = v.brand;
        this.model = v.model;
        this.vehicleType = v.vehicleType;
        this.color = v.color;
        this.policy = v.policy;
        this.insurance = v.insurance;
        this.beacon = v.beacon;
        this.incidences = v.incidences;
        this.drivers = v.drivers;
        this.image = v.image;
    }

    protected Vehicle(Parcel in) {
        id = in.readString();
        externalVehicleId = in.readString();
        licensePlate = in.readString();
        registrationYear = in.readString();
        brand = in.readString();
        model = in.readString();
        vehicleType = in.readParcelable(VehicleType.class.getClassLoader());
        color = in.readParcelable(ColorType.class.getClassLoader());
        policy = in.readParcelable(Policy.class.getClassLoader());
        insurance = in.readParcelable(Insurance.class.getClassLoader());
        beacon = in.readParcelable(Beacon.class.getClassLoader());
        incidences = in.createTypedArrayList(Incidence.CREATOR);
        drivers = in.createTypedArrayList(Driver.CREATOR);
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(externalVehicleId);
        dest.writeString(licensePlate);
        dest.writeString(registrationYear);
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeParcelable(vehicleType, flags);
        dest.writeParcelable(color, flags);
        dest.writeParcelable(policy, flags);
        dest.writeParcelable(insurance, flags);
        dest.writeParcelable(beacon, flags);
        dest.writeTypedList(incidences);
        dest.writeTypedList(drivers);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    public String getName()
    {
        return brand + " " + model;
    }

    public boolean hasIncidencesActive()
    {
        boolean res = false;

        if (incidences != null)
        {
            for (int i = 0 ; i < incidences.size(); i++)
            {
                Incidence incidence = incidences.get(i);
                if (!incidence.isClosed() && !incidence.isCanceled())
                {
                    res = true;
                    break;
                }
            }
        }

        return res;
    }

    public boolean hasPolicyIncompleted()
    {
        boolean res = true;

        if (insurance != null && insurance.name != null)
        {
            if (policy != null && policy.policyNumber != null && policy.dni != null && policy.policyEnd != null)
            {
                res = false;
            }
        }

        return res;
    }
}
