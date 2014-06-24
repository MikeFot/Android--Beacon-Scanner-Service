#IBeaconScannerService
Sample Service App for Bluetooth LE Scanner

##Description
- This application will start a Service to scan for IBeacons through an ASyncTask. 
- The Service will then reschedule itself for future runs and will unregister once the maximum number of runs have been reached or the application is paused through the MainActivity's onPause() method.

##Requirements
This is intended to be used in conjunction with alt236's "Bluetooth LE Library for Android" library project which can be found here:
https://github.com/alt236/Bluetooth-LE-Library---Android

##Target Platforms
Android devices running OS version 18+ and which can support receiving of Bluetooth LE signal.

##Usage
The Main Activity provided is just for demo purposes. All the results are reported through a Logger utility.
