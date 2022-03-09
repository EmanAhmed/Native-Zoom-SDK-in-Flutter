package com.example.test_zoom_app

import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.os.BatteryManager
import android.content.ContextWrapper
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.content.Context

import android.util.Log

import us.zoom.sdk.JoinMeetingOptions

import us.zoom.sdk.JoinMeetingParams

import us.zoom.sdk.MeetingError

import us.zoom.sdk.MeetingService

import us.zoom.sdk.MeetingStatus

import us.zoom.sdk.StartMeetingOptions

import us.zoom.sdk.StartMeetingParamsWithoutLogin

import us.zoom.sdk.ZoomSDK

import us.zoom.sdk.ZoomSDKInitParams

import us.zoom.sdk.ZoomSDKInitializeListener

class MainActivity : FlutterActivity(), ZoomSDKInitializeListener {

  private val CHANNEL = "samples.flutter.dev/battery"
  private val APP_KEY = "YOUR_APP_KEY"
  private val APP_SECRET = "YOUR_APP_SECRET"
  private val DOMAIN = "zoom.us"

  override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
    super.configureFlutterEngine(flutterEngine)
    MethodChannel(
      flutterEngine.dartExecutor.binaryMessenger,
      CHANNEL
    ).setMethodCallHandler { call, result ->
//      if (call.method == "getBatteryLevel") {
//        val batteryLevel = getBatteryLevel()
//
//        if (batteryLevel != -1) {
//          result.success(batteryLevel)
//        } else {
//          result.error("UNAVAILABLE", "Battery level not available.", null)
//        }
//      } else {
//        result.notImplemented()
//      }

      if (call.method == "joinZoom") {
        var meetingId:String? = call.argument("meetingId");
        var password:String? = call.argument("password");
        var name:String? = call.argument("name");
        joinZoom(meetingId , password , name);
        result.success("done")
      }
      else if(call.method == "initZoom" ){
        initZoom()
        result.success("done")
      }else{
        result.notImplemented();
      }

    }

  }

  override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
    if (errorCode == us.zoom.sdk.ZoomError.ZOOM_ERROR_SUCCESS) {
      Log.d("TAG", "zoom init success")
      registerMeetingServiceListener(zoomSDK.getMeetingService())
    } else Log.d("TAG", "zoom init failed")

  }

  override fun onZoomAuthIdentityExpired() {
    Log.d("TAG", "onZoomAuthIdentityExpired : ")

  }

  private fun getBatteryLevel(): Int {

    val batteryLevel: Int
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      val batteryManager =
        getSystemService(android.content.Context.BATTERY_SERVICE) as BatteryManager
      batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    } else {
      val intent = ContextWrapper(applicationContext).registerReceiver(
        null,
        android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)
      )
      batteryLevel =
        intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(
          BatteryManager.EXTRA_SCALE,
          -1
        )
    }




    return batteryLevel
  }

  lateinit var zoomSDK: ZoomSDK;
  private fun joinZoom(meetingNumber: String?,password: String?,name: String?) {
    Log.d("Zoom", "Join Zoom");
    joinMeeting(
      this,
      zoomSDK.getMeetingService(),
      name!!,
      meetingNumber!!, password!!
    )

  }

  private fun initZoom() {
    Log.d("Zoom", "Join Zoom");
    zoomSDK = ZoomSDK.getInstance()
    initZoomSdk(context, zoomSDK, zoomSDK.getMeetingService())

  }

  fun initZoomSdk(context: Context?, zoomSDK: ZoomSDK, meetingService: MeetingService?) {
    val params = ZoomSDKInitParams()
    params.appKey = APP_KEY
    params.appSecret = APP_SECRET
    params.domain = DOMAIN
    params.enableLog = true
    zoomSDK.initialize(context, this, params)
  }

  fun registerMeetingServiceListener(meetingService: MeetingService?) {
    if (meetingService != null) {
      Log.d("TAG", "meetingService != null")
      meetingService.addListener { meetingStatus, errorCode, internalErrorCode ->
        Log.i(
          "TAG",
          "onMeetingStatusChanged, meetingStatus=" + meetingStatus + ", errorCode=" + errorCode
              + ", internalErrorCode=" + internalErrorCode
        )
        if (meetingStatus === MeetingStatus.MEETING_STATUS_FAILED && errorCode === MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
          Log.d("TAG", "Version of ZoomSDK is too low!")
        }
        if (meetingStatus === MeetingStatus.MEETING_STATUS_IDLE || meetingStatus === MeetingStatus.MEETING_STATUS_FAILED) {
          Log.d("TAG", "Meeting status idle or failed !!");
        }
      }
    } else Log.d("TAG", "meetingService == null")
  }

  fun joinMeeting(
    context: Context?,
    meetingService: MeetingService,
    name: String,
    meetingNumber: String,
    password: String
  ) {
    val options = JoinMeetingOptions()
    val params = JoinMeetingParams()
    params.displayName = name
    params.meetingNo = meetingNumber
    params.password = password
    meetingService.joinMeetingWithParams(context, params, options)
  }

}
