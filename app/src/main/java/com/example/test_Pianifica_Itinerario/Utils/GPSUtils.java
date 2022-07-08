package com.example.test_Pianifica_Itinerario.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class GPSUtils {

    public static boolean hasGPSFeature(Context context){
        PackageManager packageManager = context.getPackageManager();
        return (packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS));
    }

    public static boolean isGPSEnabled(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean haveGPSPermissionGranted(Context context){
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
