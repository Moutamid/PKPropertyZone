package com.techroof.pkpropertyzone.Model;

public class Property {

    String location, imageUrl, fullAddress, propertyDescription, propertyId, propertyName, propertyPrice,
            propertyPurpose, propertyType, seller, videoUrl, sellerPhone;

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public Property(String location, String imageUrl, String fullAddress, String propertyDescription, String propertyId, String propertyName, String propertyPrice, String propertyPurpose, String propertyType, String seller, String videoUrl, String sellerPhone) {
        this.location = location;
        this.imageUrl = imageUrl;
        this.fullAddress = fullAddress;
        this.propertyDescription = propertyDescription;
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.propertyPrice = propertyPrice;
        this.propertyPurpose = propertyPurpose;
        this.propertyType = propertyType;
        this.seller = seller;
        this.videoUrl = videoUrl;
        this.sellerPhone = sellerPhone;
    }

    public Property() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription = propertyDescription;
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

    public String getPropertyPrice() {
        return propertyPrice;
    }

    public void setPropertyPrice(String propertyPrice) {
        this.propertyPrice = propertyPrice;
    }

    public String getPropertyPurpose() {
        return propertyPurpose;
    }

    public void setPropertyPurpose(String propertyPurpose) {
        this.propertyPurpose = propertyPurpose;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
