package com.salesforce.tests.fs.path;

public class IsNotFile implements IsFile {

    @Override
    public boolean isAFile() {
        return false;
    }

    @Override
    public String getPathNameFormat(String pathName) {
        return "/" + pathName;
    }
}
