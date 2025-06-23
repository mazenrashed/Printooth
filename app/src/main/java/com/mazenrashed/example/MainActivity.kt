package com.mazenrashed.example

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mazenrashed.example.databinding.ActivityMainBinding
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.converter.ArabicConverter
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.data.printer.Printer
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.PermissionsUtils
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var printing : Printing? = null

    private var customPrinter: Printer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        printing = getPrinting()

        initViews()
        initListeners()
    }

    private fun initViews() {
        binding.btnPairUnpair.text = if (Printooth.hasPairedPrinter()) "Un-pair ${Printooth.getPairedPrinter()?.name}" else "Pair with printer"
    }

    private fun initListeners() {
        binding.btnPrint.setOnClickListener {
            if (!Printooth.hasPairedPrinter()) invokeScanningActivity()
            else printSomePrintable()
        }

        binding.btnPrintImages.setOnClickListener {
            if (!Printooth.hasPairedPrinter()) invokeScanningActivity()
            else printSomeImages()
        }

        binding.btnPairUnpair.setOnClickListener {
            if (Printooth.hasPairedPrinter()) Printooth.removeCurrentPrinter()
            else invokeScanningActivity()

            initViews()
        }

        binding.btnCustomPrinter.setOnClickListener {
            customPrinter = if (customPrinter == null) WoosimPrinter() else null

            printing = getPrinting()

            binding.btnCustomPrinter.text = if (customPrinter != null) "Default printer" else "Custom printer (woosim)"
        }

        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(this@MainActivity, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@MainActivity, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String?) {
                Toast.makeText(this@MainActivity, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String?) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@MainActivity, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@MainActivity, "Disconnected Printer", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPrinting(): Printing? {
        return if (Printooth.hasPairedPrinter()) {
            if (customPrinter != null) Printooth.printer(customPrinter!!) else Printooth.printer()
        }
        else null
    }

    private fun invokeScanningActivity() {
        invokeAction { scanPrinterResult.launch(Intent(this, ScanningActivity::class.java)) }
    }

    private fun invokeAction(action: () -> Unit) {
        if (PermissionsUtils.isBluetoothEnabled(this)) {
            val requiredPermissions = PermissionsUtils.requiredPermissions()

            val permissionsGranted = requiredPermissions.all { p -> ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED }

            if (permissionsGranted) {
                action.invoke()
            }
            else {
                requestPermissionsLauncher.launch(requiredPermissions.toTypedArray())
            }
        }
        else {
            Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun printSomePrintable() {
        invokeAction { printing?.print(getSomePrintables()) }
    }

    @SuppressLint("MissingPermission")
    private fun printSomeImages() {
        invokeAction {
            val printables = arrayListOf<Printable>(
                ImagePrintable.Builder(R.drawable.image1, resources).build(),
                ImagePrintable.Builder(R.drawable.image2, resources).build(),
                ImagePrintable.Builder(R.drawable.image3, resources).build()
            )

            printing?.print(printables)
        }
    }

    private fun getSomePrintables() = ArrayList<Printable>().apply {
        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build()) // feed lines example in raw mode

        add(TextPrintable.Builder()
                .setText(" Hello World : été è à '€' içi Bò Xào Coi Xanh")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build())

        add(TextPrintable.Builder()
                .setText("Hello World : été è à €")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build())

        add(TextPrintable.Builder()
                .setText("Hello World")
                .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setNewLinesAfter(1)
                .build())

        add(TextPrintable.Builder()
                .setText("Hello World")
                .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setNewLinesAfter(1)
                .build())

        add(TextPrintable.Builder()
                .setText("اختبار العربية")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setCharacterCode(DefaultPrinter.CHARCODE_ARABIC_FARISI)
                .setNewLinesAfter(1)
                .setCustomConverter(ArabicConverter()) // change only the converter for this one
                .build())
    }

    private val scanPrinterResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            printing = getPrinting()
        }

        initViews()
    }

    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.containsValue(false)) {
            Toast.makeText(this@MainActivity, "Permissions are required to use the app", Toast.LENGTH_SHORT).show()
        }
    }
}
