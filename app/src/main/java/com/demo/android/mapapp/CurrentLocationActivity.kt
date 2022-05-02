package com.demo.android.mapapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.demo.android.mapapp.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.demo.android.mapapp.PermissionUtils.isPermissionGranted
import com.demo.android.mapapp.databinding.ActivityCurrentLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

/**
 * 現在地をマップに点で表示する。
 */
class CurrentLocationActivity : AppCompatActivity(),
    GoogleMap.OnMyLocationButtonClickListener,
    OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private var permissionDenied = false
    private var map: GoogleMap? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var lastKnownLocation: Location? = null

    private lateinit var binding: ActivityCurrentLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 現在地取得用のFusedLocationProviderClient取得
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // マップフラグメント取得
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
//    }

    // 現在地表示ボタン押下時処理（特に何もせず現在位置に移動）
    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    /**
     * マップが利用可能になったら呼び出される(OnMapReadyCallbackが持つメソッド)。
     * 引数は準備できたマップが渡ってくる。権限リクエストや現在位置取得・カメラ移動を行う。
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.setOnMyLocationButtonClickListener(this)
        map?.uiSettings?.isZoomControlsEnabled = true
        // 権限リクエストおよび現在位置有効化
        enableMyLocation()
        // 現在地取得して地図移動
        getDeviceLocation()
    }

    /**
     * 現在地を有効にする（必要な場合は権限リクエストを行う）
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        // 許可確認開始
        // 1.権限が付与されているかどうかを確認。既に付与されていれば現在地を有効にする
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map?.isMyLocationEnabled = true
            return
        }

        // 2. ユーザーに位置情報を説明する必要が有る場合、権限リクエストダイアログを表示。
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        // 3. それ以外の場合、権限をリクエストする
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * パーミッションリクエスト許可ダイアログの許可または拒否が選択された時の処理
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // 権限許可ダイアログでOKが押された場合
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        // ???なにこれ。
        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            || isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            enableMyLocation()
        } else {
            // 位置情報許可リクエストが拒否された場合。エラーメッセージを表示する
            permissionDenied = true
        }
    }

    /*
     * デバイスの最新の位置情報を取得し、マップのカメラを移動
     */
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (!permissionDenied) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        // todo 位置情報が取得できなかった場合　↓後で参考
                        // https://zenn.dev/yass97/articles/e99dccecdd4b80
//                        map?.moveCamera(
//                            CameraUpdateFactory
//                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
//                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        // 位置情報取得リクエストのリクエストコード
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        // デフォルトの倍率
        private const val DEFAULT_ZOOM = 15
        private val TAG = CurrentLocationActivity::class.java.simpleName

    }
}