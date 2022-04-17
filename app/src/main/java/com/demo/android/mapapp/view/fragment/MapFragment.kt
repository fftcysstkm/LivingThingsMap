package com.demo.android.mapapp.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.demo.android.mapapp.PermissionUtils
import com.demo.android.mapapp.R
import com.demo.android.mapapp.databinding.FragmentMapBinding
import com.demo.android.mapapp.util.awaitLastLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MapFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private var permissionDenied = false
    private var map: GoogleMap? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

//    private var lastKnownLocation: Location? = null

    // バインディングクラスをnullableとし、nullで初期化
    // フラグメントはonCreateView()までレイアウトをインフレートできないため
    private var _binding: FragmentMapBinding? = null

    // onCreateView後には_bindingはnullではないことが分かっているのでnon-nullにする。読み取り専用。
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 現在地取得用のFusedLocationProviderClient取得
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // マップフラグメント取得
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * 現在地表示ボタン押下時処理（特に何もせず現在位置に移動）
     */
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
        lifecycleScope.launch {
            getDeviceLocation()
        }
    }

    /**
     * 現在地を有効にする（必要な場合は権限リクエストを行う）
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        // 許可確認開始
        // 1.権限が付与されているかどうかを確認。既に付与されていれば現在地を有効にする
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map?.isMyLocationEnabled = true
            return
        }

        // 2. ユーザーに位置情報を説明する必要が有る場合、権限リクエストダイアログを表示。
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                MapFragment.LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(parentFragmentManager, "dialog")
            return
        }

        // 3. それ以外の場合、権限をリクエストする
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            MapFragment.LOCATION_PERMISSION_REQUEST_CODE
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
        if (requestCode != MapFragment.LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        // ???なにこれ。
        if (PermissionUtils.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            || PermissionUtils.isPermissionGranted(
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

    /**
     * デバイスの最新の位置情報を取得し、マップのカメラを移動
     */
    @SuppressLint("MissingPermission")
    private suspend fun getDeviceLocation() {
        try {
            val lastLocation = fusedLocationProviderClient.awaitLastLocation()
            if (lastLocation != null) {
                map?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            lastLocation!!.latitude,
                            lastLocation!!.longitude
                        ), MapFragment.DEFAULT_ZOOM.toFloat()
                    )
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "Unable to get location", e)
        }
        //↓　移植したもの
//        try {
//            if (!permissionDenied) {
//                val locationResult = fusedLocationProviderClient.lastLocation
//                locationResult.addOnCompleteListener(requireActivity()) { task ->
//                    if (task.isSuccessful) {
//                        // Set the map's camera position to the current location of the device.
//                        lastKnownLocation = task.result
//                        if (lastKnownLocation != null) {
//                            map?.moveCamera(
//                                CameraUpdateFactory.newLatLngZoom(
//                                    LatLng(
//                                        lastKnownLocation!!.latitude,
//                                        lastKnownLocation!!.longitude
//                                    ), MapFragment.DEFAULT_ZOOM.toFloat()
//                                )
//                            )
//                        }
//                    } else {
//                        Log.d(
//                            MapFragment.TAG,
//                            "Current location is null. Using defaults."
//                        )
//                        Log.e(MapFragment.TAG, "Exception: %s", task.exception)
//                        // todo 位置情報が取得できなかった場合　↓後で参考
//                        // https://zenn.dev/yass97/articles/e99dccecdd4b80
//                        map?.moveCamera(
//                            CameraUpdateFactory
//                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
//                        )
//                        map?.uiSettings?.isMyLocationButtonEnabled = false
//                    }
//                }
//            }
//        } catch (e: SecurityException) {
//            Log.e("Exception: %s", e.message, e)
//        }
    }

    private fun showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true)
            .show(parentFragmentManager, "dialog")
    }

    companion object {
        // 位置情報取得リクエストのリクエストコード
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        // デフォルトの倍率
        private const val DEFAULT_ZOOM = 17
        private val TAG = MapFragment::class.java.simpleName

        private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    }

}