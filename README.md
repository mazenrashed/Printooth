# Universal Bluetooth Printer
UBP aim is to provide a simple abstraction for use the bluetooth printers regardless of its brand.

###  Add the JitPack repository to your build file
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
### Add dependency
```groovy
dependencies {
	implementation 'com.github.mazenrashed:Universal-Bluetooth-Printer:1.0.0'
}
```
### Initialize UBP
Should be initialized once in `Application.onCreate()`:
```kotlin
UBP.init(context);
```
### Scan and pair printer
UBP is providing a scanning activity to make pairing process easy.
Just start `ScanningActivity` and you will skip the process of pairing and saving printer.
```kotlin
startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
```
When the printer is being ready:
```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {  
    super.onActivityResult(requestCode, resultCode, data)  
    if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK)  
        //Printer is ready now 
}
```
If you want to make your own user interface, you can pass your paired printer to UBP like this:
```kotlin
UBP.setPrinter(printerName, printerAddress)
```
Check if UBP has saved printer:
```kotlin
UBP.hasPairedPrinter()
```
To get current saved printer:
```kotlin
UBP.getPairedPrinter()
```
To remove current saved printer:
```kotlin
UBP.removeCurrentPrinter()
```
### Printing
UBP provide a simple builder to design your paper.
To print `Hello World` simply, write this code:
```kotlin
var printables = ArrayList<Printable>()
var printable = Printable.PrintableBuilder()  
        .setText("Hello World")
printables.add(printable)
BluetoothPrinter.printer(this).print(printables)
```
Use all builder responsibilities:
```kotlin
var printables = ArrayList<Printable>()
var printable = Printable.PrintableBuilder()  
        .setText("Hello World") //The text you want to print
        .setAlignment(DefaultPrinter.ALLIGMENT_CENTER)   //Text alignment
        .setEmphasizedMode(DefaultPrinter.EMPHASISED_MODE_BOLD) //Bold or normal  
        .setFontSize(0.0) // Font size
        .setUnderlined(DefaultPrinter.UNDELINED_MODE_ON) // Underline on/off
        .setCharacterCode(DefaultPrinter.CHARACTER_CODE_USA_CP437) // Character code to support languages
        .setNewLinesAfter(1) // To provide n lines after sentence
        .build()
printables.add(printable)
BluetoothPrinter.printer(this).print(printables)
```




