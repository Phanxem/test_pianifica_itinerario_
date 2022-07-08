package com.example.test_Pianifica_Itinerario.Controllers;

import android.Manifest;
import android.app.Activity;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.test_Pianifica_Itinerario.Activities.RicercaPuntoActivity;
import com.example.test_Pianifica_Itinerario.DAOImpl.AddressDAOImpl;
import com.example.test_Pianifica_Itinerario.DAOImpl.RoadDAOImpl;
import com.example.test_Pianifica_Itinerario.DAOPattern.AddressDAO;
import com.example.test_Pianifica_Itinerario.DAOPattern.RoadDAO;
import com.example.test_Pianifica_Itinerario.Models.PianificaItinerarioModel;
import com.example.test_Pianifica_Itinerario.ListAdapters.ListAdapterInterestPoints;
import com.example.test_Pianifica_Itinerario.Utils.AddressUtils;
import com.example.test_Pianifica_Itinerario.Utils.ParcelableAddress;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PianificaItinerarioController {

    public final static Integer STARTING_POINT_CODE = -2;
    public final static Integer DESTINATION_POINT_CODE = -1;

    public final static int REQUEST_CODE = 3;

    private final String TAG = "pianificaItinC";

    int counter = 0;

    //Views
    private AppCompatActivity activity;
    private ListAdapterInterestPoints intermediatePointsListAdapter;

    //Model
    private PianificaItinerarioModel pianificaItinerarioModel;

    //DAO
    private AddressDAO addressDAO;
    private RoadDAO roadDAO;


    private ActivityResultLauncher<Intent> startForResult;



    public PianificaItinerarioController(AppCompatActivity activity) {

        this.activity = activity;



        this.pianificaItinerarioModel = new PianificaItinerarioModel();

        this.addressDAO = new AddressDAOImpl();
        this.roadDAO = new RoadDAOImpl();

        this.intermediatePointsListAdapter = new ListAdapterInterestPoints(
                activity,
                pianificaItinerarioModel.getIntermediatePoints(),
                this);



        this.startForResult = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result == null){
                            //TODO ERROR
                            return;
                        }

                        //E' STATO SELEZIONATO UN PUNTO TRA I RISULTATI DI RICERCA
                        if(result.getResultCode() == AddressUtils.RESULT_OK){
                            if(result.getData() == null){
                                //TODO ERROR
                                return;
                            }

                            if(result.getData().getParcelableExtra(RicercaPuntoController.EXTRA_ADDRESS) != null) {

                                ParcelableAddress parcelableAddress = result.getData().getParcelableExtra(RicercaPuntoController.EXTRA_ADDRESS);
                                Address address = parcelableAddress.getAddress();

                                updateInterestPointSelected(address);

                                pianificaItinerarioModel.removeIndexPointSelected();
                                intermediatePointsListAdapter.notifyDataSetChanged();
                            }
                            Log.d(TAG, "ADDRESS trovato con successo");
                            return;
                        }

                        //E' STATA SCELTA L'OPZIONE DI SELEZIONARE UN PUNTO DALLA MAPPA
                        if(result.getResultCode() == AddressUtils.RESULT_GET_FROM_MAP){
                            pianificaItinerarioModel.notifyObservers();
                            Log.d(TAG, "GET FROM MAP");
                            return;
                        }

                    }
                }
        );


    }

    public void updateInterestPointSelected(Address address){
        Integer indexPointSelected = pianificaItinerarioModel.getIndexPointSelected();
        if(indexPointSelected == null){
            //TODO ERROR
            return;
        }

        if(indexPointSelected.equals(STARTING_POINT_CODE)){
            Log.e(TAG, "Start");
            pianificaItinerarioModel.setStartingPoint(address);
            updateRoads();
        }
        else if(indexPointSelected.equals(DESTINATION_POINT_CODE)){
            Log.e(TAG, "Destination");
            pianificaItinerarioModel.setDestinationPoint(address);
            updateRoads();
        }
        else if(indexPointSelected == pianificaItinerarioModel.getIntermediatePointsQuantity()){
            Log.e(TAG, "New Intermediate");
            pianificaItinerarioModel.addIntermediatePoint(address);
            updateRoads();
        }
        else if(indexPointSelected >= 0 || indexPointSelected < pianificaItinerarioModel.getIntermediatePointsQuantity()){
            Log.e(TAG, "Intermediate " + indexPointSelected);
            pianificaItinerarioModel.setIntermediatePoint(indexPointSelected,address);
            updateRoads();
        }
        else{
            //TODO ERRORE
            Log.e(TAG, "indice lista errato");
        }
    }



    public void initListViewIntermediatePoints(ListView listView_puntiIntermedi) {
        listView_puntiIntermedi.setAdapter(intermediatePointsListAdapter);
    }

    public PianificaItinerarioModel getPianificaItinerarioModel() {
        return this.pianificaItinerarioModel;
    }


    public void selectStartingPoint() {
        pianificaItinerarioModel.setIndexPointSelected(STARTING_POINT_CODE);
        openPointSearchScreen(activity);
    }
