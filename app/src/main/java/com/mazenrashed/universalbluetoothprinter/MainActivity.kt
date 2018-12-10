package com.mazenrashed.universalbluetoothprinter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mazenrashed.universalbluethootprinter.Printooth
import com.mazenrashed.universalbluethootprinter.data.DefaultPrinter
import com.mazenrashed.universalbluethootprinter.data.Printable
import com.mazenrashed.universalbluethootprinter.ui.ScanningActivity
import kotlinx.android.synthetic.main.activity_main.btnPiarUnpair
import kotlinx.android.synthetic.main.activity_main.btnPrint

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPrint.setOnClickListener {
            if (!Printooth.hasPairedPrinter()) startActivityForResult(Intent(this,
                        ScanningActivity::class.java),
                        ScanningActivity.SCANNING_FOR_PRINTER)
            else printSomePrintable()
        }

        btnPiarUnpair.text = if (Printooth.hasPairedPrinter()) "Unpair ${Printooth.getPairedPrinter()?.name}" else "Pair with printer"

        btnPiarUnpair.setOnClickListener {
            if (Printooth.hasPairedPrinter()) Printooth.removeCurrentPrinter()
            else startActivityForResult(Intent(this, ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER)
        }
    }

    private fun printSomePrintable() {
        val printable = ArrayList<Printable>()
        printable.add(
                Printable.PrintableBuilder()
                        .setText("Hello World")
                        .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                        .setAlignment(DefaultPrinter.ALLIGMENT_CENTER)
                        .setEmphasizedMode(DefaultPrinter.EMPHASISED_MODE_BOLD)
                        .setFontSize(0)
                        .setUnderlined(DefaultPrinter.UNDELINED_MODE_ON)
                        .setCharacterCode(DefaultPrinter.CHARACTER_CODE_USA_CP437)
                        .setNewLinesAfter(1)
                        .build()
        )
        printable.add(
                Printable.PrintableBuilder()
                        .setText("Hello World")
                        .setAlignment(DefaultPrinter.ALLIGMENT_LEFT)
                        .setEmphasizedMode(DefaultPrinter.EMPHASISED_MODE_NORMAL)
                        .setFontSize(0)
                        .setUnderlined(DefaultPrinter.UNDELINED_MODE_OFF)
                        .setCharacterCode(DefaultPrinter.CHARACTER_CODE_USA_CP437)
                        .setNewLinesAfter(1)
                        .build()
        )
        printable.add(
                Printable.PrintableBuilder()
                        .setText("Hello World")
                        .setAlignment(DefaultPrinter.ALLIGMENT_REGHT)
                        .setEmphasizedMode(DefaultPrinter.EMPHASISED_MODE_BOLD)
                        .setFontSize(1)
                        .setUnderlined(DefaultPrinter.UNDELINED_MODE_ON)
                        .setCharacterCode(DefaultPrinter.CHARACTER_CODE_USA_CP437)
                        .setNewLinesAfter(1)
                        .build()
        )
        printable.add(
                Printable.PrintableBuilder()
                        .setText("اختبار العربية")
                        .setAlignment(DefaultPrinter.ALLIGMENT_CENTER)
                        .setEmphasizedMode(DefaultPrinter.EMPHASISED_MODE_BOLD)
                        .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                        .setUnderlined(DefaultPrinter.UNDELINED_MODE_ON)
                        .setCharacterCode(DefaultPrinter.CHARACTER_CODE_ARABIC_CP864)
                        .setNewLinesAfter(1)
                        .build()
        )

        Printooth.printer(this).print(printable)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK)
            printSomePrintable()
    }
}
