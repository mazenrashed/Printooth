
# Printooth
[![](https://jitpack.io/v/mazenrashed/Printooth.svg)](https://jitpack.io/#mazenrashed/Printooth)

Printooth aim is to provide a simple abstraction for use the bluetooth printers regardless of its brand.

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
	implementation 'com.github.mazenrashed:Printooth:1.0.2'
}
```
### Add persessions to manifest
```groovy
<uses-permission android:name="android.permission.BLUETOOTH" />  
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```
### Initialize Printooth
Should be initialized once in `Application.onCreate()`:
```kotlin
Printooth.init(context);
```
### Scan and pair printer
Printooth is providing a scanning activity to make pairing process easy. Just start `ScanningActivity` and you will skip the process of pairing and saving printer.
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
If you want to make your own user interface, you can pass your paired printer to Printooth like this:
```kotlin
Printooth.setPrinter(printerName, printerAddress)
```
Check if Printooth has saved printer:
```kotlin
Printooth.hasPairedPrinter()
```
To get current saved printer:
```kotlin
Printooth.getPairedPrinter()
```
To remove current saved printer:
```kotlin
Printooth.removeCurrentPrinter()
```
### Printing
Printooth provide a simple builder to design your paper.
To print `Hello World` simply, write this code:
```kotlin
var printables = ArrayList<Printable>()
var printable = Printable.PrintableBuilder()  
        .setText("Hello World")
printables.add(printable)
BluetoothPrinter.printer().print(printables)
```
Use all builder responsibilities:
```kotlin
var printables = ArrayList<Printable>()
var printable = Printable.PrintableBuilder()  
        .setText("Hello World") //The text you want to print
        .setAlignment(DefaultPrinter.ALLIGMENT_CENTER)
        .setEmphasizedMode(DefaultPrinter.EMPHASISED_MODE_BOLD) //Bold or normal  
        .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
        .setUnderlined(DefaultPrinter.UNDELINED_MODE_ON) // Underline on/off
        .setCharacterCode(DefaultPrinter.CHARACTER_CODE_USA_CP437) // Character code to support languages
        .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
        .setNewLinesAfter(1) // To provide n lines after sentence
        .build()
printables.add(printable)
BluetoothPrinter.printer().print(printables)
```
### Listen to your printing order state:
```kotlin
Printooth.printer().printingCallback = object : PrintingCallback {  
    override fun connectingWithPrinter() { } 
  
    override fun printingOrderSentSuccessfully() { }  //printer was received your printing order successfully.
  
    override fun connectionFailed(error: String) { }  
  
    override fun onError(error: String) { }  
  
    override fun onMessage(message: String) { }  
}
```
### Use more than printer in the same time:
```kotlin
var printer1 = PairedPrinter(name, address)  
var printer2 = PairedPrinter(name, address)  
BluetoothPrinter.printer(printer1).print(printables)  
BluetoothPrinter.printer(printer2).print(printables)
```
### If you have a printer with deferent commands

Create a class from type `Printer` and override the initializers method, then return your printer commands from the printers command sheet ( You can find it on the Internet ), lets take an example:
 ```kotlin
 open class SomePrinter : Printer() {  
  
    override fun initLineSpacingCommand(): ByteArray = byteArrayOf(0x1B, 0x33)  
  
    override fun initInitPrinterCommand(): ByteArray = byteArrayOf(0x1b, 0x40)  
  
    override fun initJustificationCommand(): ByteArray = byteArrayOf(27, 97)  
  
    override fun initFontSizeCommand(): ByteArray = byteArrayOf(29, 33)  
  
    override fun initEmphasizedModeCommand(): ByteArray = byteArrayOf(27, 69)
  
    override fun initUnderlineModeCommand(): ByteArray = byteArrayOf(27, 45) 
  
    override fun initCharacterCodeCommand(): ByteArray = byteArrayOf(27, 116)  
  
    override fun initFeedLineCommand(): ByteArray = byteArrayOf(27, 100)  
}
```
Then pass your printer class to Printooth:
```kotlin
BluetoothPrinter.printer(SomePrinter()).print(printables)
```

### Proguard config
````
-keep class * implements java.io.Serializable { *; }
````
