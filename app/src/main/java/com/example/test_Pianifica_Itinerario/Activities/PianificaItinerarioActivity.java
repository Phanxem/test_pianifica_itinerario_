package com.example.test_Pianifica_Itinerario.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.test_Pianifica_Itinerario.Controllers.ImportaFileGPXController;
import com.example.test_Pianifica_Itinerario.Controllers.PianificaItinerarioController;
import com.example.test_Pianifica_Itinerario.Models.PianificaItinerarioModel;
import com.example.test_Pianifica_Itinerario.ObserverPattern.Observer;
import com.example.test_Pianifica_Itinerario.R;
import com.example.test_Pianifica_Itinerario.Utils.AddressUtils;
import com.example.test_Pianifica_Itinerario.Utils.Utils;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.GoogleRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;


public class PianificaItinerarioActivity extends AppCompatActivity implements Observer {

    public static final String DEFAULT_MESSAGE_STARTING_POINT = "Scegli il punto di partenza";
    public static final String DEFAULT_MESSAGE_DESTINATION_POINT = "Scegli il punto di destinazione";



    //Controller
    PianificaItinerarioController pianificaItinerarioController;

    //Model
    PianificaItinerarioModel pianificaItinerarioModel;


    //UI Object
    ImageView imageView_iconMenu;

    ConstraintLayout constraintLayout_puntiInteresse;

    RelativeLayout relativeLayout_campoPuntoPartenza;
    RelativeLayout relativeLayout_campoPuntoDestinazione;

    TextView textView_nomePuntoPartenza;
    TextView textView_nomePuntoDestinazione;

    RelativeLayout relativeLayout_mostraPuntiIntermedi;
    ConstraintLayout constraintLayout_puntiIntermedi;
    RelativeLayout relativeLayout_nascondiPuntiIntermedi;
    RelativeLayout relativeLayout_aggiungiPuntiIntermedi;

    ImageView imageView_iconClosePuntoPartenza;
    ImageView imageView_iconClosePuntoDestinazione;


    TextView textView_selezionaPuntoDaMappa;
    ConstraintLayout constraintLayout_opzioniSelezionaPuntoDaMappa;
    TextView textView_nomeIndirizzoPuntatoDaMappa;
    Button button_impostaPuntoPartenza;
    Button button_impostaPuntoDestinazione;
    Button button_impostaPuntoIntermedio;


    ListView listView_puntiIntermedi;


    //UI Map Object

    MapView mapView;

    Overlay mapOverlay;

    FolderOverlay itineraryMarkers;
    Marker selectionMarker;

    ArrayList<Polyline> roadOverlays;





    //----------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOsmdroidConfiguration();
        setContentView(R.layout.activity_pianifica_itinerario);

        //DEFINIZIONE MAPVIEW
        mapView = findViewById(R.id.InsertItinerary_mapView_mappa);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        //Then we add the ability to zoom with 2 fingers (multi-touch)
        mapView.setMultiTouchControls(true);

        //We can move the map on a default view point. For this, we need access to the map controller:
        IMapController mapController = mapView.getController();
        mapController.setZoom(9.5);
        mapView.setMaxZoomLevel(17.0);
        mapView.setMinZoomLevel(5.0);

