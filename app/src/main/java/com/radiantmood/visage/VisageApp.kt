package com.radiantmood.visage

import android.app.Application

lateinit var app: VisageApp

class VisageApp : Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
    }
}