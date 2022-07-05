package com.example.test_Pianifica_Itinerario.Utils;

import android.util.Log;

import com.example.test_Pianifica_Itinerario.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadLeg;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class RoadUtils {

    static final HashMap<String, Integer> MANEUVERS;
    static {
        MANEUVERS = new HashMap<>();
        MANEUVERS.put("new name", 2); //road name change
        MANEUVERS.put("turn-straight", 1); //Continue straight
        MANEUVERS.put("turn-slight right", 6); //Slight right
        MANEUVERS.put("turn-right", 7); //Right
        MANEUVERS.put("turn-sharp right", 8); //Sharp right
        MANEUVERS.put("turn-uturn", 12); //U-turn
        MANEUVERS.put("turn-sharp left", 5); //Sharp left
        MANEUVERS.put("turn-left", 4); //Left
        MANEUVERS.put("turn-slight left", 3); //Slight left
        MANEUVERS.put("depart", 24); //"Head" => used by OSRM as the start node. Considered here as a "waypoint".
        // TODO - to check...
        MANEUVERS.put("arrive", 24); //Arrived (at waypoint)
        MANEUVERS.put("roundabout-1", 27); //Round-about, 1st exit
        MANEUVERS.put("roundabout-2", 28); //2nd exit, etc ...
        MANEUVERS.put("roundabout-3", 29);
        MANEUVERS.put("roundabout-4", 30);
        MANEUVERS.put("roundabout-5", 31);
        MANEUVERS.put("roundabout-6", 32);
        MANEUVERS.put("roundabout-7", 33);
        MANEUVERS.put("roundabout-8", 34); //Round-about, 8th exit
        //TODO: other OSRM types to handle properly:
        MANEUVERS.put("merge-left", 20);
        MANEUVERS.put("merge-sharp left", 20);
        MANEUVERS.put("merge-slight left", 20);
        MANEUVERS.put("merge-right", 21);
        MANEUVERS.put("merge-sharp right", 21);
        MANEUVERS.put("merge-slight right", 21);
        MANEUVERS.put("merge-straight", 22);
        MANEUVERS.put("ramp-left", 17);
        MANEUVERS.put("ramp-sharp left", 17);
        MANEUVERS.put("ramp-slight left", 17);
        MANEUVERS.put("ramp-right", 18);
        MANEUVERS.put("ramp-sharp right", 18);
        MANEUVERS.put("ramp-slight right", 18);
        MANEUVERS.put("ramp-straight", 19);
        //MANEUVERS.put("fork", );
        //MANEUVERS.put("end of road", );
        //MANEUVERS.put("continue", );
    }

    //From: Project-OSRM-Web / WebContent / localization / OSRM.Locale.en.js
    // driving directions
    // %s: road name
    // %d: direction => removed
    // <*>: will only be printed when there actually is a road name
    static final HashMap<Integer, Object> DIRECTIONS;
    static {
        DIRECTIONS = new HashMap<>();
        DIRECTIONS.put(1, R.string.osmbonuspack_directions_1);
        DIRECTIONS.put(2, R.string.osmbonuspack_directions_2);
        DIRECTIONS.put(3, R.string.osmbonuspack_directions_3);
        DIRECTIONS.put(4, R.string.osmbonuspack_directions_4);
        DIRECTIONS.put(5, R.string.osmbonuspack_directions_5);
        DIRECTIONS.put(6, R.string.osmbonuspack_directions_6);
        DIRECTIONS.put(7, R.string.osmbonuspack_directions_7);
        DIRECTIONS.put(8, R.string.osmbonuspack_directions_8);
        DIRECTIONS.put(12, R.string.osmbonuspack_directions_12);
        DIRECTIONS.put(17, R.string.osmbonuspack_directions_17);
        DIRECTIONS.put(18, R.string.osmbonuspack_directions_18);
        DIRECTIONS.put(19, R.string.osmbonuspack_directions_19);
        //DIRECTIONS.put(20, R.string.osmbonuspack_directions_20);
        //DIRECTIONS.put(21, R.string.osmbonuspack_directions_21);
        //DIRECTIONS.put(22, R.string.osmbonuspack_directions_22);
        DIRECTIONS.put(24, R.string.osmbonuspack_directions_24);
        DIRECTIONS.put(27, R.string.osmbonuspack_directions_27);
        DIRECTIONS.put(28, R.string.osmbonuspack_directions_28);
        DIRECTIONS.put(29, R.string.osmbonuspack_directions_29);
        DIRECTIONS.put(30, R.string.osmbonuspack_directions_30);
        DIRECTIONS.put(31, R.string.osmbonuspack_directions_31);
        DIRECTIONS.put(32, R.string.osmbonuspack_directions_32);
        DIRECTIONS.put(33, R.string.osmbonuspack_directions_33);
        DIRECTIONS.put(34, R.string.osmbonuspack_directions_34);
    }


    protected static int getManeuverCode(String direction){
        Integer code = MANEUVERS.get(direction);
        if (code != null)
            return code;
        else
            return 0;
    }






    public static ArrayList<Road> buildRoads(JsonObject jsonObject){
        JsonArray jsonRoutes = jsonObject.getAsJsonArray("routes");

        ArrayList<Road> roads = new ArrayList<Road>();

        for (int i=0; i<jsonRoutes.size(); i++){
            Road road = new Road();
            roads.add(road);
            //roads.set(i,road);
            road.mStatus = Road.STATUS_OK;

            JsonObject jsonRoute = jsonRoutes.get(i).getAsJsonObject();

            String route_geometry = jsonRoute.get("geometry").getAsString();
            road.mRouteHigh = PolylineEncoder.decode(route_geometry, 10, false);
            road.mBoundingBox = BoundingBox.fromGeoPoints(road.mRouteHigh);
            road.mLength = jsonRoute.get("distance").getAsDouble() / 1000.0;
            road.mDuration = jsonRoute.get("duration").getAsDouble();
            //legs:
            JsonArray jLegs = jsonRoute.get("legs").getAsJsonArray();
            for (int l=0; l<jLegs.size(); l++) {
                //leg:
                JsonObject jLeg = jLegs.get(l).getAsJsonObject();
                RoadLeg leg = new RoadLeg();
                road.mLegs.add(leg);
                leg.mLength = jLeg.get("distance").getAsDouble();
                leg.mDuration = jLeg.get("duration").getAsDouble();
                //steps:
                JsonArray jsonSteps = jLeg.get("steps").getAsJsonArray();
                RoadNode lastNode = null;
                String lastRoadName = "";
                for (int s=0; s<jsonSteps.size(); s++) {
                    JsonObject jsonStep = jsonSteps.get(s).getAsJsonObject();
                    RoadNode node = new RoadNode();
                    node.mLength = jsonStep.get("distance").getAsDouble() / 1000.0;
                    node.mDuration = jsonStep.get("duration").getAsDouble();
                    JsonObject jsonStepManeuver = jsonStep.get("maneuver").getAsJsonObject();
                    JsonArray jsonLocation = jsonStepManeuver.get("location").getAsJsonArray();
                    node.mLocation = new GeoPoint(jsonLocation.get(1).getAsDouble(), jsonLocation.get(0).getAsDouble());
                    String direction = jsonStepManeuver.get("type").getAsString();
                    if (direction.equals("turn") || direction.equals("ramp") || direction.equals("merge")){
                        String modifier = jsonStepManeuver.get("modifier").getAsString();
                        direction = direction + '-' + modifier;
                    } else if (direction.equals("roundabout")){
                        int exit = jsonStepManeuver.get("exit").getAsInt();
                        direction = direction + '-' + exit;
                    } else if (direction.equals("rotary")) {
                        int exit = jsonStepManeuver.get("exit").getAsInt();
                        direction = "roundabout" + '-' + exit; //convert rotary in roundabout...
                    }
                    node.mManeuverType = getManeuverCode(direction);

                    String roadName = "";
                    if(!jsonStep.get("name").isJsonNull()) roadName = jsonStep.get("name").getAsString();

                    /*
                    node.mInstructions = buildInstructions(node.mManeuverType, roadName);
                    if (lastNode != null && node.mManeuverType == 2 && lastRoadName.equals(roadName)) {
                        //workaround for https://github.com/Project-OSRM/osrm-backend/issues/2273
                        //"new name", but identical to previous name:
                        //skip, but update values of last node:
                        lastNode.mDuration += node.mDuration;
                        lastNode.mLength += node.mLength;
                    } else {
                        road.mNodes.add(node);
                        lastNode = node;
                        lastRoadName = roadName;
                    }
                    */
                } //steps
            } //legs
        } //routes
        Log.d("FINISCHSNDF", "OSRMRoadManager.getRoads - finished");
        return roads;
    }
}
