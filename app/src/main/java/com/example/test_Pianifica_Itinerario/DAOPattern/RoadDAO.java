package com.example.test_Pianifica_Itinerario.DAOPattern;

import android.location.Address;

import org.osmdroid.bonuspack.routing.Road;

import java.util.ArrayList;
import java.util.List;

public interface RoadDAO {
    Road findRoadByAddresses(Address startAddress, Address destinationAddress);
    ArrayList<Road> findRoadsByAddresses(ArrayList<Address> addresses);

}
