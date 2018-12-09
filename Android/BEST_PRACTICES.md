# Android

## Android Best Practices
1. Use Strings.xml for support for multiple languages
2. Create a separate layout for UI elements that will be reused <br>
**Using \<include\>**
```
<Button
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/unique_button"
    android:textStyle="bold"
    android:text="Unique button" />
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:text="See button below" />

    <include layout="@layout/unique_button.xml" />
</LinearLayout>
```
**Using \<merge\>**
For example, if your re-usable layout contains two Buttons placed vertically, you can put them inside a LinearLayout with a vertical orientation. But this LinearLayout becomes redundant if the layout is included using \<include\> into another LinearLayout. In this case, the re-usable alyout can have \<merge\> as the root ViewGroup instead of Linear Layout
```
<merge xmlns:android="http://schemas.android.com/apk/res/android">
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:text="Submit" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:text="Reset" />
</merge>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter text" />

    <include layout="@layout/buttons.xml" />
</LinearLayout>
```
3. Include icons from the xxxhdpi drawable folder since many phones are beginning to be able to use those resources
4. Place launcher icons in mipmap folders since mipmap folders do not get stripped of their quality in density
5. Avoid deep levels in layouts by using the appropriate top level layout <br>
**DON'T**
```
<LinearLayout
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="horizontal">

    <ImageView
        android:layout_width="wrap_content" 
        android:layout_height="wrap_content"
        android:src="@drawable/magnifying_glass" />

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="top text" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="bottom text" />
    </LinearLayout>
</LinearLayout>
```
**DO**
```
<RelativeLayout
    android:layout_width="wrap_content"
    android:layout_height="wrap_content">

    <ImageView
        android:id="@+id/image"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/magnifying_glass" />

    <TextView
        android:id="@+id/top_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_toRightOf="@id/image"
        android:text="top text" />

    <TextView
        android:id="@+id/bottom_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@id/top_text"
        android:layout_toRightOf="@id/image"
        android:text="bottom text" />
</RelativeLayout>
```
6. Don't add the whole google service library
7. Use Libraries such as Volley and Retrofit for networking
8. Use the Parcelable class instead of Serializable when passing data in Intents/Bundles
9. Use an AsyncTaskLoader instead of an AsyncTask
    * If an activity get destroyed before the AsyncTask has completed, it will still keep running and deliver the result in it's onPostExecute() method. A typical example is when a device is roated while an AsyncTask is loading content.
    * Loaders are managed by a LoaderManager which is tied to the lifecycle of its Activity or Fragment. Each Activiry or Fragment contains an instance of LoaderManager. If the Activity/Fragment is destroyed, the LoaderManager destroys the Loaders and frees up resources. In case of a configuration change, it retains its Loaders.
```
// Get a LoaderManager instance and initialize a Loader in the following manner
class MyActivity extends Activity implements LoaderManager.LoaderCallbacks<String> {
    private static final int LOADER_ID = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The third argument to the initLoader function is a callback that specified the below functions
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        // Return a loader according to the Loader id
        return new CustomLoader(context);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        // Process result here
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
            
    }
}

// A simple AsyncTaskLoader can be created in the following manner:
class CustomLoader extends AsyncTaskLoader<String> {

    public CustomLoader(Context context) {
        super(context);
    }

    public String loadInBackground() {
        String result = null;
        // Load result string
        return result;
    }

    // Override the following methods if necessary:
    onStartLoading()
    onForceLoad()
    onReset()
    onCancelled()
    onStopLoading()
    onAbandon()
    cancelLoadInBackground()
    onCancelLoad()
}

```
10. Perform file Operations using AsyncTask/Loader

11. Handling Passwords. In the application's build.gradle, you will need to define the **signingConfigs** for the release build. <br>
Avoid the following:
```
signingConfigs{
    release {
        //DON'T DO THIS
        storeFile file("myapp.keystore")
        storePassword "password123"
        keyAlias "theKey"
        keyPassword "password789"
    }
}
```
Make the following **gradle.properties** file which should NOT be added to the version control system
```
KEYSTORE_PASSWORD=password123
KEY_PASSWORD=password789
```
The above file is automatically imported by Gradle, so you can use it in **build.gradle** as such:
```
singningConfigs {
    release {
        try {
            storeFile file("myapp.keystore")
            storePassword KEYSTORE_PASSWORD
            keyAlias "thekey"
            keyPassword KEY_PASSWORD
        }catch (ex) {
            throw new InvalidUserDataException("You should define KEYSTORE_PASSWORD and KEY_PASSWORD in gradle.properties");
        }
    }
}
```
12. Use different package name for non-release builds.
* use **applicationIdSuffix** for debug build type to be able to install both *debug* and *release* apk on the same device. This will be especially valuable after your application has been published.
```
android {
    buildTypes {
        debug{
            applicationIdSuffix '.debug'
            versionNameSuffix '-DEBUG'
        }
        release {
            //...
        }
    }
}
```

13. Use different icons for the debug and release builds.
* Gradle makesthis easy, simply put *debug* icon in app/src/debug/res and *release* icon in app/src/release/res.

14. Use JUnit for unit testing.
15. Espresso for UI tests
16. AssertJ-Android makes it easy for you to test Android specific components such as the Android Support, Google Play Services, and Appcompat libraries
17. Data storage best practices
* Shared Preferences for simple values and your application runs in a single process. These should not be used if your data is complex and abundant, multiple processes accessing the data, or you want a relation data structure.
* ContentProviders are fast and process safe. You can also use the **Schematic** library to ease making the boiler plate code for ContentProvider