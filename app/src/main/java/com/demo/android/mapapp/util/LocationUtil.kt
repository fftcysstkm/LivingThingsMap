package com.demo.android.mapapp.util

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * FusedLocationProviderClientに追加する拡張関数
 * コルーチンで最後に取得した位置情報を返す
 * 参考↓
 * https://developer.android.com/codelabs/building-kotlin-extensions-library#4
 */
@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.awaitLastLocation(): Location? =
    suspendCancellableCoroutine<Location> { continuation ->
        lastLocation.addOnSuccessListener { location ->
            // 成功時に位置情報を返却
            continuation.resume(location)
        }.addOnFailureListener { e ->
            // 失敗時に呼び出し元に例外を返す
            continuation.resumeWithException(e)
        }
    }
