package com.techroof.pkpropertyzone;

import android.app.Application;

import com.fxn.stash.Stash;

public class ApplicationContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stash.init(this);
    }
}
