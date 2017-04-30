package com.example.musiqueue.HelperClasses;

public class HubsListItem {
    private String hub_name;
    private String hub_creator_name;
    private Boolean hub_pin_required;

    public String getHub_creator_name() {
        return hub_creator_name;
    }

    public void setHub_creator_name(String hub_creator_name) {
        this.hub_creator_name = hub_creator_name;
    }

    public Boolean getHub_pin_required() {
        return hub_pin_required;
    }

    public void setHub_pin_required(Boolean hub_pin_required) {
        this.hub_pin_required = hub_pin_required;
    }

    public String getHub_name() {
        return hub_name;
    }

    public void setHub_name(String hub_name) {
        this.hub_name = hub_name;
    }
}
