
package com.breezee.sergeystasyuk.weatherinfo.pojos.dailyforecast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Direction__ {

    @SerializedName("Degrees")
    @Expose
    private Float degrees;
    @SerializedName("Localized")
    @Expose
    private String localized;
    @SerializedName("English")
    @Expose
    private String english;

    public Float getDegrees() {
        return degrees;
    }

    public void setDegrees(Float degrees) {
        this.degrees = degrees;
    }

    public String getLocalized() {
        return localized;
    }

    public void setLocalized(String localized) {
        this.localized = localized;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

}