/*
    public void setStartingPoint() {


        //recupera il punto
        Address interestPoint = findInterestPoint();

        //TODO
        //SI PASSA ALLA SCHERMATA DI RICERCA DEL PUNTO
        //QUANDO LA SCHERMATA DI RICERCA VIENE CHIUSA, A QUESTA ATTUALE VIENE PASSATO L'ADDRESS RICHIESTO

        //aggiorna il model
        pianificaItinerarioModel.setStartingPoint(interestPoint);
    }
*/

    public void setSelectedAddressAsStartingPoint() {
        Address address = pianificaItinerarioModel.getAddressPointedOnMap();

        if(address == null){
            //TODO ERRORE
            return;
        }
        pianificaItinerarioModel.setStartingPoint(address);
        pianificaItinerarioModel.removeIndexPointSelected();
        pianificaItinerarioModel.removeAddressPointedOnMap();
        updateRoads();
    }


    public void selectDestinationPoint() {

        pianificaItinerarioModel.setIndexPointSelected(DESTINATION_POINT_CODE);
        openPointSearchScreen(activity);
    }

    public void setSelectedAddressAsDestinationPoint() {
        Address address = pianificaItinerarioModel.getAddressPointedOnMap();

        if(address == null){
            //TODO ERRORE
            return;
        }

        pianificaItinerarioModel.setDestinationPoint(address);
        pianificaItinerarioModel.removeIndexPointSelected();
        pianificaItinerarioModel.removeAddressPointedOnMap();
        updateRoads();
    }



    public void addIntermediatePoint() {
        int indexIntermediatePoint = pianificaItinerarioModel.getIntermediatePointsQuantity();

        pianificaItinerarioModel.setIndexPointSelected(indexIntermediatePoint);
        openPointSearchScreen(activity);
    }

    public void selectIntermediatePoint(int index) {
        if(!pianificaItinerarioModel.isValidPointIndex(index)) return;

        pianificaItinerarioModel.setIndexPointSelected(index);
        openPointSearchScreen(activity);
    }

    public void setSelectedAddressAsIntermediatePoint() {
        Address address = pianificaItinerarioModel.getAddressPointedOnMap();
        Integer index = pianificaItinerarioModel.getIndexPointSelected();

        if(address == null){
            //TODO ERRORE
            return;
        }

        //NUOVO PUNTO INTERMEDIO
        if(index == null || index == pianificaItinerarioModel.getIntermediatePointsQuantity()) {
            //pianificaItinerarioModel.setIndexPointSelected(pianificaItinerarioModel.getIntermediatePointsQuantity());
            pianificaItinerarioModel.addIntermediatePoint(address);
        }
        //PUNTO INTERMEDIO ESISTENTE
        else{
            pianificaItinerarioModel.setIntermediatePoint(index,address);
        }

        intermediatePointsListAdapter.notifyDataSetChanged();
        pianificaItinerarioModel.removeIndexPointSelected();
        pianificaItinerarioModel.removeAddressPointedOnMap();
        updateRoads();
    }

    public void cancelSetAddressSelected() {
        pianificaItinerarioModel.removeIndexPointSelected();
        pianificaItinerarioModel.removeAddressPointedOnMap();
        updateRoads();
    }


    public void cancelStartingPoint() {

        if (!pianificaItinerarioModel.hasIntermediatePoint()) {
            pianificaItinerarioModel.removeStartingPoint();
            updateRoads();
            return;
        }

        Address intermediatePoint = pianificaItinerarioModel.getIntermediatePoint(0);
        pianificaItinerarioModel.setStartingPoint(intermediatePoint);
        pianificaItinerarioModel.removeIntermediatePoint(0);
        updateRoads();
        intermediatePointsListAdapter.notifyDataSetChanged();
    }

    public void cancelDestinationPoint() {

        if (!pianificaItinerarioModel.hasIntermediatePoint()) {
            pianificaItinerarioModel.removeDestinationPoint();
            updateRoads();
            return;
        }

        int indexLastIntermediatePoint = pianificaItinerarioModel.getIntermediatePointsQuantity() - 1;
        Address intermediatePoint = pianificaItinerarioModel.getIntermediatePoint(indexLastIntermediatePoint);
        pianificaItinerarioModel.setDestinationPoint(intermediatePoint);
        pianificaItinerarioModel.removeIntermediatePoint(indexLastIntermediatePoint);
        updateRoads();
        intermediatePointsListAdapter.notifyDataSetChanged();

    }

    public void cancelIntermediatePoint(int index) {
        if(!pianificaItinerarioModel.isValidPointIndex(index)) return;

        pianificaItinerarioModel.removeIntermediatePoint(index);
        updateRoads();
        intermediatePointsListAdapter.notifyDataSetChanged();
    }




    //TODO funzione fittizia
    //effettuata dal DAO
    public Address findInterestPoint() {
        Address result = new Address(Locale.getDefault());
        result.setAddressLine(0,"Point: " + counter);
        counter++;
        return result;
    }


    public boolean hasStoragePermissions(){
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }











//--------------

    public void openPointSearchScreen(Activity activity){
        Intent intent = new Intent(activity, RicercaPuntoActivity.class);
        startForResult.launch(intent);
    }



//-------------

    public void setPointedAddress(GeoPoint geoPoint) {
        Address address = addressDAO.findInterestPointByGeoPoint(geoPoint);

        if(address == null){
            Log.e("EERRORE:", "address null");
            return;
        }

        pianificaItinerarioModel.setAddressPointedOnMap(address);
    }

    public void updateRoads(){
        List<Address> addresses = pianificaItinerarioModel.getAllInterestPoint();

        if(addresses.size()<=1){
            pianificaItinerarioModel.clearRoads();
            return;
        }

        List<Road> roads = roadDAO.findRoadsByAddresses(addresses);

        pianificaItinerarioModel.setRoads(roads);

        Log.d("TAGDSA", ": " + roads.isEmpty());
    }



}
