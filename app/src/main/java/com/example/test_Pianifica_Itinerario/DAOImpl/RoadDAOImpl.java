package com.example.test_Pianifica_Itinerario.DAOImpl;

import android.location.Address;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.test_Pianifica_Itinerario.DAOPattern.RoadDAO;
import com.example.test_Pianifica_Itinerario.Utils.AddressUtils;
import com.example.test_Pianifica_Itinerario.Utils.RoadUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import java9.util.concurrent.CompletableFuture;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RoadDAOImpl implements RoadDAO {

    static final String ROUTING_SERVICE_URL = "https://routing.openstreetmap.de/";
    public static final String MEAN_BY_FOOT = "routed-foot/route/v1/driving/";

    protected ArrayList<Road> defaultRoad(ArrayList<Address> addresses){
        ArrayList<Road> roads = new ArrayList<Road>(1);

        ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();

        for(Address address: addresses){
            GeoPoint geoPoint = new GeoPoint(address.getLatitude(),address.getLongitude());
            geoPoints.add(geoPoint);
        }

        roads.set(1, new Road(geoPoints));
        return roads;
    }

    @Override
    public Road findRoadByAddresses(Address startAddress, Address destinationAddress) {
        return null;
    }



    @Override
    public ArrayList<Road> findRoadsByAddresses(ArrayList<Address> addresses) {
        StringBuilder urlString = new StringBuilder(ROUTING_SERVICE_URL + MEAN_BY_FOOT);

        for (int i=0; i<addresses.size(); i++){
            Address address = addresses.get(i);
            if (i>0)
                urlString.append(';');
            urlString.append(address.getLongitude() + ",");
            urlString.append(address.getLatitude());
        }

        //urlString.append("?format=json");
        urlString.append("?overview=full&steps=true");

        String url = urlString.toString();

        Log.i("URL: ", url);


        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();

        Call call = client.newCall(request);

        CompletableFuture<ArrayList<Road>> completableFuture = new CompletableFuture<ArrayList<Road>>();

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ERRORE: ", e.getMessage());
                //completableFuture.complete(defaultRoad(addresses));
                completableFuture.complete(null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()){
                    Log.e("ERRORE: ","Risposta chiamata api fallita");
                    completableFuture.complete(null);
                    return;
                }


                String jsonStringResult = response.body().string();
                Log.i("JSON: ", jsonStringResult);

                JsonElement jsonElementResult = JsonParser.parseString(jsonStringResult);
                JsonObject jsonObjectResult = jsonElementResult.getAsJsonObject();

                Log.i("GOOD:", "TUTTO BENE");

                ArrayList<Road> roads = RoadUtils.buildRoads(jsonObjectResult);

                //Address address = AddressUtils.buildAddress(jsonObjectResult);

                completableFuture.complete(roads);
            }
        });


        ArrayList<Road> result = null;

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
}
