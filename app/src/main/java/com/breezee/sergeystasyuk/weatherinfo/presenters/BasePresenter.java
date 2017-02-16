package com.breezee.sergeystasyuk.weatherinfo.presenters;

import android.os.Bundle;

import com.breezee.sergeystasyuk.weatherinfo.views.AccuweatherAPIView;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by User on 13.02.2017.
 */

public abstract class BasePresenter <M, P> {
    M model;
    AccuweatherAPIView<P> view;
    Subscription subscription;

    public BasePresenter(AccuweatherAPIView<P> view) {
        this.view = view;
        this.subscription = Subscriptions.empty();
    }

    public void unsubscribeSubscription(){
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public abstract void onDestroy();
}
