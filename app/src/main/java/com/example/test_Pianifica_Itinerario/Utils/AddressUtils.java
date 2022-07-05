package com.example.test_Pianifica_Itinerario.Utils;

import android.location.Address;
import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Locale;

public class AddressUtils {

    public final static int RESULT_OK = -1;
    public final static int RESULT_CANCEL = 0;
    public final static int RESULT_GET_FROM_MAP = 1;
    public final static int RESULT_CURRENT_LOCATION = 2;

    public static String getAddressName(Address address) {
        if (address == null) return null;

        StringBuilder stringBuilder = new StringBuilder();
        int n = address.getMaxAddressLineIndex();
        for (int i = 0; i <= n; i++) {
            if (i != 0) stringBuilder.append(", ");
            stringBuilder.append(address.getAddressLine(i));
        }

        return stringBuilder.toString();
    }

    public static Address buildAddress(JsonObject jsonObject){
        Address address = new Address(Locale.getDefault());
        if (!jsonObject.has("lat") || !jsonObject.has("lon") || !jsonObject.has("address"))
            return null;


        address.setLatitude(jsonObject.get("lat").getAsDouble());
        address.setLongitude(jsonObject.get("lon").getAsDouble());

        JsonObject jsonAddress = jsonObject.get("address").getAsJsonObject();

        int addressIndex = 0;
        if (jsonAddress.has("road")){
            address.setAddressLine(addressIndex++, jsonAddress.get("road").getAsString());
            address.setThoroughfare(jsonAddress.get("road").getAsString());
        }

        if (jsonAddress.has("house_number")){
            address.setSubThoroughfare(jsonAddress.get("house_number").getAsString());
        }

        if (jsonAddress.has("suburb")){
            //address.setAddressLine(addressIndex++, jsonAddress.getString("suburb"));
            //not kept => often introduce "noise" in the address.
            address.setSubLocality(jsonAddress.get("suburb").getAsString());
        }

        if (jsonAddress.has("postcode")){
            address.setAddressLine(addressIndex++, jsonAddress.get("postcode").getAsString());
            address.setPostalCode(jsonAddress.get("postcode").getAsString());
        }

        if (jsonAddress.has("city")){
            address.setAddressLine(addressIndex++, jsonAddress.get("city").getAsString());
            address.setLocality(jsonAddress.get("city").getAsString());
        } else if (jsonAddress.has("town")){
            address.setAddressLine(addressIndex++, jsonAddress.get("town").getAsString());
            address.setLocality(jsonAddress.get("town").getAsString());
        } else if (jsonAddress.has("village")){
            address.setAddressLine(addressIndex++, jsonAddress.get("village").getAsString());
            address.setLocality(jsonAddress.get("village").getAsString());
        }

        if (jsonAddress.has("county")){ //France: departement
            address.setSubAdminArea(jsonAddress.get("county").getAsString());
        }
        if (jsonAddress.has("state")){ //France: region
            address.setAdminArea(jsonAddress.get("state").getAsString());
        }
        if (jsonAddress.has("country")){
            address.setAddressLine(addressIndex++, jsonAddress.get("country").getAsString());
            address.setCountryName(jsonAddress.get("country").getAsString());
        }
        if (jsonAddress.has("country_code"))
            address.setCountryCode(jsonAddress.get("country_code").getAsString());

        /* Other possible OSM tags in Nominatim results not handled yet:
         * subway, golf_course, bus_stop, parking,...
         * house, house_number, building
         * city_district (13e Arrondissement)
         * road => or highway, ...
         * sub-city (like suburb) => locality, isolated_dwelling, hamlet ...
         * state_district
         */

        //Add non-standard (but very useful) information in Extras bundle:
        Bundle extras = new Bundle();
        if (jsonObject.has("polygonpoints")){
            JsonArray jPolygonPoints = jsonObject.get("polygonpoints").getAsJsonArray();
            ArrayList<GeoPoint> polygonPoints = new ArrayList<GeoPoint>(jPolygonPoints.size());
            for (int i=0; i<jPolygonPoints.size(); i++){
                JsonArray jCoords = jPolygonPoints.get(i).getAsJsonArray();
                double lon = jCoords.get(0).getAsDouble();
                double lat = jCoords.get(1).getAsDouble();
                GeoPoint p = new GeoPoint(lat, lon);
                polygonPoints.add(p);
            }
            extras.putParcelableArrayList("polygonpoints", polygonPoints);
        }
        if (jsonObject.has("boundingbox")){
            JsonArray jBoundingBox = jsonObject.get("boundingbox").getAsJsonArray();
            BoundingBox bb = new BoundingBox(
                    jBoundingBox.get(1).getAsDouble(), jBoundingBox.get(2).getAsDouble(),
                    jBoundingBox.get(0).getAsDouble(), jBoundingBox.get(3).getAsDouble());
            extras.putParcelable("boundingbox", bb);
        }
        if (jsonObject.has("osm_id")){
            long osm_id = jsonObject.get("osm_id").getAsLong();
            extras.putLong("osm_id", osm_id);
        }
        if (jsonObject.has("osm_type")){
            String osm_type = jsonObject.get("osm_type").getAsString();
            extras.putString("osm_type", osm_type);
        }
        if (jsonObject.has("display_name")){
            String display_name = jsonObject.get("display_name").getAsString();
            extras.putString("display_name", display_name);
        }
        address.setExtras(extras);

        return address;
    }
}
