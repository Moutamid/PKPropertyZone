package com.techroof.pkpropertyzone.Model;

public class AddPropertyModel {

    private String propertyId,propertyName,propertyDescription,propertyPrice,imageUrl;

    public AddPropertyModel() {

    }

    public AddPropertyModel(String propertyId, String propertyName, String propertyDescription, String propertyPrice, String imageUrl) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.propertyDescription = propertyDescription;
        this.propertyPrice = propertyPrice;
        this.imageUrl = imageUrl;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription = propertyDescription;
    }

    public String getPropertyPrice() {
        return propertyPrice;
    }

    public void setPropertyPrice(String propertyPrice) {
        this.propertyPrice = propertyPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
