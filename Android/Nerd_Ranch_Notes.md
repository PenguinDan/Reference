# Nerd Ranch Notes

## Definitions
Inflate : 
* When a layout is inflated, each widget in the layout file is instantiated as defined by its attributes.

## Communicating Between Activities
Sending Data from one Activity to Another upon Activity 2 Creation
```
// Activity 1
Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
startActivity(intent);

// Activity 2
mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
```
Getting a result back from a child Activity. If **setResult(..)** is not called, the OS will send a default resultCode back to the parent once the user hits the back button
```
// Activity 1
boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
startActivityForResult(intent, REQUEST_CODE_CHEAT);

// Activity 2
// There are two methods that can be called to send data back to the parent
// * resultCode typically equals Activity.RESULT_OK or Activity.RESULT_CANCELED
public final void setResult(int resultCode);
public final void setResult(int resultCode, Intent data);

// Back to Activity 1
protected void onActivityResult(int requestCode, int resultCode, Intent data);
```

## Setting Arguments for Fragments
There is a convention of adding a static method named **newInstance(...)** to the Fragment class. This method creates the fragment instance and bundles up and sets its arguments.
```
// From Fragment
public static CrimeFragment newInstace(UUID crimeId) {
    Bundle args = new Bundle();
    args.putSerializable(ARG_CRIME_ID, crimeId);

    CrimeFragment fragment = new CrimeFragment();
    fragment.setArguments(args);
    return fragment;
}

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
    mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
}
```

## Using ViewPager
Adding a **ViewPager** to the UI allows users to navigate between list items by swiping accross the screen to "page" forward or backward through the items.

1. Create a Layout file that includes a **ViewPager**
2. Instantiate the ViewPager in your Fragment/Activity
3. Set the ViewPager's adapter to a new instance of  **FragmentStatePagerAdapter**
```
// Find the ViewPager in your layout to be instantiated
mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);
// Retrieve the list of crimes
List<Crime> mCrimes = CrimeLab.get(this).getCrimes();
// To be able to Add and manage Fragments
FragmentManager fragmentManager = getSupportFragmentManager();
mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
    @Override
    public Fragment getItem(int position) {
        Crime crime = mCrimes.get(position);
        return CrimeFragment.newInstance(crime.getId());
    }

    @Override
    public int getCount() {
        return mCrimes.size();
    }
});
```

## File Storage
Basic file and directory methods in the Context class. These files are private. If you need to share or receive files with other applications (such as files like store pictures), you need to expose those files through a **ContentProvider**.: <br>
**File getFilesDir()**:
* Returns a handle to the directory for private application files <br>
**FileInputStream openFileInput(String name)**:
* Opens an existing file for input (relative to the files directory) <br>
**FileOutputStream openFileOutput(String name, int mode)**:
* Opens a file for output, possibly creating it (relative to the files directory) <br>
**File getDir(String name, int mode)**:
* Gets (and possibly creates) a subdirectory within the files directory <br>
**String[] fileList()**:
* Gets a list of file names in the main files directory, such as for use with **openFileInput(String)** <br>
**File getCacheDir()**:
* Returns a handle to a directory you can use specifically for storing cache files; you should take care to keep this directory tidy and use as little space as possible

## ContentProviders
**ContentProviders** allows you to expose content URIs to other applications. They can then download from or write to those content URIs. Either way, you are in control and always have the option to deny the reads or writes if chosen.  <br>
If all you need to do is receive a file from another application, it is overkill to implement a complete **ContentProvider**. Implement a **FileProvider** instead.

## FileProvider
1. Declare **FileProvider** as a **ContentProvider** hooked up to a specific *authority*. 
   * An *authority* is a location in which the files will be saved to. By hooking up **FileProvider** to an *authority*, you give other applications a target for their requests. 
   * By setting *exported* to false, you keep anyone from using your provider except you or anyone you grant permissions to.
   * By setting *grantUriPermissions* to true, you add the ability to grant other applications permissions to write to URIs on this authority, when you send them out in an intent.
```
// Inside of Manifest
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="com.bignerdranch.android.criminalintent.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
</provider>
```
2. Tell your **FileProvider** which files it is exposing. This configuration is done with an extra XML resource file. 
    * Create a new Android resource file and set the name to files.xml
    * Replace the contents with the following which basically states to "Map the root path of my private storage as crime_photos":
```
<paths>
    <files-path name="crime_photos" path=".">
</paths>
```
3. Hook up *files.xml* to your **FileProvider** by adding a meta-data tag in your AndroidManifest.xml
```
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="com.bignerdranch.android.criminalintent.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/files"/>
</provider>
```

