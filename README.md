# Beacon Scanner Service Sample App
Sample Service App for Bluetooth LE Scanner

## Description
- This application will start a Service to scan for Beacons through an ASyncTask. 
- The Service will then reschedule itself for future runs and will unregister once the maximum number of runs have been reached or the application is paused through the MainActivity's onPause() method.

## Dependencies

This is intended to be used in conjunction with alt236's "Bluetooth LE Library for Android" library project which can be found here:
https://github.com/alt236/Bluetooth-LE-Library---Android

It also utilises John Persnano's SuperToasts, found here:
https://github.com/JohnPersano/SuperToasts

##Target Platforms
Android devices running OS version 18+ and which can support receiving of Bluetooth LE signal.

## Usage
The Main Activity is currently providing a simple visualisation of results.

## Play Store Page

<a href='https://play.google.com/store/apps/details?id=com.michaelfotiadis.ibeaconscanner&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_gb/badges/images/generic/en_badge_web_generic.png'/></a>

## Screenshots

![Screenshot 1](/screenshots/Screenshot_1.png?raw=true)

![Screenshot 2](/screenshots/Screenshot_2.png?raw=true)

![Screenshot 3](/screenshots/Screenshot_3.png?raw=true)

![Screenshot 4](/screenshots/Screenshot_4.png?raw=true)

![Screenshot 5](/screenshots/Screenshot_5.png?raw=true)

The code in this project is licensed under the Apache Software License 2.0.
