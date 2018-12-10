# RxAndroid
RxAndroid is an extension to RxJava built just for Android. It included special bindings that will make your life easier.

## Link
https://github.com/ReactiveX/RxAndroid 

## Android Schedulers
Schedulers that are ready-made for Android's threading system. For example, do you need to run some code on the UI thread? Just use AndroidSchedulers.mainThread()
```
retrofitService.getImage(url)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(bitmap -> myImageView.setImageBitmap(bitmap));
```
If you have your own Handler, you can create a scheduler linked to it with **HandlerThreadScheduler**.

## Android Observable
Provides more facilities for working within the Android lifecycle.
* bindActivity() and bindFragment(), in addition to automatically using AndroidSchedulers.mainThread() for observing, will also stop emitting items when your Activity or Fragment is finishing.
```
AndroidObservable.bindActivity(this, retrofitService.getImage(url))
    .subscribeOn(Schedulers.io())
    .subscribe(bitmap -> myImageView.setImageBitmap(bitmap));
```
## Android Observable Broadcast
Allows you to create an Observable that works like a BroadcastReceiver. The following shows a way to be notified whenever network connectivity changes:
```
IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
AndroidObservable.fromBroadcast(context, filter)
    .subscribe(intent -> handleConnectivityChange(intent));
```
## Android View Observables
Finally, there is ViewObservable, which adds a couple bindings for Views. There's ViewObservable.clicks() if you want to get an event each time a View is clicked, or ViewObservable.text() to observe whenever a TextView changes its content.
```
ViewObservable.clicks(mCardNameEditText, false)
    .subscribe(view -> handleClick(view));
```
## Retrofit and Observables
Normally when you define an asynchronous method you add a Callback
```
@GET("/user/{id}/photo")
void getUserPhoto(@Path("id") int id, Callback<Photo> cb);
```
With RxJava installed, you can have it return an Observable instead. Now you can hook into the Observable any way you want; not only will you get yoru data, but yo ucan transform it too! 
```
@GET("/user/{id}/photo")
Observable<Photo> getUserPhoto(@Path("id") int id);
```
Retrofit support for Observable also makes it easy to combine multiple REST calls together. For example, suppose we have one call that gets the photo and a second that gets the metadata. We can zip the results together
```
Observable.zip(
    service.getUserPhoto(id),
    service.getPhotoMetadata(id),
    (photo, metadata) -> createPhotoWithData(photo, metadata))
    .subscribe(photoWithData -> showPhoto(photoWithData));
```
## Converting Code to Observables
```
// Observable.just() and Observable.from() will suffice for creating an Observable from code most of the time
private Object oldMethod() { ... }
public Observable<Object> newMethod() {
    return Observable.just(oldMethod());
}

// If old method is slow, wrap the slower part with defer() which forces the Observable to not return slowBlockingMethod() until you subscribe to it
private Object slowBlockingMethod() {...}
public Observable<Object> newMethod() {
    return Observable.defer(() -> Observable.just(slowBlockingMethod()));
}
```

## Guidelines
1. **When the activity is restarted such as if the user rotates the screen**
```
Observable<Photo> request = service.getUserPhoto(id).cache();
Subscription sub = request.subscribe(photo -> handleUserPhoto(photo));

// ...When the Activity is being recreated...
sub.unsubscribe();

// ...Once the Activity is recreated...
request.subscribe(photo -> handleUserPhoto(photo));
```
Note that we're using the same cached request in both cases; that way the underlying call only happens once. Where you store request I leave up to you, but like all lifecycle solutions, it must be stored somewhere outside the lifecycle (a retained fragment, a singleton, etc).

2. The second problem can be solved by properly unsubscribing from your subscriptions in accordance with the lifecycle. It's a common pattern to use a CompositeSubscription to hold all of your Subscriptions, and then unsubscribe all at once in onDestroy() or onDestroyView():
```
private CompositeSubscription mCompositeSubscription
    = new CompositeSubscription();

private void doSomething() {
    mCompositeSubscription.add(
		AndroidObservable.bindActivity(this, Observable.just("Hello, World!"))
        .subscribe(s -> System.out.println(s)));
}

@Override
protected void onDestroy() {
    super.onDestroy();
    
    mCompositeSubscription.unsubscribe();
}
```
For bonus points you can create a root Activity/Fragment that comes with a CompositeSubscription that you can add to and is later automatically unsubscribed.

A warning! Once you call CompositeSubscription.unsubscribe() the object is unusable, as it will automatically unsubscribe anything you add to it afterwards! You must create a new CompositeSubscription as a replacement if you plan on re-using this pattern later.