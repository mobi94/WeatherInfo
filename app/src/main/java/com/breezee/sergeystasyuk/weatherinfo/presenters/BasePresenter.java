package com.breezee.sergeystasyuk.weatherinfo.presenters;


import android.location.Location;

/**
 * Created by User on 12.02.2017.
 */

public interface BasePresenter<T> {

    void getData(T data);
    void onDestroy();

}