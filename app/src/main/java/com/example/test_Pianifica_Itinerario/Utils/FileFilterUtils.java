package com.example.test_Pianifica_Itinerario.Utils;

import java.io.File;
import java.io.FileFilter;

public class FileFilterUtils {

    public static boolean hasExtension(File file, String extension) {
        String fileExtension = getExtension(file);
        if(fileExtension == null) return false;

        if(fileExtension.equalsIgnoreCase(extension)) return true;

        return false;
    }

    public static String getExtension(File file) {
        String fileName = file.getName();

        int index = fileName.lastIndexOf('.');
        if (index > 0) return fileName.substring(index+1);
        else return null;
    }

    public static FileFilter extensionFileFilter(String extension, String... nextExtensions){
        FileFilter eFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.isHidden() || !file.canRead()) return false;

                if (file.isDirectory()) return true;

                if(hasExtension(file, extension)) return true;

                if(nextExtensions.length > 0){
                    for(String nextExtension : nextExtensions) {
                        if (hasExtension(file, nextExtension)) return true;
                    }
                }
                return false;
            }
        };

        return eFileFilter;
    }









}
