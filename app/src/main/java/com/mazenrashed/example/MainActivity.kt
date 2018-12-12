package com.mazenrashed.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.DefaultPrinter
import com.mazenrashed.printooth.data.Printable
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import kotlinx.android.synthetic.main.activity_main.btnPiarUnpair
import kotlinx.android.synthetic.main.activity_main.btnPrint

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initListeners()
    }

    private fun initListeners() {
        btnPrint.setOnClickListener {
            if (!Printooth.hasPairedPrinter()) startActivityForResult(Intent(this,
                    ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER)
            else printSomePrintable()
        }
        btnPiarUnpair.setOnClickListener {
            if (Printooth.hasPairedPrinter()) Printooth.removeCurrentPrinter()
            else startActivityForResult(Intent(this, ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER)
            initViews()
        }
    }

    private fun initViews() {
        btnPiarUnpair.text = if (Printooth.hasPairedPrinter()) "Un-pair ${Printooth.getPairedPrinter()?.name}" else "Pair with printer"
    }

    private fun printSomePrintable() {
        Printooth.printer().printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(this@MainActivity, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@MainActivity, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@MainActivity, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@MainActivity, "Message: $message", Toast.LENGTH_SHORT).show()
            }

        }

        val printable = getSomePrintables()

        Printooth.printer().print(printable)
    }

    private fun getSomePrintables() = ArrayList<Printable>().apply {
        add(Printable.PrintableBuilder()
                .setText("Hello World")
                .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                .setNewLinesAfter(1)
                .build())
        add(Printable.PrintableBuilder().setImage(R.mipmap.ic_launcher, resources).build())
//
//
//        add(Printable.PrintableBuilder()
//                .setText("Hello World")
//                .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
//                .setAlignment(DefaultPrinter.ALLIGMENT_CENTER)
//                .setEmphasizedMode(DefaultPrinter.EMPHASISED_MODE_BOLD)
//                .setUnderlined(DefaultPrinter.UNDELINED_MODE_ON)
//                .setNewLinesAfter(1)
//                .build())
//
//        add(Printable.PrintableBuilder()
//                .setText("Hello World")
//                .setAlignment(DefaultPrinter.ALLIGMENT_REGHT)
//                .setEmphasizedMode(DefaultPrinter.EMPHASISED_MODE_BOLD)
//                .setUnderlined(DefaultPrinter.UNDELINED_MODE_ON)
//                .setNewLinesAfter(1)
//                .build())
//
//        add(Printable.PrintableBuilder()
//                .setText("اختبار العربية")
//                .setAlignment(DefaultPrinter.ALLIGMENT_CENTER)
//                .setEmphasizedMode(DefaultPrinter.EMPHASISED_MODE_BOLD)
//                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
//                .setUnderlined(DefaultPrinter.UNDELINED_MODE_ON)
//                .setCharacterCode(DefaultPrinter.CHARACTER_CODE_ARABIC_FARISI)
//                .setNewLinesAfter(1)
//                .build())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK)
            printSomePrintable()
        initViews()
    }
}