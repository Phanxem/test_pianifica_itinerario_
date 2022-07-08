package com.example.test_Pianifica_Itinerario.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.test_Pianifica_Itinerario.Controllers.RicercaPuntoController;
import com.example.test_Pianifica_Itinerario.Models.RicercaPuntoModel;
import com.example.test_Pianifica_Itinerario.ObserverPattern.Observer;
import com.example.test_Pianifica_Itinerario.R;


public class RicercaPuntoActivity extends AppCompatActivity implements Observer {

    private final static int MAX_NUM_RESULT = 20;

    //Controller
    RicercaPuntoController ricercaPuntoController;
    LocationManager locationManager;

    //Model
    RicercaPuntoModel resultPoints;


    EditText editText_barraRicerca;
    ImageView imageView_iconaIndietro;

    ConstraintLayout constraintLayout_opzioniSelezione;
    RelativeLayout relativeLayout_opzionePuntoAttuale;
    RelativeLayout relativeLayout_opzioneSelezionaDaMappa;

    ConstraintLayout constraintLayout_risultatiPunti;
    ListView listView_risultatiPunti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_punto);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ricercaPuntoController = new RicercaPuntoController(this, locationManager);

        resultPoints = ricercaPuntoController.getResultPoints();
        resultPoints.registerObserver(this);

        editText_barraRicerca = findViewById(R.id.pointSearch_searchView_barraRicerca);
        editText_barraRicerca.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    pressSearchButton();
                    editText_barraRicerca.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    return true;
                }
                return false;
            }

        });

        imageView_iconaIndietro = findViewById(R.id.pointSearch_imageView_iconaIndietro);

        constraintLayout_opzioniSelezione = findViewById(R.id.pointSearch_constraintLayout_opzioniSelezionePunto);
        relativeLayout_opzionePuntoAttuale = findViewById(R.id.pointSearch_relativeLayout_posizioneAttuale);
        relativeLayout_opzioneSelezionaDaMappa = findViewById(R.id.pointSearch_relativeLayout_selezionaDaMappa);

        constraintLayout_risultatiPunti = findViewById(R.id.pointSearch_constraintLayout_sezioneRisultati);

        listView_risultatiPunti = findViewById(R.id.pointSearch_listView_risultati);
        ricercaPuntoController.initListViewResultPoints(listView_risultatiPunti);

    }


    public void pressIconBack(View view) {
        onBackPressed();
    }

    public void pressCurrentPositionOption(View view) {
        Integer resultCode = ricercaPuntoController.selectCurrentPosition();

        if(resultCode == RicercaPuntoController.CURRENT_POSITION_GPS_FEATURE_NOT_PRESENT){
            //TODO ERRORE
            return;
        }

        if(resultCode == RicercaPuntoController.CURRENT_POSITION_GPS_DISABLED){
            //TODO ERRORE
            return;
        }
        if (resultCode == RicercaPuntoController.CURRENT_POSITION_PERMISSIONS_REQUIRED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RicercaPuntoController.REQUEST_CODE);
            return;
        }

        if(resultCode == RicercaPuntoController.CURRENT_POSITION_NULL){
            //TODO ERRORE
            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RicercaPuntoController.REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("TAG:sd " , "Calling Permission is granted");
                ricercaPuntoController.selectCurrentPosition();
            }
            else {
                //TODO ERRORE
                Log.i("TAG:sd ", "Calling Permission is denied");
            }
        }
    }


    public void pressSelectFromMapOption(View view) {
        ricercaPuntoController.selectFromMap();
    }


    public void pressSearchButton(){
        String searchString = editText_barraRicerca.getText().toString();
        ricercaPuntoController.searchInterestPoint(searchString, MAX_NUM_RESULT);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }




    @Override
    public void update() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUISelectPointOptionsVisibility();
            }
        });
    }

    public void updateUISelectPointOptionsVisibility(){
        if(resultPoints.hasResultPoints()){
            constraintLayout_opzioniSelezione.setVisibility(View.GONE);
            constraintLayout_risultatiPunti.setVisibility(View.VISIBLE);
            return;
        }

        constraintLayout_opzioniSelezione.setVisibility(View.VISIBLE);
        constraintLayout_risultatiPunti.setVisibility(View.GONE);
    }
}