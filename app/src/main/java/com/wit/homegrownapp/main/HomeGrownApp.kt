package com.wit.homegrownapp.main

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wit.homegrownapp.R
import timber.log.Timber

class HomeGrownApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("Starting HomeGrown Applicationn")
    }
}