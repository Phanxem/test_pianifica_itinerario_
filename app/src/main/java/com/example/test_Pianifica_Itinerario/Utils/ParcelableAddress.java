package com.example.test_Pianifica_Itinerario.Utils;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;


public class ParcelableAddress implements Parcelable {

    private Address address;


    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ParcelableAddress(Address address){
        this.address = address;
    }

    protected ParcelableAddress(Parcel in) {
        address = in.readParcelable(Address.class.getClassLoader());
    }

    public static final Creator<ParcelableAddress> CREATOR = new Creator<ParcelableAddress>() {
        @Override
        public ParcelableAddress createFromParcel(Parcel in) {
            return new ParcelableAddress(in);
        }

        @Override
        public ParcelableAddress[] newArray(int size) {
            return new ParcelableAddress[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(address, flags);
    }
}
