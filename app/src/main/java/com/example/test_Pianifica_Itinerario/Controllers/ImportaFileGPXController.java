package com.example.test_Pianifica_Itinerario.Controllers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.test_Pianifica_Itinerario.ListAdapters.ListAdapterGPXFiles;
import com.example.test_Pianifica_Itinerario.Models.ImportaFileGPXModel;
import com.example.test_Pianifica_Itinerario.Utils.FileFilterUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ImportaFileGPXController {

    public static final int REQUEST_CODE = 1;

    public static final String EXTENSION_PDF = "pdf";


    private AppCompatActivity activity;
    private ListAdapterGPXFiles filesListAdapter;

    private ImportaFileGPXModel importaFileGPXModel;





    public ImportaFileGPXController(AppCompatActivity activity){
        this.activity = activity;
        this.importaFileGPXModel = new ImportaFileGPXModel();
        this.filesListAdapter = new ListAdapterGPXFiles(
                activity,
                importaFileGPXModel.getFiles(),
                this);
    }

    public void initListViewFiles(ListView listView_files) {
        listView_files.setAdapter(filesListAdapter);
    }

    public ImportaFileGPXModel getImportaFileGPXModel(){
        return this.importaFileGPXModel;
    }



    public void openDirectory(File directory){
/*
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.e("RIC_PUNT: ", "STORAGE PERMISSION REQUIRED");
            return -1;
        }
*/


        if(directory == null || !directory.isDirectory()){
            return;
        }

        List<File> files = Arrays.asList(directory.listFiles(FileFilterUtils.extensionFileFilter(EXTENSION_PDF)));

        importaFileGPXModel.set(directory,files);
        filesListAdapter.notifyDataSetChanged();

        return;
    }

    public boolean hasParentDirectory(){
        return (importaFileGPXModel.getCurrentDirectory() != null);
    }





}
