package com.salesforce.tests.fs.path;

import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class Path {

    protected String relativePathName;
    protected String absolutePathName;
    protected Path parentPath;
    private IsFile containsSubPaths;
    private Map<String, Path> directoryContents = new HashMap<>();
    private int currentPathLevel;

    public Path() {
    }

    public Path(String relativePathName, IsFile containsSubPaths) {
        this.containsSubPaths = containsSubPaths;
        this.relativePathName = this.containsSubPaths.getPathNameFormat(relativePathName);
    }

    public String getRelativePathName() {
        return relativePathName;
    }

    public void setRelativePathName(String relativePathName) {
        this.relativePathName = relativePathName;
    }

    public IsFile getContainsSubPaths() {
        return containsSubPaths;
    }

    public void setContainsSubPaths(IsFile containsSubPaths) {
        this.containsSubPaths = containsSubPaths;
    }

    public Path getParentPath() {
        return parentPath;
    }

    public void setParentPath(Path parentPath) {
        this.parentPath = parentPath;
        this.setCurrentPathLevelBasedOnParent();
    }

    public void setAbsolutePathNameFromParent(){
        if(this.parentPath != null)
            this.absolutePathName = this.parentPath.getAbsolutePathName() + this.relativePathName;
        else
            this.absolutePathName = this.relativePathName;
    }

    public String getAbsolutePathName() {
        return absolutePathName;
    }


    public Map<String, Path> getDirectoryContents() {
        return directoryContents;
    }

    public void setDirectoryContents(Map<String, Path> directoryContents) {
        this.directoryContents = directoryContents;
    }

    public boolean canContainSubPaths(){
        return !this.containsSubPaths.isAFile();
    }

    public boolean containsSubPath(String pathName){
        return this.directoryContents.get(pathName) != null;
    }

    public Path getSubPathByName(String pathName){
        return this.getDirectoryContents().get(pathName);
    }

    public String getParentPathName(){
        return this.parentPath.getRelativePathName();
    }

    public void addSubDirectory(String pathName, Path path){
        this.directoryContents.put(pathName, path);
    }

    public boolean isRootPath(){
        return this.parentPath == null;
    }

    public boolean hasParent() {
        return this.parentPath != null;
    }

    public boolean containsSubPaths(){
        return MapUtils.isNotEmpty(this.directoryContents);
    }

    public Integer getCurrentPathLevel() {
        return currentPathLevel;
    }

    public void setCurrentPathLevel(Integer currentPathLevel) {
        this.currentPathLevel = currentPathLevel;
    }

    private void setCurrentPathLevelBasedOnParent(){
        if(this.parentPath == null)
            this.currentPathLevel = 0;
        else
            this.currentPathLevel = this.parentPath.currentPathLevel + 1;
    }
}
