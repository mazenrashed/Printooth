package com.mazenrashed.universalbluetoothprinter

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mazenrashed.universalbluethootprinter.BluetoothPrinter
import com.mazenrashed.universalbluethootprinter.data.DefaultPrinter
import com.mazenrashed.universalbluethootprinter.data.Printable
import com.mazenrashed.universalbluethootprinter.ui.ScanningActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        print.setOnClickListener {
            if (!BluetoothPrinter.hasPairedPrinter())
                startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
            else
                printSomePrintables()
        }
    }

    private fun printSomePrintables() {
        var printables = ArrayList<Printable>()
        printables.add(
                Printable.PrintableBuilder()
                        .setText("Hello World")
                        .setAlignment(DefaultPrinter.ALLIGMENT_CENTER)
                        .setEmphasizedMode(DefaultPrinter.EMPHASISED_MODE_BOLD)
                        .setFontSize(0.0)
                        .setUnderlined(DefaultPrinter.UNDELINED_MODE_ON)
                        .setCharacterCode(DefaultPrinter.CHARACTER_CODE_USA_CP437)
                        .setNewLinesAfter(1)
                        .build()
        )
        BluetoothPrinter.printer(this).print(printables)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK)
            printSomePrintables()
    }
}
