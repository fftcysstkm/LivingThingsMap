package com.demo.android.mapapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.demo.android.mapapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var setClient: SettingsClient
    private lateinit var locCallback: LocationCallback
    private lateinit var locReq: LocationRequest
    private lateinit var locSetReq: LocationSettingsRequest

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Client準備
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setClient = LocationServices.getSettingsClient(this)

        // パーミッションの確認、要求
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

        /**
         * マップを取得。得られたマップオブジェクトは引数のコールバックオブジェクトに渡させる。
         * コールバックオブジェクトはOnMapReadyCallbackの実装（= this）
         * なお、getMapAsyncは非同期。
         */
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.apply {
            btnSatellite.setOnClickListener {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            }
            btnNormal.setOnClickListener {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }

        // 位置情報を取得したときに実行すべき処理
        // ここでは、位置情報を持つオブジェクトを生成し、地図位置を移動
        locCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val loc = locationResult.lastLocation
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(loc.latitude, loc.longitude), 16f
                    )
                )
            }
        }

        // 位置リクエストを作成
        locReq = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // 位置情報に関する設定リクエスト情報を生成
        val builder = LocationSettingsRequest.Builder()
        locSetReq = builder.addLocationRequest(locReq).build()

        // 位置情報の監視
        startWatchLocation()

    }

    /**
     * 位置情報の監視（位置情報のパーミッションが適切な場合に限る）
     * (AndroidManifestにパーミッション許可したが警告でるので無視する)
     */
    @SuppressLint("MissingPermission")
    private fun startWatchLocation() {
        // 位置情報の設定を確認
        setClient.checkLocationSettings(locSetReq)
            .addOnSuccessListener(this)
            succ@{
                // ACCESS_FINE_LOCATIONへのパーミッションを確認
                if (ActivityCompat.checkSelfPermission(
                        this@MapsActivity, Manifest.permission.ACCESS_FINE_LOCATION
                    ) !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    return@succ
                }
                // 位置情報の取得を開始
                fusedLocationClient.requestLocationUpdates(
                    locReq, locCallback, Looper.getMainLooper()
                )
            }
            .addOnFailureListener(this) { e ->
                Log.d("MapMyLocation", e.message!!)
            }
    }

    // 位置情報取得の解除
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locCallback)
    }

    override fun onResume() {
        super.onResume()
        startWatchLocation()
    }


    /**
     * マップが利用可能になったら呼び出される(OnMapReadyCallbackが持つメソッド)。
     * 引数は準備できたマップが渡ってくる。
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.uiSettings.isZoomControlsEnabled = true
    }
}