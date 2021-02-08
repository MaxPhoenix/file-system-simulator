package com.salesforce.tests.fs.path;

import java.util.HashMap;
import java.util.Map;

public class PathCache {

    private Map<String, Path> pathsAdded = new HashMap<>();

    public boolean containsPath(String pathName){
        return this.pathsAdded.get(pathName) != null;
    }

    public Map<String, Path> getPathsAdded() {
        return pathsAdded;
    }

    public void setPathsAdded(Map<String, Path> pathsAdded) {
        this.pathsAdded = pathsAdded;
    }

    public void addPath(String pathName, Path path){
        if(this.pathsAdded == null)
            this.pathsAdded = new HashMap<>();
        this.pathsAdded.putIfAbsent(pathName, path);
    }
}
