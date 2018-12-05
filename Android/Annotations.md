# Annotations
Annotations allow you to provide hints to code inspection tools like Lint, to help detect these more subtle code problems. They are added as metadata tags that you attach to variables, parameters, and return values to inspect method return values, passed parameters, local variables, and fields.

## Adding Annotations to the Project
```
dependencies {
    implementation 'com.android.support:support-annotations:28.0.0
}
```

## Run code inspections
Select **Analyze > Inspect Code** from the menu bar. Android Studio displays conflict messages to flag potential problems where your code conflicts with annotations and to suggest possible resolutions.

## Nullness Annotations
Add @Nullable and @NonNull annotatiosn to check the nullness of a given variable, parameter, or return value. 

## Resource Annotations
Validating resource types can be useful because Android references to resources, such as drawable and string, are passed as integers. 
```
public abstract void setTitle(@StringRes int resId)
```

## Thread Annotations
* @MainThread
* @UiThread
* @WorkerThread
* @BinderThread
* @AnyThread

And MORE