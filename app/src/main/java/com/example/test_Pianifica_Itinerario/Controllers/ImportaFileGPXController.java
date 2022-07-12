package com.example.test_Pianifica_Itinerario.Controllers;

import android.content.Intent;
import android.location.Address;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test_Pianifica_Itinerario.DAOImpl.AddressDAOImpl;
import com.example.test_Pianifica_Itinerario.DAOPattern.AddressDAO;
import com.example.test_Pianifica_Itinerario.ListAdapters.ListAdapterGPXFiles;
import com.example.test_Pianifica_Itinerario.Models.ImportaFileGPXModel;
import com.example.test_Pianifica_Itinerario.Utils.FileFilterUtils;
import com.example.test_Pianifica_Itinerario.Utils.ParcelableAddress;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;

public class ImportaFileGPXController {

    public static final int REQUEST_CODE = 1;

    public static final int RESULT_CODE_RETURN_ALL_ADDRESSES = 2;

    public static final String EXTRA_ADDRESSES = "ADDRESSES";


    private AppCompatActivity activity;
    private ListAdapterGPXFiles filesListAdapter;

    private ImportaFileGPXModel importaFileGPXModel;

    private AddressDAO addressDAO;





    public ImportaFileGPXController(AppCompatActivity activity){
        this.activity = activity;
        this.importaFileGPXModel = new ImportaFileGPXModel();
        this.filesListAdapter = new ListAdapterGPXFiles(
                activity,
                importaFileGPXModel.getFiles(),
                this);
        this.addressDAO = new AddressDAOImpl();
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

        List<File> files = Arrays.asList(directory.listFiles(FileFilterUtils.extensionFileFilter(FileFilterUtils.EXTENSION_GPX)));

        importaFileGPXModel.set(directory,files);
        filesListAdapter.notifyDataSetChanged();

        return;
    }

    public boolean openGPXFile(File gpxFile) {
        GPX.Reader gpxReader = GPX.reader();

        try {
            GPX gpx = gpxReader.read(gpxFile);
            if(gpx != null) Log.i("IMPORTGPX","gpx readed");

            List<WayPoint> wayPoints = gpx.getWayPoints();
            if(wayPoints == null || wayPoints.isEmpty()){
                Log.i("IMPORTGPX","waypoints NOT getted");
                return false;
            }


            List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
            for(WayPoint wayPoint : wayPoints){
                GeoPoint geoPoint = new GeoPoint(wayPoint.getLatitude().doubleValue(),wayPoint.getLongitude().doubleValue());
                geoPoints.add(geoPoint);
            }

            List<Address> addresses = new ArrayList<Address>();
            for(GeoPoint geoPoint: geoPoints){
                Address address = addressDAO.findInterestPointByGeoPoint(geoPoint);
                addresses.add(address);
            }

            ArrayList<ParcelableAddress> parcelableAddresses = new ArrayList<ParcelableAddress>();
            for(Address address: addresses){
                ParcelableAddress parcelableAddress = new ParcelableAddress(address);
                parcelableAddresses.add(parcelableAddress);
            }


            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(EXTRA_ADDRESSES, parcelableAddresses);
            activity.setResult(RESULT_CODE_RETURN_ALL_ADDRESSES, intent);
            activity.finish();
            return true;

        }
        catch (IOException e) {
            Log.e("GPXCONTROLLER", "errore import", e);
        }

        return false;

    }





}
