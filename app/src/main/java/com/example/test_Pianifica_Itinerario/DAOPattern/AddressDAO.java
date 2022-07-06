package com.example.test_Pianifica_Itinerario.DAOPattern;

import android.location.Address;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public interface AddressDAO {
    Address findInterestPointByGeoPoint(GeoPoint geoPoint);

    List<Address> findInterestPointsByString(String string, int maxResults);
}
