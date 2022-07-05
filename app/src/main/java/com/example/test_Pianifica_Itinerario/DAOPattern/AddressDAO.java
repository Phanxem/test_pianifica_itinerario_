package com.example.test_Pianifica_Itinerario.DAOPattern;

import android.location.Address;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public interface AddressDAO {
    Address findInterestPointByGeoPoint(GeoPoint geoPoint);

    ArrayList<Address> findInterestPointsByString(String string);
}
