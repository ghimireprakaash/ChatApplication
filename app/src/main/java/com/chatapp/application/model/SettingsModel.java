package com.chatapp.application.model;

public class SettingsModel {
    private int icon;
    private String title;

    boolean isExpandable = false;


    //Empty constructor
    public SettingsModel() {
    }


    public SettingsModel(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }



    //..............................................................................................



    private String childTitle, childTitle1;
    private Integer viewType;

    public SettingsModel(String childTitle) {
        this.childTitle = childTitle;
    }

    public String getChildTitle() {
        return childTitle;
    }

    public void setChildTitle(String childTitle) {
        this.childTitle = childTitle;
    }


    public SettingsModel(String childTitle, String childTitle1) {
        this.childTitle = childTitle;
        this.childTitle1 = childTitle1;
    }

    public String getChildTitle1() {
        return childTitle1;
    }

    public void setChildTitle1(String childTitle1) {
        this.childTitle1 = childTitle1;
    }



    public Integer getViewType() {
        if (viewType == null){
            return 1;
        }
        return viewType;
    }

    public void setViewType(Integer viewType) {
        this.viewType = viewType;
    }
}
