package com.salesforce.tests.fs.utils;

public class TabBuilder {

    private String tabString = "";

    public String getTabString() {
        return tabString;
    }

    public void setTabString(String tabString) {
        this.tabString = tabString;
    }

    public void addTabToString(){
        this.tabString += "\t";
    }
}
