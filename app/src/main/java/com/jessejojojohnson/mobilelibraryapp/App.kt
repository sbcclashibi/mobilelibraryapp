package com.jessejojojohnson.mobilelibraryapp

import android.app.Application
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.jessejojojohnson.mobilelibraryapp.util.slog
import com.jessejojojohnson.mobilelibraryapp.util.sloge


class App: Application() {

    private lateinit var scanner: GmsBarcodeScanner

    override fun onCreate() {
        super.onCreate()
        instance = this

        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_EAN_13
            )
            .build()
        scanner = GmsBarcodeScanning.getClient(this, options)

        val moduleInstallClient = ModuleInstall.getClient(this)
        moduleInstallClient.installModules(
            ModuleInstallRequest.Builder()
                .addApi(GmsBarcodeScanning.getClient(this))
                .build()
        ).addOnSuccessListener {
            if (it.areModulesAlreadyInstalled()) {
                // Modules are present on the device...
                slog("Modules are available")
            } else {
                // Modules are not present on the device...
                sloge("Modules aren't available on device")
            }
        }.addOnFailureListener {
            sloge("Failed to install modules")
            sloge(it.localizedMessage!!)
        }
    }

    fun getScanner() = scanner

    companion object {
        private lateinit var instance: App
        fun getInstance() = instance
    }
}