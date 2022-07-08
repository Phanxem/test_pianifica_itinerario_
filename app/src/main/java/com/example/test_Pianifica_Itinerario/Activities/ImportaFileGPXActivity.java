package com.example.test_Pianifica_Itinerario.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.test_Pianifica_Itinerario.Controllers.ImportaFileGPXController;
import com.example.test_Pianifica_Itinerario.Controllers.RicercaPuntoController;
import com.example.test_Pianifica_Itinerario.Models.ImportaFileGPXModel;
import com.example.test_Pianifica_Itinerario.ObserverPattern.Observer;
import com.example.test_Pianifica_Itinerario.R;

import java.io.File;
import java.util.List;

public class ImportaFileGPXActivity extends AppCompatActivity implements Observer {

    //Controller
    ImportaFileGPXController importaFileGPXController;

    //Model
    ImportaFileGPXModel importaFileGPXModel;

    TextView textView_directoryName;

    RelativeLayout relativeLayout_parentDirectory;

    ListView listView_files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importa_file_gpx);

        importaFileGPXController = new ImportaFileGPXController(this);

        importaFileGPXModel = importaFileGPXController.getImportaFileGPXModel();
        importaFileGPXModel.registerObserver(this);

        textView_directoryName = findViewById(R.id.ImportaFileGPX_textView_nomeDirectory);

        relativeLayout_parentDirectory = findViewById(R.id.ImportaFileGPX_relativeLayout_directoryPadre);

        listView_files = findViewById(R.id.ImportaFileGPX_listView_listaFiles);
        importaFileGPXController.initListViewFiles(listView_files);


    }


    public void pressIconBack(View view) {
    }

    public void pressButton(View view) {

         importaFileGPXController.openDirectory(Environment.getExternalStorageDirectory());
         //importaFileGPXController.openDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));



        List<File> files = importaFileGPXModel.getFiles();
        for(File file : files){
            Log.i("SDMAòG: ", file.getName());
        }

        Log.i("NUM: ", " " + files.size());

    }

    public void pressParentDirectory(View view) {
        importaFileGPXController.openDirectory(importaFileGPXModel.getParentDirectory());

    }

    @Override
    public void update() {

        textView_directoryName.setText(importaFileGPXModel.getCurrentDirectory().getPath());

        if(importaFileGPXModel.hasParentDirectory()){
            relativeLayout_parentDirectory.setVisibility(View.VISIBLE);

            //File parentDirectory = importaFileGPXModel.getParentDirectory();
            //textView_parentDirectory.setText(parentDirectory.getName());
            return;
        }
        relativeLayout_parentDirectory.setVisibility(View.GONE);

    }


/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ImportaFileGPXController.REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("TAG:sd " , "Calling Permission is granted");
                importaFileGPXController.testingFiles();
            }
            else {
                //TODO ERRORE
                Log.i("TAG:sd ", "Calling Permission is denied");
            }
        }
    }

 */
}