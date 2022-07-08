package com.example.test_Pianifica_Itinerario.Controllers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.test_Pianifica_Itinerario.DAOImpl.AddressDAOImpl;
import com.example.test_Pianifica_Itinerario.DAOPattern.AddressDAO;
import com.example.test_Pianifica_Itinerario.ListAdapters.ListAdapterPointSearchResults;
import com.example.test_Pianifica_Itinerario.Models.RicercaPuntoModel;
import com.example.test_Pianifica_Itinerario.R;
import com.example.test_Pianifica_Itinerario.Utils.AddressUtils;
import com.example.test_Pianifica_Itinerario.Utils.GPSUtils;
import com.example.test_Pianifica_Itinerario.Utils.ParcelableAddress;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;


public class RicercaPuntoController {



    private final static String TAG = "RicercaPuntoC";

    public final static String EXTRA_ADDRESS = "ADDRESS";

    public final static int CURRENT_POSITION_OK = 0;
    public final static int CURRENT_POSITION_NULL = 1;
    public final static int CURRENT_POSITION_PERMISSIONS_REQUIRED = 2;
    public final static int CURRENT_POSITION_GPS_FEATURE_NOT_PRESENT = 3;
    public final static int CURRENT_POSITION_GPS_DISABLED = 4;
    public final static int CURRENT_POSITION_ERROR = -1;

    public static final int REQUEST_CODE = 0;


    //Views
    private AppCompatActivity activity;
    private ListAdapterPointSearchResults resultPointsListAdapter;
    //DAO
    private AddressDAO addressDAO;

    //Model
    private RicercaPuntoModel resultPoints;


    private LocationManager locationManager;

    public RicercaPuntoController(AppCompatActivity activity, LocationManager locationManager) {
        this.activity = activity;

        this.resultPoints = new RicercaPuntoModel();

        this.addressDAO = new AddressDAOImpl();

        this.resultPointsListAdapter = new ListAdapterPointSearchResults(
                activity,
                resultPoints.getResultPoints(),
                this);


        this.locationManager = locationManager;
    }

    public void initListViewResultPoints(ListView listView_risultatiPunti) {
        listView_risultatiPunti.setAdapter(resultPointsListAdapter);
    }

    public RicercaPuntoModel getResultPoints() {
        return this.resultPoints;
    }




    public Integer selectCurrentPosition() {

        if(!GPSUtils.hasGPSFeature(activity)){
            //TODO ERRORE
            Log.e("RIC_PUNT: ", "GPS NOT PRESENT");
            return CURRENT_POSITION_GPS_FEATURE_NOT_PRESENT;
        }

        if(!GPSUtils.isGPSEnabled(activity)){
            //TODO ERRORE
            Log.e("RIC_PUNT: ", "GPS DISABLED");
            return CURRENT_POSITION_GPS_DISABLED;
        }

        //Verifica se i permessi per il gps sono stati concessi
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("RIC_PUNT: ", "GPS PERMISSION REQUIRED");
            return CURRENT_POSITION_PERMISSIONS_REQUIRED;
        }

        Log.i("RIC_PUNT: ", "permesso dato");


        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null) location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location == null){
            //TODO ERRORE
            Log.e("RIC_PUNT: ", "location null");
            return CURRENT_POSITION_NULL;
        }

        GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
        Address address = addressDAO.findInterestPointByGeoPoint(geoPoint);
        ParcelableAddress parcelableAddress = new ParcelableAddress(address);

        //TODO da modificare
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ADDRESS, parcelableAddress);
        activity.setResult(AddressUtils.RESULT_OK, intent);
        activity.finish();
        return CURRENT_POSITION_OK;

    }


    public void searchInterestPoint(String searchString, int numResult) {
        if(searchString.isEmpty()){
            resultPoints.clear();
            resultPointsListAdapter.notifyDataSetChanged();
            return;
        }
        List<Address> resultAddresses = addressDAO.findInterestPointsByString(searchString, numResult);
        resultPoints.setResultPoints(resultAddresses);
        resultPointsListAdapter.notifyDataSetChanged();
    }

    public void selectResultPoint(Address resultAddress){
        ParcelableAddress parcelableAddress = new ParcelableAddress(resultAddress);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_ADDRESS, parcelableAddress);
        activity.setResult(AddressUtils.RESULT_OK, intent);
        activity.finish();

    }

    public void selectFromMap(){
        Intent intent = new Intent();
        activity.setResult(AddressUtils.RESULT_GET_FROM_MAP, intent);
        activity.finish();
    }
}