**Example with Image Storage**
```
// Returns a File object that point to the right location
public File getPhotoFile(Context context, String fileName) {
    File filesDir = context.getFilesDir();
    return new File(filesDir, fileName);
}
// Camera Intent in Fragment
public View onCreateView(....) {
    // ------------ Initialize Variables -------------
    // Retrieve the Photo File
    File mPhotoFile = getPhotoFile(getActivity, "photo");
    // Create the intent to take an Image
    final Intent captureImage = new Intent
        (MediaStore.ACTION_IMAGE_CAPTURE);
    // Check whehter we can take photos
    boolean canTakePhoto = (mPhotoFile != null && 
        captureImage.resolveActivity(packageManger) != null);
    
    // Initialize Views
    mPhotoButton = (ImageButton) v.findViewById(R.id.camera);
    mPhotoButton.setEnabled(canTakePhoto);
    mPhotoButton.setOnClickListener((view) -> {
        // Translates your local filepath into a Uri the camera application can see.
        Uri uri = FileProvider.getUriForFile(getActivity(),
            "com.bignerdranch.android.criminalintent.fileprovider", mPhotoFile);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        List<ResolveInfo> cameraActivities = getActivity()
            .getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

        // Grant the camera app permission to every activity your camera Image intent can resolve to.
        for (ResolveInfo activity : cameraActivities) {
            getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        startActivityForResult(captureImage, REQUEST_PHOTO);
    });
}

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(resultCode != Activity.RESULT_OK) {
        return;
    }

    if(requestCode == REQUEST_PHOTO) {
        Uri uri = FileProvider.getUriForFile(getActivity(), "com.bignerdranch.android.criminalintent.fileprovider", mPhotoFile);

        getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        updatePhotoView();
    }
}
```
Make sure to Add in the Manifest
```
<uses-feature android:name="android.hardware.camera"
                android:required="false"/>
```

## Showing Image to the User the RIGHT WAY
A **Bitmap** is a simble object that stores literal pixel data. That means that even if the original file was compressed, there is no compression in the Bitmap itself. The right way is the following: 
1. Scan the file to see how big it is
2. Figure out how much you need to scale it by to fit in a given area
3. Reread the file to  create a scaled-down Bitmap object
```
public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1;
        if(srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWdith / destWidth;

            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSameSize = inSampleSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }
}
```

## Adding Layout Flexibility
1. Create a new XML file with multiple FrameLayout in the way and place that you want your Fragments to be expanded
2. Use an alias resource to show a different layout file depending on whether it is on a tablet or a phone
3. Create a ref.xml file in res/values/ with the following content:
```
<resources>
    <item name="activity_masterdetail" type="layout">@layout/activity_fragment</item>
</resources>
```
4. Right click res/values/ to create an alternative resource with the same name refs.xml in the same location as before, but in this case, select Smallest Screen Width under Available qualifiers and click the >> button to move it over to the right.
5. With the newly created file, write the follwing:
```
<resources>
    <item name="activity_masterdetail" type="layout">@layout/activity_twopane</item>
</resources>
```
For devices that are under a specified size, use activity_fragment.xml. For devices that are over a specified size, use activity_twopane.xml

## Localization
1. Right click on res/values
2. Select New -> Values
3. Enter strings for the File Name
4. Leave the Source set option set to main
5. Make sure that Directory name is set to values
6. Select Locale in the Available Qualifiers list
7. Click the >> button to move Locale to the Chosen qualifiers section
8. Select es:Spanish in the Language list
9. Private alternative texts in the newly created file
10. Double check using Translation Editor by right clicking on one of the strings.xml and selecting Open Translation Editor.


## Data Binding
1. Add the following to the app/build.gradle file
```
    versionCode 1
    versionName "1.0"
    testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
}
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'),
                'proguard-rules.pro'
        }
    }
    dataBinding {
        enabled = true
    }
}
dependencies {
```
2. To use Data Binding within a particular layout file, change it into a data binding layout file by wrapping the entire XML file in a ``<layout>`` tag.
```
<layout
    xmlns:android="http://schemas.android.com/apk/res/android">
    <android.support.v7.widget.RecyclerView
        android:id="@+id/recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</layout>
```
3. The above code will generate a *binding class* for you. By default, this class is named after your layout file.
4. Now a ...Binding class should have been created
5. Instead of inflating a view hierarchy with a LayoutInflater, you will inflate an instance of ...Binding.
    * ...Binding will hold on to the view hierarchy for you in a getter called **getRoot()**. 
    * It also holds on to named references for each view you tagged with an android:id in your layout file.
6. Use **DataBindingUtil** to inflate an instance of **...Binding**
```
// R.layout.fragment_beat_box is the layout file for name for the binding that was created
...Binding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_beat_box, container, false);
// You can now directly modify your objects instead of through findViewById(..)
binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
return binding.getRoot();
```

## Binding to Data
With Data Binding, you can declare data objects within your layout file:
```
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <data>
        <variable
            name="crime"
            type="com.bignerranch.android.criminalintent.Crime"/>
    </data>

    <CheckBox
        android:id="@+id/list_item_crime_solved_check_box"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentRight="true"
        android:checked="@{crime.isSolved()}"
        android:padding="4dp"/>
</layout>
```

## MVVM
Model:
* The data
View:
* Wiring widgets up with that data will be handled directly in the layout file using data binding to a View Model
ViewModel:
* Formats data for display
Activity/Fragment:
* Will be in charge of things like initialize the binding and the view model and creating the link between the two.

**Look at MVVM Example Android Project**


## Assets Importing for Raw Data
Define these in cases where you only access files programmatically.
1. Create an assets folder inside your project by right-clicking on your app module and selecting New --> Folder --> Assets folder.
2. Leave the Change Folder Location checkbox unchecked and leave the Target Source Set set to main
3. Right-click on assets to create a subfolder for your sounds by selecting New --> Directory. Enter the Name of your directory such as "sounds"
4. Pour your items into the newly created sub-directory
5. Retrieve assets in the following manner
```
Assetmanager assetManager = context.getAssets();
// To get a listing of assets
String[] soundNames = assetManager.list(SOUNDS_FOLDER);
```