package com.example.test_Pianifica_Itinerario.Models;

import android.location.Address;

import com.example.test_Pianifica_Itinerario.Controllers.PianificaItinerarioController;
import com.example.test_Pianifica_Itinerario.ObserverPattern.Observable;
import com.example.test_Pianifica_Itinerario.ObserverPattern.Observer;
import com.example.test_Pianifica_Itinerario.Utils.AddressUtils;


import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PianificaItinerarioModel implements Observable {

    private Address startingPoint;
    private Address destinationPoint;
    private List<Address> intermediatePoints;

    private Address addressPointedOnMap;
    private Integer indexPointSelected;

    //TODO
    private List<Road> roads;

    private List<Observer> observers;


    public PianificaItinerarioModel(){
        this.startingPoint = null;
        this.destinationPoint = null;
        this.intermediatePoints = new ArrayList<Address>();

        this.addressPointedOnMap = null;
        this.indexPointSelected = null;

        this.roads = new ArrayList<Road>();

        this.observers = new ArrayList<Observer>();
    }



    public Address getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(Address startingPoint) {
        this.startingPoint = startingPoint;
        notifyObservers();
    }

    public void removeStartingPoint(){
        this.startingPoint = null;
        notifyObservers();
    }

    public String getStartingPointName() {
        return AddressUtils.getAddressName(this.startingPoint);
    }

    public GeoPoint getStaratingGeoPoint(){
        return new GeoPoint(startingPoint.getLatitude(), startingPoint.getLongitude());
    }


    public Address getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(Address destinationPoint) {
        this.destinationPoint = destinationPoint;
        notifyObservers();
    }

    public void removeDestinationPoint(){
        this.destinationPoint = null;
        notifyObservers();
    }

    public String getDestinationPointName(){
        return AddressUtils.getAddressName(this.destinationPoint);
    }

    public GeoPoint getDestinationGeoPoint(){
        return new GeoPoint(destinationPoint.getLatitude(),destinationPoint.getLongitude());
    }



    public List<Address> getIntermediatePoints() {
        return intermediatePoints;
    }

    public void setIntermediatePoints(List<Address> intermediatePoints) {
        this.intermediatePoints.clear();
        this.intermediatePoints.addAll(intermediatePoints);
    }

    public int getIntermediatePointsQuantity(){
        return this.intermediatePoints.size();
    }

    public Address getIntermediatePoint(int index){
        if(index < 0 || index > intermediatePoints.size()){
            //TODO ERROR
            return null;
        }
        return intermediatePoints.get(index);
    }

    public void setIntermediatePoint(int index, Address interestPoint){
        if(index < 0 || index > intermediatePoints.size()){
            //TODO ERROR
            return;
        }
        this.intermediatePoints.set(index, interestPoint);
        notifyObservers();
    }

    public void removeIntermediatePoint(int index){
        if(index < 0 || index > intermediatePoints.size()){
            //TODO ERROR
            return;
        }

        this.intermediatePoints.remove(index);
        notifyObservers();
    }

    public void addIntermediatePoint(Address interestPoint){
        this.intermediatePoints.add(interestPoint);
        notifyObservers();
    }


    public void updateInterestPoints(List<Address> addresses){

        if(addresses == null || addresses.isEmpty()) return;

        this.startingPoint = addresses.get(0);
        this.destinationPoint = addresses.get(addresses.size()-1);

        this.intermediatePoints.clear();
        for(Address address: addresses.subList(1,addresses.size()-1)){
            this.intermediatePoints.add(address);
        }

        this.indexPointSelected = null;
        this.addressPointedOnMap = null;


        notifyObservers();
    }


    public Address getAddressPointedOnMap() {
        return addressPointedOnMap;
    }

    public void setAddressPointedOnMap(Address addressPointedOnMap) {
        this.addressPointedOnMap = addressPointedOnMap;
        notifyObservers();
    }

    public void removeAddressPointedOnMap() {
        this.addressPointedOnMap = null;
        notifyObservers();
    }


    public Integer getIndexPointSelected() {
        return indexPointSelected;
    }

    public void setIndexPointSelected(Integer indexPointSelected) {
        this.indexPointSelected = indexPointSelected;
        //notifyObservers();
    }

    public void removeIndexPointSelected(){
        this.indexPointSelected = null;
        notifyObservers();
    }


    public List<Road> getRoads(){
        return this.roads;

    }

    public Road getRoad(int index){
        return this.roads.get(index);
    }

    public void setRoads(List<Road> roads){
        this.roads.clear();
        this.roads.addAll(roads);
        notifyObservers();
    }

    public void clearRoads(){
        this.roads.clear();
        notifyObservers();
    }




    public boolean isValidPointIndex(Integer index){
        if (index == null) return false;
        if (index == PianificaItinerarioController.STARTING_POINT_CODE || index == PianificaItinerarioController.DESTINATION_POINT_CODE) return true;
        if (index >= 0 && index <= intermediatePoints.size()) return true;

        return false;
    }



    public ArrayList<Address> getAllInterestPoint(){
        ArrayList<Address> addresses = new ArrayList<Address>();

        if (startingPoint != null) addresses.add(startingPoint);
        if (!intermediatePoints.isEmpty()) addresses.addAll(intermediatePoints);
        if (destinationPoint != null) addresses.add(destinationPoint);

        return addresses;

    }

    //VIEW function
    public boolean hasStartingPoint(){
        return this.startingPoint != null;
    }

    public boolean hasDestinationPoint(){
        return this.destinationPoint != null;
    }

    public boolean hasIntermediatePoint(){
        return !this.intermediatePoints.isEmpty();
    }


    //---------------------------------------------------------

    @Override
    public void registerObserver(Observer observer) {
        if(!observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void undergisterObserver(Observer observer) {
        if(observers.contains(observer)) observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer observer : observers){
            observer.update();
        }
    }


}
