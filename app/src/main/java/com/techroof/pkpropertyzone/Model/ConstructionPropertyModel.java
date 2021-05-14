package com.techroof.pkpropertyzone.Model;

public class ConstructionPropertyModel {

    private String propertyId,propertyDecription,imageUrl;

    public ConstructionPropertyModel() {

    }

    public ConstructionPropertyModel(String propertyId, String propertyDecription, String imageUrl) {
        this.propertyId = propertyId;
        this.propertyDecription = propertyDecription;
        this.imageUrl = imageUrl;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyDecription() {
        return propertyDecription;
    }

    public void setPropertyDecription(String propertyDecription) {
        this.propertyDecription = propertyDecription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

