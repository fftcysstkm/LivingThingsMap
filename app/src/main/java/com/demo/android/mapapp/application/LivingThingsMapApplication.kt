package com.demo.android.mapapp.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * アプリケーションでHiltによるDIを可能にする
 */
@HiltAndroidApp
class LivingThingsMapApplication : Application() {
}