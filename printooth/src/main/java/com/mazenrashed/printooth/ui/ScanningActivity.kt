package com.mazenrashed.printooth.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mazenrashed.printooth.databinding.ActivityScanningBinding

class ScanningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.scanningView.establishDeviceBondedCallback {
            this.setResult(RESULT_OK)
            this.finish()
        }
    }
}
