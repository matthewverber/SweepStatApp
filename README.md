# SweepStatApp 

The SweepStatApp is an Android app that interfaces with the SweepStat, a low-cost Arduino-based potentiostat. The device is used by everyone including national agencies as well as Chemistry educators and students for measurements in the scientific and medical fields. 

# Getting Started 

## Prerequisites  

Java 8 SDK and Android Studio  

To install Java 8 SDK,  

Go to the following Oracle website  

https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html 

Check “Accept License Agreement” 

Download the correct version for your system (Do not download the Demos and Samples) 

Run the JDK installer 

To install Android Studio,  

Go to Android Studio website https://developer.android.com/studio 

Click “Download Android Studio” 

Run the Android Studio installer 

## Installing 

Import the project from Github: 

https://github.com/AmadeusSko/SweepStatApp 

Change Java version to Java 8 if necessary. 

## Running Locally 

To run the SweepStatApp locally, one can either download the complete .apk file to an Android device running Android version 4.4 or higher (KitKat, API 19), or to an emulated device with the same system requirements. Alternatively, one can fork the GitHub repository through Android Studio on either a Windows or Mac computer and edit the application there or run it through Android Studio’s built-in device emulator (AVD). It must be noted that the AVD does not have Bluetooth capabilities. 

## Warranty 

 Last tested on 11/14/2019 by Team Q on Windows 10 64 bit 

# Testing 

 The current test suites are built in both Android JUnit and Espresso. The JUnit tests are meant to test data within the Java documents, such as values and objects behaving according to expectations. The Espresso tests are built to be run on physical devices or on emulators as they test the user interface of the application to ensure consistency and proper displaying of elements. To run the JUnit tests, a user must be using the app in a development setting in a desktop IDE. JUnit is included with the installation of a JDK. To run Espresso tests, a user must have either a physical Android device in debug mode with all visual effects and transitions disabled or run a virtual device with the same constraints.  

# Deployment 

 The app will be deployed on the Google Play Store or can also be distributed as an apk. 

# Technologies Used 

Java 8 

Android Studio 

ADRs: https://github.com/AmadeusSko/SweepStatApp/tree/master/ADR 

# Contributing 

Github repo: https://github.com/AmadeusSko/SweepStatApp 

Trello: https://trello.com/b/3pyUyZvF/sweepstatapp 

No specific coding style or convention is enforced, but good coding style is encouraged. 

Project website: https://sweepstatapp.web.unc.edu/ 

# Authors 

Amadeus Skoczek 

Ken Liu 

Eric Xin 

# License 

[Apache 2.0] (https://github.com/AmadeusSko/SweepStatApp/blob/master/LICENSE) 

# Acknowledgements 

The development team acknowledges the work of Jonas Gehring and his team for the jjoe64 GraphView used to plot data from the SweepStat within the application. 
