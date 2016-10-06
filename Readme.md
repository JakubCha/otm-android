[![Travis build status](https://travis-ci.org/OpenTreeMap/otm-android.png?branch=master)](https://travis-ci.org/OpenTreeMap/otm-android
)

OpenTreeMap for Android
=======================

Development Instructions
------------------------

You will need to run an OpenTreeMap server to use the Android app.  If you do not have an OpenTreeMap development environment, you can use the [otm-vagrant project](https://github.com/OpenTreeMap/otm-vagrant) to get started.

### Android Studio Setup

* This project uses [RetroLambda](https://github.com/evant/gradle-retrolambda) to support Java-8 Lambdas on Android, which requires JDK8:
  * Install JDK8 (If you are running Ubuntu, [see here](http://www.webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html))

* download Android Studio

* unzip it wherever you want

* Using the SDK Manager, install necessary software:
  * From Extras, install:
    * `Google Play services`
    * `Google Repository`
    * `Android Support Repository`
  * For all the SDK versions that you will use, (currently API 10, API 16, API [latest]), grab:
    * `SDK Platform`
    * `Google APIs` (ARM if there's a choice)
    * `System Image` (if you are using an emulator)

### Device / Emulator Setup

You may want to setup your device for debugging, if you prefer that over working in an emulator.
Follow these instructions:
http://developer.android.com/tools/device.html

If you would prefer to use an emulator, make sure to use an x86 image with Google APIs available, and set your computer up to use hardware acceleration as documented here: https://software.intel.com/en-us/android/articles/speeding-up-the-android-emulator-on-intel-architecture

### App Setup

* clone the `otm-android` repo to wherever you store code

* Put the appropriate values into the templates in OpenTreeMapSkinned (for more information, see the [OpenTreeMapSkinned README](OpenTreeMapSkinned/README.md))

* Setup google maps API key. See [Google's documentation](https://developers.google.com/maps/documentation/android/start#step_4_get_a_google_maps_api_key) for help in doing so.  Set this key as the `google_maps_api_key` in the `defaults.xml` template in the OpenTreeMapSkinned project.

### Weird Bugs

If your debug.keystore is not generated, you probably won't be able to run the [keytool command](https://developers.google.com/maps/documentation/android/start#obtain_a_google_maps_api_key). When you first try to run the OpenTreeMapSkinned project, Android Studio should create a debug.keystore if you do not already have one.

USDA Grant
---------------
Portions of OpenTreeMap are based upon work supported by the National Institute of Food and Agriculture, U.S. Department of Agriculture, under Agreement No. 2010-33610-20937, 2011-33610-30511, 2011-33610-30862 and 2012-33610-19997 of the Small Business Innovation Research Grants Program. Any opinions, findings, and conclusions, or recommendations expressed on the OpenTreeMap website are those of Azavea and do not necessarily reflect the view of the U.S. Department of Agriculture.
