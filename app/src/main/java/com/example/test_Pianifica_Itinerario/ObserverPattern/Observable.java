package com.example.test_Pianifica_Itinerario.ObserverPattern;

public interface Observable {

    public void registerObserver(Observer observer);
    public void undergisterObserver(Observer observer);
    public void notifyObservers();

}
