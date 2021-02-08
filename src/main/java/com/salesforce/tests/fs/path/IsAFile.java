package com.salesforce.tests.fs.path;

public class IsAFile implements IsFile {
    @Override
    public boolean isAFile() {
        return true;
    }

    @Override
    public String getPathNameFormat(String pathName) {
        return pathName.replace("/", "");
    }
}