        //remove zooms default buttons
        CustomZoomButtonsController zoomController = mapView.getZoomController();
        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        //set mapview to a geopoint center
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);

        mapOverlay = new Overlay(){
            @Override
            public void draw(Canvas canvas, MapView mapView, boolean shadow) {
                super.draw(canvas,mapView,shadow);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent, MapView mapView) {
                Projection projection = mapView.getProjection();
                GeoPoint geoPoint = (GeoPoint) projection.fromPixels((int)motionEvent.getX(), (int)motionEvent.getY());

                Log.i("PianificaActivity: ", "Lat: " + geoPoint.getLatitude() + " | Lon: " + geoPoint.getLongitude());

                pianificaItinerarioController.setPointedAddress(geoPoint);

                return true;
            }
        };
        mapView.getOverlays().add(mapOverlay);

        selectionMarker = new Marker(mapView);
        //selectionMarker.setIcon(getDrawable(R.drawable.ic_selection_marker));
        selectionMarker.setIcon(Utils.getMarker(this, R.drawable.ic_selection_marker,null));
        mapView.getOverlays().add(selectionMarker);

        itineraryMarkers = new FolderOverlay();
        mapView.getOverlays().add(itineraryMarkers);

        roadOverlays = null;


        //------------------------------------------------

        imageView_iconMenu = findViewById(R.id.InsertItinerary_imageView_iconMenu);

        constraintLayout_puntiInteresse = findViewById(R.id.InsertItinerary_constraintLayout_puntiInteresse);
        relativeLayout_campoPuntoPartenza = findViewById(R.id.InsertItinerary_relativeLayout_puntoPartenza);
        textView_nomePuntoPartenza = findViewById(R.id.InsertItinerary_textView_nomePuntoPartenza);
        imageView_iconClosePuntoPartenza = findViewById(R.id.InsertItinerary_imageView_iconClosePuntoPartenza);
        relativeLayout_campoPuntoDestinazione = findViewById(R.id.InsertItinerary_relativeLayout_puntoDestinazione);
        textView_nomePuntoDestinazione = findViewById(R.id.InsertItinerary_textView_nomePuntoDestinazione);
        imageView_iconClosePuntoDestinazione = findViewById(R.id.InsertItinerary_imageView_iconClosePuntoDestinzione);

        relativeLayout_mostraPuntiIntermedi = findViewById(R.id.InsertItinerary_relativeLayout_mostraPuntiIntermedi);
        constraintLayout_puntiIntermedi = findViewById(R.id.InsertItinerary_constraintLayout_puntiIntermedi);
        relativeLayout_nascondiPuntiIntermedi = findViewById(R.id.InsertItinerary_relativeLayout_nascondiPuntiIntermedi);
        relativeLayout_aggiungiPuntiIntermedi = findViewById(R.id.InsertItinerary_relativeLayout_aggiungiPuntoIntermedio);

        textView_selezionaPuntoDaMappa = findViewById(R.id.InsertItinerary_textView_selezionaPuntoInteressatoDaMappa);
        constraintLayout_opzioniSelezionaPuntoDaMappa = findViewById(R.id.InsertItinerary_constraintLayout_opzioniSelezioneDaMappa);
        textView_nomeIndirizzoPuntatoDaMappa = findViewById(R.id.InsertItinerary_textView_nomeIndirizzoSelezionatoDaMappa);
        button_impostaPuntoPartenza = findViewById(R.id.InsertItinerary_button_impostaPuntoPartenza);
        button_impostaPuntoDestinazione = findViewById(R.id.InsertItinerary_button_impostaPuntoDestinazione);
        button_impostaPuntoIntermedio = findViewById(R.id.InsertItinerary_button_impostaPuntoIntermedio);

        //------------------

        pianificaItinerarioController = new PianificaItinerarioController(this);

        pianificaItinerarioModel = pianificaItinerarioController.getPianificaItinerarioModel();
        pianificaItinerarioModel.registerObserver(this);

        listView_puntiIntermedi = findViewById((R.id.InsertItinerary_listView_puntiIntermedi));
        pianificaItinerarioController.initListViewIntermediatePoints(listView_puntiIntermedi);
    }


    public void pressMenuIcon(View view) {
        PopupMenu popupMenu = new PopupMenu(this,imageView_iconMenu);

        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.PopupMenu_popupMenu_altro){
                    if(!pianificaItinerarioController.hasStoragePermissions()){
                        ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ImportaFileGPXController.REQUEST_CODE);
                        return false;
                    }

                    Log.d("dsfs","click");
                    return true;
                }
                else{
                    return false;
                }
            }
        });

        popupMenu.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PianificaItinerarioController.REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("TAG:sd " , "Calling Permission is granted");
                //TODO go to importa gpx page
            }
            else {
                //TODO ERRORE
                Log.i("TAG:sd ", "Calling Permission is denied");
            }
        }
    }

    public void pressStartingPointField(View view) {
        pianificaItinerarioController.selectStartingPoint();
    }

    public void pressDestinationPointField(View view) {
        pianificaItinerarioController.selectDestinationPoint();
    }

    public void pressAddIntermediatePoint(View view) {
        pianificaItinerarioController.addIntermediatePoint();
    }

    public void pressShowIntermediatePoints(View view) {
        updateUIIntermediatePointsVisibility();
    }

    public void pressHideIntermediatePoints(View view) {
        updateUIIntermediatePointsVisibility();
    }

    public void pressIconCancelStartingPoint(View view) {
        pianificaItinerarioController.cancelStartingPoint();
    }

    public void pressIconCancelDestinationPoint(View view) {
        pianificaItinerarioController.cancelDestinationPoint();
    }

    public void pressIconCancel(View view) {
        pianificaItinerarioController.cancelSetAddressSelected();
    }

    public void pressButtonSetAsStartingPoint(View view) {
        pianificaItinerarioController.setSelectedAddressAsStartingPoint();
    }

    public void pressButtonSetAsDestinationPoint(View view) {
        pianificaItinerarioController.setSelectedAddressAsDestinationPoint();
    }

    public void pressButtonSetAsIntermediatePoint(View view) {
        pianificaItinerarioController.setSelectedAddressAsIntermediatePoint();
    }




    @Override
    public void update() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                updateUIStartingDestinationPoints();
                updateUIIntermediatePointsOptionsVisibility();

                updateUIMap();

                updateUISelectFromMapOptionsVisibility();

            }
        });
    }

    private void updateUIStartingDestinationPoints(){
        if (pianificaItinerarioModel.hasStartingPoint()){
            textView_nomePuntoPartenza.setText(pianificaItinerarioModel.getStartingPointName());
            imageView_iconClosePuntoPartenza.setVisibility(View.VISIBLE);
        }
        else {
            textView_nomePuntoPartenza.setText(DEFAULT_MESSAGE_STARTING_POINT);
            imageView_iconClosePuntoPartenza.setVisibility(View.INVISIBLE);
        }

        if(pianificaItinerarioModel.hasDestinationPoint()){
            textView_nomePuntoDestinazione.setText(pianificaItinerarioModel.getDestinationPointName());
            imageView_iconClosePuntoDestinazione.setVisibility(View.VISIBLE);
        }
        else {
            textView_nomePuntoDestinazione.setText(DEFAULT_MESSAGE_DESTINATION_POINT);
            imageView_iconClosePuntoDestinazione.setVisibility(View.INVISIBLE);
        }
    }

    private void updateUIIntermediatePointsOptionsVisibility(){
        //SE NON SONO STATI INSERITI I PUNTI DI PARTENZA E DESTINAZIONE
        if(!pianificaItinerarioModel.hasStartingPoint() || !pianificaItinerarioModel.hasDestinationPoint()){
            relativeLayout_aggiungiPuntiIntermedi.setVisibility(View.GONE);
            relativeLayout_mostraPuntiIntermedi.setVisibility(View.GONE);
            constraintLayout_puntiIntermedi.setVisibility(View.GONE);
            return;
        }

        relativeLayout_aggiungiPuntiIntermedi.setVisibility(View.VISIBLE);

        if(!pianificaItinerarioModel.hasIntermediatePoint()){
            relativeLayout_mostraPuntiIntermedi.setVisibility(View.GONE);
            constraintLayout_puntiIntermedi.setVisibility(View.GONE);
            return;
        }

        //SE LE OPZIONI mostraPuntiIntermedi E nascondiPuntiIntermedi NON SONO VISIBILI
        if(relativeLayout_mostraPuntiIntermedi.getVisibility() == View.GONE && constraintLayout_puntiIntermedi.getVisibility() == View.GONE){
            relativeLayout_mostraPuntiIntermedi.setVisibility(View.VISIBLE);
        }
    }

    private void updateUIIntermediatePointsVisibility(){
        if(relativeLayout_mostraPuntiIntermedi.getVisibility() == View.VISIBLE){
            relativeLayout_mostraPuntiIntermedi.setVisibility(View.GONE);
            constraintLayout_puntiIntermedi.setVisibility(View.VISIBLE);
            return;
        }
        relativeLayout_mostraPuntiIntermedi.setVisibility(View.VISIBLE);
        constraintLayout_puntiIntermedi.setVisibility(View.GONE);
    }

    private void updateUISelectFromMapOptionsVisibility(){

        Integer indexPointSelected = pianificaItinerarioModel.getIndexPointSelected();
        Address currentAddressPointedOnMap = pianificaItinerarioModel.getAddressPointedOnMap();

        //NO RETURN FROM SEARCH POINT
        if(indexPointSelected == null){

            constraintLayout_puntiInteresse.setVisibility(View.VISIBLE);
            textView_selezionaPuntoDaMappa.setVisibility(View.GONE);

            //NO ADDRESS SELECTED ON MAP
            if(currentAddressPointedOnMap == null){
                constraintLayout_opzioniSelezionaPuntoDaMappa.setVisibility(View.GONE);
                return;
            }

            //ADDRESS SELECTED ON MAP
            constraintLayout_opzioniSelezionaPuntoDaMappa.setVisibility(View.VISIBLE);
            textView_nomeIndirizzoPuntatoDaMappa.setText(AddressUtils.getAddressName(currentAddressPointedOnMap));

            button_impostaPuntoPartenza.setVisibility(View.VISIBLE);
            button_impostaPuntoDestinazione.setVisibility(View.VISIBLE);
            if(pianificaItinerarioModel.hasStartingPoint() && pianificaItinerarioModel.hasDestinationPoint()) {
                button_impostaPuntoIntermedio.setVisibility(View.VISIBLE);
            }
            else button_impostaPuntoIntermedio.setVisibility(View.GONE);
            return;
        }

        //RETURN FROM SEARCH POINT
        constraintLayout_puntiInteresse.setVisibility(View.GONE);
        textView_selezionaPuntoDaMappa.setVisibility(View.VISIBLE);

        //NO ADDRESS SELECTED ON MAP
        if(currentAddressPointedOnMap == null){
            constraintLayout_opzioniSelezionaPuntoDaMappa.setVisibility(View.GONE);
            return;
        }

        //ADDRESS SELECTED ON MAP
        constraintLayout_opzioniSelezionaPuntoDaMappa.setVisibility(View.VISIBLE);
        textView_nomeIndirizzoPuntatoDaMappa.setText(AddressUtils.getAddressName(currentAddressPointedOnMap));

        Log.d("safdfdsfsdfds",": " + indexPointSelected);

        //IL PUNTO SELEZIONATO E' IL PUNTO INIZIALE
        if(indexPointSelected == PianificaItinerarioController.STARTING_POINT_CODE){
            Log.d("testGSDFDFSD", "STARTING POINT");
            button_impostaPuntoPartenza.setVisibility(View.VISIBLE);
            button_impostaPuntoDestinazione.setVisibility(View.GONE);
            button_impostaPuntoIntermedio.setVisibility(View.GONE);
        }
        //IL PUNTO SELEZIONATO E' IL PUNTO DI DESTINAZIONE
        else if(indexPointSelected == PianificaItinerarioController.DESTINATION_POINT_CODE){
            Log.d("testGSDFDFSD", "DESTINATION POINT");
            button_impostaPuntoPartenza.setVisibility(View.GONE);
            button_impostaPuntoDestinazione.setVisibility(View.VISIBLE);
            button_impostaPuntoIntermedio.setVisibility(View.GONE);
        }
        //IL PUNTO SELEZIONATO E' UN PUNTO INTERMEDIO (GIA' ESISTENTE OPPURE NUOVO)
        else if(pianificaItinerarioModel.isValidPointIndex(indexPointSelected)){
            Log.d("testGSDFDFSD", "INTERMEDIATE POINT");
            button_impostaPuntoPartenza.setVisibility(View.GONE);
            button_impostaPuntoDestinazione.setVisibility(View.GONE);
            button_impostaPuntoIntermedio.setVisibility(View.VISIBLE);
        }
        else{
            Log.d("testGSDFDFSD", "error");
            //todo errore
            return;
        }
    }

    //---

    public void initOsmdroidConfiguration(){
        Context appContext = getApplicationContext();
        Configuration.getInstance().load(appContext, PreferenceManager.getDefaultSharedPreferences(appContext));
    }

    public void initMap(){
        mapView = findViewById(R.id.InsertItinerary_mapView_mappa);

        mapView.setTileSource(TileSourceFactory.MAPNIK);

        //Then we add the ability to zoom with 2 fingers (multi-touch)
        mapView.setMultiTouchControls(true);

        //We can move the map on a default view point. For this, we need access to the map controller:
        IMapController mapController = mapView.getController();
        mapController.setZoom(9.5);
        mapView.setMaxZoomLevel(17.0);
        mapView.setMinZoomLevel(5.0);

        //remove zooms default buttons
        CustomZoomButtonsController zoomController = mapView.getZoomController();
        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        //set mapview to a geopoint center
        GeoPoint startPoint = new GeoPoint(0F, 0F);
        mapController.setCenter(startPoint);

        mapOverlay = new Overlay(){
            @Override
            public void draw(Canvas canvas, MapView mapView, boolean shadow) {
                super.draw(canvas,mapView,shadow);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent, MapView mapView) {
                Projection projection = mapView.getProjection();
                GeoPoint geoPoint = (GeoPoint) projection.fromPixels((int)motionEvent.getX(), (int)motionEvent.getY());

                Log.i("PianificaActivity: ", "Lat: " + geoPoint.getLatitude() + " | Lon: " + geoPoint.getLongitude());

                pianificaItinerarioController.setPointedAddress(geoPoint);

                return true;
            }
        };
        mapView.getOverlays().add(mapOverlay);


        selectionMarker = new Marker(mapView);
        //selectionMarker.setIcon(getDrawable(R.drawable.ic_selection_marker));
        selectionMarker.setIcon(Utils.getMarker(this, R.drawable.ic_selection_marker,null));

        mapView.getOverlays().add(selectionMarker);


        itineraryMarkers = new FolderOverlay();
        mapView.getOverlays().add(itineraryMarkers);

        roadOverlays = null;

    }


    public void updateUIMap(){

        Address currentAddressPointedOnMap = pianificaItinerarioModel.getAddressPointedOnMap();

        if(currentAddressPointedOnMap != null) {
            GeoPoint geoPoint = new GeoPoint(currentAddressPointedOnMap.getLatitude(),currentAddressPointedOnMap.getLongitude());
            selectionMarker.setVisible(true);
            selectionMarker.setPosition(geoPoint);
            selectionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            //mapView.getController().setCenter(geoPoint);
            mapView.invalidate();
            return;
        }

        selectionMarker.setVisible(false);

        updateUIMapWithMarkers();
        updateUIMapWithRoads();




        if(pianificaItinerarioModel.hasStartingPoint() && pianificaItinerarioModel.hasDestinationPoint()){
            BoundingBox boundingBox = generateBoundingBoxWithAllMarkers();
            mapView.zoomToBoundingBox(boundingBox,true);
        }
        else if (pianificaItinerarioModel.hasStartingPoint() || pianificaItinerarioModel.hasDestinationPoint()){
            if(pianificaItinerarioModel.hasStartingPoint()){
                mapView.getController().setCenter(pianificaItinerarioModel.getStaratingGeoPoint());
                return;
            }
            mapView.getController().setCenter(pianificaItinerarioModel.getDestinationGeoPoint());
        }

        mapView.invalidate();
    }

    public BoundingBox generateBoundingBoxWithAllMarkers2(){
        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLong = Double.MAX_VALUE;
        double maxLong = Double.MIN_VALUE;

        List<Overlay> items = itineraryMarkers.getItems();
        List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();

        for(Overlay item : items){
            if(item instanceof Marker) {
                Marker marker = (Marker) item;
                geoPoints.add(marker.getPosition());
            }
        }

        for (GeoPoint geoPoint : geoPoints) {
            if (geoPoint.getLatitude() < minLat) minLat = geoPoint.getLatitude();
            if (geoPoint.getLatitude() > maxLat) maxLat = geoPoint.getLatitude();
            if (geoPoint.getLongitude() < minLong) minLong = geoPoint.getLongitude();
            if (geoPoint.getLongitude() > maxLong) maxLong = geoPoint.getLongitude();
        }

        BoundingBox boundingBox = new BoundingBox(maxLat, maxLong, minLat, minLong);
        //mapView.zoomToBoundingBox(boundingBox,true);

        return boundingBox;
    }

    public BoundingBox generateBoundingBoxWithAllMarkers(){
        List<Overlay> items = itineraryMarkers.getItems();
        List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();

        for(Overlay item : items){
            if(item instanceof Marker) {
                Marker marker = (Marker) item;
                geoPoints.add(marker.getPosition());
            }
        }

        BoundingBox boundingBox = BoundingBox.fromGeoPoints(geoPoints);
        boundingBox.setLatNorth(boundingBox.getLatNorth() + 0.1F);
        boundingBox.setLatSouth(boundingBox.getLatSouth() - 0.1F);
        boundingBox.setLonEast(boundingBox.getLonEast() + 0.01F);
        boundingBox.setLonWest(boundingBox.getLonWest() - 0.01F);
        //mapView.zoomToBoundingBox(boundingBox,true);

        return boundingBox;
    }

    public void updateUIMapWithMarkers(){
        itineraryMarkers.getItems().clear();

        if(pianificaItinerarioModel.hasStartingPoint()){
            itineraryMarkers.add(createInterestPointMarker(PianificaItinerarioController.STARTING_POINT_CODE));
        }
        if(pianificaItinerarioModel.hasIntermediatePoint()){

            List<Address> intermediatePoints = pianificaItinerarioModel.getIntermediatePoints();

            for(int i = 0; i < intermediatePoints.size(); i++){
                itineraryMarkers.add(createInterestPointMarker(i));
            }
        }
        if(pianificaItinerarioModel.hasDestinationPoint()){
            itineraryMarkers.add(createInterestPointMarker(PianificaItinerarioController.DESTINATION_POINT_CODE));
        }
    }

    public Marker createInterestPointMarker(int index){
        Marker marker = new Marker(mapView);

        String stringIndex;
        Address address;

        if(index == PianificaItinerarioController.STARTING_POINT_CODE){
            stringIndex = "S";
            address = pianificaItinerarioModel.getStartingPoint();
        }
        else if(index == PianificaItinerarioController.DESTINATION_POINT_CODE){
            stringIndex = "D";
            address = pianificaItinerarioModel.getDestinationPoint();
        }
        else{
            stringIndex = String.valueOf(index+1);
            address = pianificaItinerarioModel.getIntermediatePoint(index);
        }

        marker.setIcon(Utils.getMarker(this, R.drawable.ic_road_marker,stringIndex));
        marker.setVisible(true);
        marker.setPosition(new GeoPoint(address.getLatitude(), address.getLongitude()));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

        return marker;

    }

    public void updateUIMapWithRoads(){


        List<Overlay> mapOverlays = mapView.getOverlays();
        if (roadOverlays != null){
            for (int i=0; i<roadOverlays.size(); i++) mapOverlays.remove(roadOverlays.get(i));
            roadOverlays = null;
        }

        List<Road> roads = pianificaItinerarioModel.getRoads();


        Log.i("TESTmap", ",apf,d");

        if (roads == null || roads.isEmpty()) {
            Log.i("AAAAAAAAAAAAA", "dgdfgdfgdf");
            return;
        }



        /*
        if (roads[0].mStatus == Road.STATUS_TECHNICAL_ISSUE)
            Toast.makeText(map.getContext(), "Technical issue when getting the route", Toast.LENGTH_SHORT).show();
        else if (roads[0].mStatus > Road.STATUS_TECHNICAL_ISSUE) //functional issues
            Toast.makeText(map.getContext(), "No possible route here", Toast.LENGTH_SHORT).show();
        */

        roadOverlays = new ArrayList<Polyline>();
        for (int i=0; i<roads.size(); i++) {
            Log.i("MAP TEST", " : " + i);
            Polyline roadPolyline = RoadManager.buildRoadOverlay(roads.get(i));
            roadOverlays.add(roadPolyline);
            /*
            if (mWhichRouteProvider == GRAPHHOPPER_BICYCLE || mWhichRouteProvider == GRAPHHOPPER_PEDESTRIAN) {
                Paint p = roadPolyline.getPaint();
                p.setPathEffect(new DashPathEffect(new float[]{10, 5}, 0));
            }
            */

            String routeDesc = roads.get(i).getLengthDurationText(this, -1);
            roadPolyline.setTitle(routeDesc);
            //roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
            roadPolyline.setRelatedObject(i);
            //roadPolyline.setOnClickListener(new RoadOnClickListener());
            mapOverlays.add(1, roadPolyline);
            //we insert the road overlays at the "bottom", just above the MapEventsOverlay,
            //to avoid covering the other overlays.
        }
        selectRoad(0);
    }


    void selectRoad(int roadIndex){
        //mSelectedRoad = roadIndex;
        //putRoadNodes(roads[roadIndex]);

        //Set route info in the text view:
        //TextView textView = (TextView)findViewById(R.id.routeInfo);
        //textView.setText(mRoads[roadIndex].getLengthDurationText(this, -1));
        for (int i=0; i<roadOverlays.size(); i++){
            Paint p = roadOverlays.get(i).getPaint();
            if (i == roadIndex) {
                p.setColor(0x800000FF);//blue
                p.setStrokeWidth(10.0F);
            }
            else
                p.setColor(0x90666666); //grey
        }
        mapView.invalidate();
    }




/*
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

 */
}

























