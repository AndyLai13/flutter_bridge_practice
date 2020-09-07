package com.viewsonic.bridge_practice

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.Toast
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
	private val CHANNEL = "samples.flutter.dev/battery"

	override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
		MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
			call, result ->
			if (call.method == "getBatteryLevel") {
				val batteryLevel = getBatteryLevel()

				if (batteryLevel != -1) {
					result.success(batteryLevel)
				} else {
					result.error("UNAVAILABLE", "Battery level not available.", null)
				}
			} else if (call.method == "showToast") {
				showToast()
				result.success("Success")
			} else {
				result.notImplemented()
			}
		}
	}

	private fun showToast() {
		Toast.makeText(this, "Toast Test", Toast.LENGTH_SHORT).show()
	}

	private fun getBatteryLevel(): Int {
		val batteryLevel: Int
		if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
			val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
			batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
		} else {
			val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
			batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
		}

		return batteryLevel
	}
}
