# Configurating the Build for your Application
## Set the Application ID
Every Android application has a unique application ID that looks like a Java package name, such as *com.example.myapp*. This ID uniquely identifies your app on the device and in Google Play Store. If you want to upload a new version of your application, the application ID and the certificate that you sign it with must be the same as the original APK. If the application ID is changed, Google Play Store treats the APK as a completely different application. Once the application is published, **the application ID should never be changed**. <br>
The application ID can be defined in the following manner in a module's build.gradle file
```
android {
    defaultConfig {
        applicationId "com.example.myapp"
        minSdkVersion 15
        targetSdkVersion 24
        versionCode 1
        versionName "1.0"
    }
    ...
}
```
When a project is created, the applicationId exactly matches the Java-style pacakge name, however, the application ID and package name are independent are independent of each other beyond this point. Below are some rules for the application ID since it is a bit more restrictive: <br>
* It must have at least two segments, or in other words, one or more dots
* Each segment must start with a letter
* All characters must be alphanumeric or an underscore

## Changing the application ID for build variants 
If you want to create different versions of your application to appear as separate listings on Google Play Store, such as a free or pro version, you need to create separate build variants that each have a different application ID. <br>
In this case, each build variant should be defined as a separate **product flavor**. For each flavor inside the **productFlavors** block, you can redefine the applicationId property, or you can instead append a segment to the default application ID using **applicationIdSuffix** as shown below:
```
android {
    defaultConfig {
        applicationId "com.example.myapp"
    }
    productFlavors {
        free {
            applicationIdSuffix ".free" // The applicatinId for the free product is com.example.myapp.free
        }
        pro {
            applicationIdSuffix ".pro" // The applicatinId for the pro product is com.example.myapp.pro
        }
    }
}
```

## Change the Package Name
If you want to change the package name, be aware that the package name should always match the package attribute in the AndroidManifest.xml file
```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.myapp"
    android:versionCode="1"
    android:versionName="1.0" >
```
The Android build tools use the **package** attribute for two things:
* It applies this name as the namespace for your app's generated R.java class
* It uses it to resolve any relative class names that are declared in the manifest file. An activity declared with ```<activity android:name=".MainActivity">``` is resolved to be **com.example.myapp.MainActivity**


## Enable Multidex for applications with over 64k methods
When the application and the libraries it references exceed 65,536 methods, you encounter a build error that indicates that the application has reached the limit of the Android build architecture. An example of some errors are below:
```
trouble writting output:
Too many field references: 131000; max is 65536.
You may try using --multi-dex option

OR 

Conversion to Dalvik format failed:
Unable to execute dex: method ID not in [0, 0xffff]: 65536
```
**Multidex support prior to Android 5.0** <br>
By default, Dalvik limits applications to a single classes.dex bytecode file per APK. To get around the limitation, you can add the multidex support library to your project.
```
android {
    defaultConfig {
        ...
        minSdkVersion 15 
        targetSdkVersion 28
        multiDexEnabled true
    }
    ...
}

dependencies{
    implementation 'com.android.support:multidex:1.0.3
}
```
If you do not override the Application class, edit the manifest file to set **android:name** in the ```<application>``` tag as follows:
```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.myapp">
    <application
            android:name="android.support.multidex.MultiDexApplication" >
        ...
    </application>
</manifest>
```
If you do override the Application class, change it to extend MultiDexApplication
```
public class MyApplication extends MultiDexApplication { ... }
```
If you cannot extend MultiDexApplication, instead override the attachBaseContext() method and call MultiDex.install(this) to enable multidex:
```
public class MyApplication extends SomeOtherApplication {
  @Override
  protected void attachBaseContext(Context base) {
     super.attachBaseContext(base);
     MultiDex.install(this);
  }
}
```

**Multidex support for Android 5.0 and higher** <br>
Android now uses a runtime called ART which natively supports loading multiple DEX files from APK files. Simply set the **multiDexEnabled** to **true** in a module-level build.gradle file:
```
android {
    defaultConfig {
        ...
        minSdkVersion 21 
        targetSdkVersion 28
        multiDexEnabled true
    }
    ...
}
```

## Avoid the 64k Limit
Remove unused code with Proguard: <br>
Enable code shrinking to run Proguard for your rlease builds. Enabling shrinking ensures you are not shipping unused code with your APKs.