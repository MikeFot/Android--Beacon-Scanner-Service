#IBeaconScannerService
Sample Service App for Bluetooth LE Scanner

##Description
- This application will start a Service to scan for IBeacons through an ASyncTask. 
- The Service will then reschedule itself for future runs and will unregister once the maximum number of runs have been reached or the application is paused through the MainActivity's onPause() method.

##Dependencies
This is intended to be used in conjunction with alt236's "Bluetooth LE Library for Android" library project which can be found here:
https://github.com/alt236/Bluetooth-LE-Library---Android
It also utilises John Persnano's SuperToasts, found here:
https://github.com/JohnPersano/SuperToasts

##Target Platforms
Android devices running OS version 18+ and which can support receiving of Bluetooth LE signal.

##Usage
The Main Activity is currently providing a simple visualisation of results.

##Play Store Project
https://play.google.com/store/apps/details?id=com.michaelfotiadis.ibeaconscanner

The code in this project is licensed under the Apache Software License 2.0.
