
package com.breezee.sergeystasyuk.weatherinfo.pojos.dailyforecast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Imperial______________________ {

    @SerializedName("Value")
    @Expose
    private Float value;
    @SerializedName("Unit")
    @Expose
    private String unit;
    @SerializedName("UnitType")
    @Expose
    private Integer unitType;

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getUnitType() {
        return unitType;
    }

    public void setUnitType(Integer unitType) {
        this.unitType = unitType;
    }

}
