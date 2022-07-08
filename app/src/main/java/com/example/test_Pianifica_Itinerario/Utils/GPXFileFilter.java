package com.example.test_Pianifica_Itinerario.Utils;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GPXFileFilter{// implements FileFilter {
/*
    private static final String TAG = "GPXFileFilter";


    private final boolean allowDirectories;

    public GPXFileFilter(boolean allowDirectories) {
        this.allowDirectories = allowDirectories;
    }

    public GPXFileFilter(){
        this(true);
    }

    @Override
    public boolean accept(File file) {
        if(file.isHidden() || !file.canRead()) return false;

        if(file.isDirectory()) return checkDirectory(file);

        return checkFileExtension(file);
    }


    private boolean checkDirectory(File directory) {
        if (!allowDirectories) return false;

        List<File> subDirectories = new ArrayList<File>();
        int fileQuantity = directory.listFiles( new FileFilter() {

                @Override
                public boolean accept(File file) {
                    if(file.isFile()) {
                        if (file.getName().equals(".nomedia")) return false;
                        return checkFileExtension(file);
                    }
                    if(file.isDirectory()){
                        subDirectories.add(file);
                        return false;
                    }
                    else return false;
                }
        } ).length;

        if (fileQuantity > 0) {
            Log.i(TAG, "checkDirectory: dir " + directory.toString() + " return true con songNumb -> " + fileQuantity );
            return true;
        }

        for(File subDirectory: subDirectories) {
            if (checkDirectory(subDirectory)) {
                Log.i(TAG, "checkDirectory [for]: subDir " + subDirectory.toString() + " return true" );
                return true;
            }
        }
        return false;
    }


    private boolean checkDirectory2(File directory) {
        if (!allowDirectories) return false;

        List<File> files = Arrays.asList(directory.listFiles(this));

        if(files.size() > 0) return true;

        return false;



        List<File> subDirectories = new ArrayList<File>();
        int fileQuantity = directory.listFiles( new FileFilter() {

            @Override
            public boolean accept(File file) {
                if(file.isFile()) {
                    if (file.getName().equals(".nomedia")) return false;
                    return checkFileExtension(file);
                }
                if(file.isDirectory()){
                    subDirectories.add(file);
                    return false;
                }
                else return false;
            }
        } ).length;

        if(fileQuantity > 0) return true;

        return false;
    }

    private boolean checkFileExtension(File file) {
        String fileExtension = extractFileExtension(file);
        if(fileExtension == null) return false;

        if(fileExtension.equalsIgnoreCase("gpx")) return true;


        try {
            if (SupportedFileFormat.valueOf(fileExtension.toUpperCase()) != null ) {
                return true;
            }
        } catch(IllegalArgumentException e) {
            //Not known enum value
            return false;
        }


        return false;
    }

    public String extractFileExtension( File file ) {
        String fileName = file.getName();

        int index = fileName.lastIndexOf('.');
        if (index > 0) return fileName.substring(index+1);
        else return null;
    }


*/

}
