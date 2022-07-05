package com.example.test_Pianifica_Itinerario.Models;

import android.location.Address;

import com.example.test_Pianifica_Itinerario.ObserverPattern.Observable;
import com.example.test_Pianifica_Itinerario.ObserverPattern.Observer;

import java.util.ArrayList;

public class RicercaPuntoModel implements Observable {

    private ArrayList<Address> resultPoints;

    private ArrayList<Observer> observers;


    public RicercaPuntoModel(){
        this.resultPoints = new ArrayList<Address>();

        this.observers = new ArrayList<Observer>();
    }



    public ArrayList<Address> getResultPoints() {
        return resultPoints;
    }

    public void setResultPoints(ArrayList<Address> resultPoints) {
        this.resultPoints.clear();
        this.resultPoints.addAll(resultPoints);

        //this.resultPoints = resultPoints;
        notifyObservers();
    }

    public void clear(){
        this.resultPoints.clear();
        notifyObservers();
    }

    public Boolean hasResultPoints(){
        return !resultPoints.isEmpty();
    }



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
