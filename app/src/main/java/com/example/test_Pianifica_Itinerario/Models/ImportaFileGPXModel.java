package com.example.test_Pianifica_Itinerario.Models;

import android.os.Environment;

import com.example.test_Pianifica_Itinerario.ObserverPattern.Observable;
import com.example.test_Pianifica_Itinerario.ObserverPattern.Observer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImportaFileGPXModel implements Observable {

    private File currentDirectory;
    private List<File> files;

    private List<Observer> observers;

    public ImportaFileGPXModel(){
        this.files = new ArrayList<File>();
        this.observers = new ArrayList<Observer>();
    }


    public void set(File currentDirectory, List<File> files){
        this.currentDirectory = currentDirectory;
        this.files.clear();
        this.files.addAll(files);

        notifyObservers();
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public boolean hasParentDirectory(){
        if(currentDirectory == null) return false;
        if(currentDirectory.equals(Environment.getExternalStorageDirectory())) return false;
        if(currentDirectory.getParentFile() == null || !currentDirectory.getParentFile().isDirectory()) return false;
        return true;
    }

    public File getParentDirectory(){
        if(currentDirectory == null) return null;
        return currentDirectory.getParentFile();
    }

/*
    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
*/
    public List<File> getFiles() {
        return files;
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
    /*
    public void setFiles(List<File> files) {
        this.files = files;
    }

    public File getFile(int index){
        return files.get(index);
    }
    public void setFile(int index, File file){
        this.files.set(index, file);
    }
    public void clearFiles(){
        this.files.clear();
    }
    public void addFile(File file){
        this.files.add(file);
    }
    public void addFiles(Collection<? extends File> files){
        this.files.addAll(files);
    }
    public void removeFile(int index){
        this.files.remove(index);
    }

     */
}
