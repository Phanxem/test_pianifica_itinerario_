package com.example.test_Pianifica_Itinerario;

import android.location.Address;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.utils.BonusPackHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeocoderTest extends GeocoderNominatim {


    public GeocoderTest(Locale locale, String userAgent) {
        super(locale, userAgent);
    }

    public GeocoderTest(String userAgent) {
        super(userAgent);
    }

    @Override
    public List<Address> getFromLocation(double latitude, double longitude, int maxResults)
            throws IOException {
        String url = mServiceUrl + "reverse?";
        if (mKey != null)
            url += "key=" + mKey + "&";
        url += "format=json"
                + "&accept-language=" + mLocale.getLanguage()
                //+ "&addressdetails=1"
                + "&lat=" + latitude
                + "&lon=" + longitude;
        Log.i(BonusPackHelper.LOG_TAG+"-c", "GeocoderNominatim::getFromLocation:"+url);
        String result = BonusPackHelper.requestStringFromUrl(url, mUserAgent);
        //String result = null;


        Log.i("£er", "result");
        if (result == null) {
            Log.i("£er", "null");
            throw new IOException();
        }
        else{
            Log.i("£er", "not null");

        try {
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(result);
            JsonObject jResult = json.getAsJsonObject();
            Address gAddress = buildAndroidAddress(jResult);
            List<Address> list = new ArrayList<Address>(1);
            if (gAddress != null)
                list.add(gAddress);
            return list;
        } catch (JsonSyntaxException e) {
            throw new IOException();
        }
        }
    }
}
