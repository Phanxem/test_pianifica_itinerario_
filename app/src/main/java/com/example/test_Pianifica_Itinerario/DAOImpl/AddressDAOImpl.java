package com.example.test_Pianifica_Itinerario.DAOImpl;

import android.location.Address;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.test_Pianifica_Itinerario.DAOPattern.AddressDAO;
import com.example.test_Pianifica_Itinerario.Utils.AddressUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


import java9.util.concurrent.CompletableFuture;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddressDAOImpl implements AddressDAO {

    public static final String NOMINATIM_SERVICE_URL = "https://nominatim.openstreetmap.org/";

    public static final String OPERATON_REVERSE = "reverse";





    @Override
    public Address findInterestPointByGeoPoint(GeoPoint geoPoint){

        String url = NOMINATIM_SERVICE_URL + OPERATON_REVERSE
                + "?format=json"
                + "&accept-language=" + Locale.getDefault().getLanguage()
                + "&lat=" + geoPoint.getLatitude()
                + "&lon=" + geoPoint.getLongitude();

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();

        Call call = client.newCall(request);

        CompletableFuture<Address> completableFuture = new CompletableFuture<Address>();

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ERRORE: ", e.getMessage());
                completableFuture.complete(null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()){
                    Log.e("ERRORE: ","Risposta chiamata api fallita");
                    return;
                }


                String jsonStringResult = response.body().string();
                Log.i("JSON: ", jsonStringResult);

                JsonElement jsonElementResult = JsonParser.parseString(jsonStringResult);
                JsonObject jsonObjectResult = jsonElementResult.getAsJsonObject();

                Address address = AddressUtils.buildAddress(jsonObjectResult);

                completableFuture.complete(address);
            }
        });


        Address result = null;

        try {
            result = completableFuture.get();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        finally {
            return result;
        }
    }





    @Override
    public ArrayList<Address> findInterestPointsByString(String searchString) {
        ArrayList<Address> addresses = new ArrayList<Address>();

        Address address1 = new Address(Locale.getDefault());
        address1.setAddressLine(0,"Address1");

        Address address2 = new Address(Locale.getDefault());
        address2.setAddressLine(0,"Address2");

        Address address3 = new Address(Locale.getDefault());
        address3.setAddressLine(0,"Address3");

        addresses.add(address1);
        addresses.add(address2);
        addresses.add(address3);
        addresses.add(address1);
        addresses.add(address2);
        addresses.add(address3);
        addresses.add(address1);
        addresses.add(address2);
        addresses.add(address3);
        addresses.add(address1);
        addresses.add(address2);
        addresses.add(address3);
        addresses.add(address1);
        addresses.add(address2);
        addresses.add(address3);

        return addresses;
    }




    //Utils




}
