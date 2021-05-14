package com.techroof.pkpropertyzone.Model;

public class Areas {
    private String cityId,cityName,areaName;

    public Areas() {

    }

    public Areas(String cityId, String cityName, String areaName) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.areaName = areaName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
