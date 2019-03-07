# Exoplayer

## ExoPlayer Components
1. ExoPlayer: <br>
Exposes high level media player functionality. Uses many components to achieve modularity and customizability. It delegates work to components which have to be injected when we create and prepare an ExoPlayer instance. **It is important to keep in mind that we have to release the player when it's no longer needed which can be done by calling ExoPlayer.release**.
2. TrackSelector: <br>
Selects tracks by the **MediaSource** object to be consumed by each of the available **Renderers** objects.
3. MediaSource: <br>
Defines and provides media to be played by an **ExoPlayer** object. **Note that MediaSource** instances should not be re-used, meaning they should be passed only once. The way of getting a MediaSource instance depends on what kind of data you want to play for example:
* **ExtractorMediaSource** for regular media files
* **DashMediaSource** for DASH
* **SsMediaSource** for SmoothStreaming
* **HlsMediaSource** for HLS
4. DataSource: <br>
Exactly as its name states, it is the data source for a **MediaSource** object. It tells ExoPlayer how to load data using a particular HTTP stack or loading files from the local device.
5. Renderer: <br>
Its responsibility is to render individual components of the media. The library provides default implementations for common media types such as:
* MediaCodecVideoRenderer
* MediaCodecAudioRenderer
* TextRenderer
* MetadataRenderer <br>
It consumes media from the **MediaSource** object being played. 
6. LoadController: <br>
Controls when a **MediaSource** should buffer more media and how much it should buffer. 
7. PlayerView: <br>
Displays the video or audio with controls.

## TrackSelector
**What does it do?**
Responsible for selecting tracks to be consumed by each of the player's Renderers. 

**Customizing TrackSelector**
```
DefaultTrackSelector trackSelector = new DefaultTrackSelector(...);

Parameters parameters = trackSelector.getParameters()
    // Sets the maximum allowed video width and height. The default value is Integer.MAX_VALUE as in no constraint, 
    // this is important because you know that someone's phone isn't that big, you might want to shrink down the amount
    // of data you consume to get a lower-quality video
    .withMaxVideoSize(720, 480);
    // The preffered audio language for the case that there are multiple media tracks readily available/exposed
    .withPrefferedAudioLanguage("de");

// More Parameters at https://google.github.io/ExoPlayer/doc/reference/com/google/android/exoplayer2/trackselection/DefaultTrackSelector.Parameters.html

trackSelector.setParameters(parameters);

SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
    new DefaultRenderersFactory(...),
    trackSelector,
    new DefaultLoadControl(...)
);
```

```
// For further customization
TrackSelector trackSelector = new TrackSelector() {
    @Override
    public TrackSelectorResult selectTracks(RendererCapabilities[] rendererCapabilities, TrackGroupArray trackGroups) 
        throws ExoPlaybackException{
            // IMPLEMENTATION HERE
        }
};

SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
    new DefaultRenderersFactory(...),
    trackSelector,
    new DefaultLoadControl(...)
);
```


## MediaSource

**Loading in MP3**
```
MediaSource mediaSource = new ExtractorMediaSource.Factory(DataSource.Factory dataSourceFactory).createMediaSource(Uri mp3Uri);
```
**Sending in an Authorization Header for every request**
```
HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSourceFactory(...);

// Set a header for HTTP requests
httpDataSourceFactory.getDefaultRequestProperties().set("Authorization", "Basic aldkfjalkjf;aljf==");

MediaSource mediaSource = new ExtractorMediaSource(
    videoUri,
    httpDataSourceFactory,
    ...
);
```
**Caching videos for cases such as users repeatedly rewinding videos**
```
// Instantiate a cache (usually a singleton)
Cache cache = new SimpleCache(
    cacheDir,
    new LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE)
);

HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSourceFactory(...);

// Use the Cache!
DataSource.Factory cacheDataSourceFactory = new CacheDataSourceFactory(cache, httpDataSourceFactory);

MediaSource mediaSource = new ExtractorMediaSource(
    videoUri,
    httpDataSourceFactory,
    ...
);
```
**Avoid multiple buffering with Ads with AdsMediaSource**
```
MediaSource contentMediaSource = ...;

// Insert Ads!
MediaSource mediaSource = new ImaAdsMediaSource(
    contentMediaSource,
    new DefaultDataSourceFactory(...),
    context,
    adTagUri,
    // A skip button for example
    overlayViewGroup
);
```

## DataSource
**DataSource object for a Simple MP3 MediaSource**
```
DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(this, "yourApplicationName"));
```


## Renderer



## LoadController
**What does it do?**
* In charge of telling the MediaSource when to start and stop buffering
* In charge of starting and stopping the video playback

**Customizing LoadController**
```
LoadControl loadControl = new DefaultLoadControl(
    allocator,
    // Customize the minimum buffer duration in terms of playback to be stored (stores 15 seconds of video)
    15000,
    // Customize the maximum buffer duration in terms of playback to be stored (stores 30 seconds of video)
    30000,
    // Customize the minimum buffer required to start playback
    2500,
    // Customize the minimum buffer required to resume playback
    5000
);

SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
    new DefaultRenderersFactory(...),
    new DefaultTrackSelector(...),
    loadControl
);
```
If the bound between minimum buffer duration and maximum buffer duration, is kept close, it means that we want to keep the buffer relatively full most of the time.

```
// For further LoadController customization
LoadControl loadControl = new LoadControl() {
    @Override
    public boolean shouldContinueLoading(long bufferedUs) {
        // Implementation here
    }
    ...
}

SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
    new DefaultRenderersFactory(...),
    new DefaultTrackSelector(...),
    loadControl
);
```

## Example Code

**Video playback** <br>
Dependencies: 
* implementation 'com.google.android.exoplayer:exoplayer-core:2.x.x'
* implementation 'com.google.android.exoplayer:exoplayer-ui:2.x.x'
```
private PlayerView playerView;
private SimpleExoPlayer player;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    playerView = findViewById(R.layout.main_activity);
}

@Override
protected void onStart() {
    super.onStart();

    // Step 1) Instantiate a SimpleExoPlayer instance and attach it to the
    // PlayerView
    player = ExoPlayerFactory.newSimpleInstance(
        this,
        new DefaultTrackSelector()
    );
    playerView.setPlayer(player);

    // Step 2) First part of telling ExoPlayer what to load
    // Tell ExoPlayer what to play, in this instance, we are using a default 
    // DataSource. It provides loading for HTTP URLs, files from the local disk, and
    // assets loaded from the APK
    DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
        this,
        Util.getUserAgent(this, "applicationName)
    );

    // Step 3) Second part of telling ExoPlayer what to load
    // Specify a MediaSource, which depends on what type of media we are trying to play
    // In this example, we're going to be playing an MP4 so we are going to use an 
    // ExtractorMediaSource which supports formats such as MP4, MP3, Matroska, and so 
    // on. If you wanted to use DASH, SmoothStreaming, or HLS, you would have to use
    // the appropriate MediaSource that supports the format
    ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
        .createMediaSource(Samples.MP4_URI);
    
    // Step 4) Prepare the player to start buffering the data. We set the method
    // .setPlayWhenReady to tell the player to start playing once we have buffered
    // enough data based on the default values
    player.prepare(mediaSource);
    player.setPlayWhenReady(true);
}

@Override
protected void onStop() {
    super.onStop();

    // Clear references
    playerView.setPlayer(null);
    player.release();
    player = null;
}
```

**Adding ads on Videos** <br>
Dependencies: 
* implementation 'com.google.android.exoplayer:exoplayer-core:2.x.x'
* implementation 'com.google.android.exoplayer:exoplayer-ui:2.x.x'
* implementation 'com.google.android.exoplayer:extension-ima:2.x.x'
```
private PlayerView playerView;
private SimpleExoPlayer player;
private ImaAdsLoader adsLoader;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    playerView = findViewById(R.layout.main_activity);

    // Step 2) Create the Ads loader to load in ads
    // Samples.AD_TAG_URI is an XML document specifying where/when to play the
    // ads in our content
    adsLoader = new ImaAdsLoader(
        this,
        Samples.AD_TAG_URI
    );
}

@Override
protected void onStart() {
    super.onStart();

    // Step 3) Instantiate a SimpleExoPlayer instance and attach it to the
    // PlayerView
    player = ExoPlayerFactory.newSimpleInstance(
        this,
        new DefaultTrackSelector()
    );
    playerView.setPlayer(player);

    // Step 4) First part of telling ExoPlayer what to load
    // Tell ExoPlayer what to play, in this instance, we are using a default 
    // DataSource. It provides loading for HTTP URLs, files from the local disk, and
    // assets loaded from the APK
    DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
        this,
        Util.getUserAgent(this, "applicationName)
    );

    // Step 5) Second part of telling ExoPlayer what to load
    // Specify a MediaSource, which depends on what type of media we are trying to play
    // In this example, we're going to be playing an MP4 so we are going to use an 
    // ExtractorMediaSource which supports formats such as MP4, MP3, Matroska, and so 
    // on. If you wanted to use DASH, SmoothStreaming, or HLS, you would have to use
    // the appropriate MediaSource that supports the format
    ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
        .createMediaSource(Samples.MP4_URI);

    // Step 6) Since MediaSource objects are how we tell ExoPlayer what to play
    // this is where we load in the MediaSource to play the ads
    AdsMediaSource adsMediaSource = new AdsMediaSource(
        // The content MediaSource that contains the videos we are going to play
        mediaSource,
        // Used to load data to play the ads
        dataSourceFactory,
        adsLoader,
        // Any overlay UI used to show Views such as a skip ad button
        playerView.getOverlayFrameLayout()
    )
    
    // Step 7) Prepare the player to start buffering the data. We set the method
    // .setPlayWhenReady to tell the player to start playing once we have buffered
    // enough data based on the default values
    player.prepare(adsMediaSource);
    player.setPlayWhenReady(true);
}

@Override
protected void onStop() {
    super.onStop();

    // Clear references
    playerView.setPlayer(null);
    player.release();
    player = null;
}

@Override
protected void onDestroy() {
    super.onDestroy();

    // Clear references
    // We release the ImaAdsLoader object in onDestroy() because the object contains
    // information that we might need if the user comes back to the app, information 
    // such as which ads have already played
    adsLoader.release();
}
```

**Audio Playback with a Playlist**
```
// Register service in the Manifest
<service android:name=".AudioPlayerService"/>

// Uses a foreground service to notify the user that something is still going on
// even though this application moves to the background
public class AudioPlayerService extends Service {
    private SimpleExoPlayer player;
    private PlayerNotificationManager playerNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context = this;

        player = ExoPlayerFactory.newSimpleInstance(
            context,
            new DefaultTrackSelector()
        );
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "ApplicationName")
        );

        // Joins together a number of media sources to not face hitches and also
        // allows us to dynamically change/move media sources while the player is playing
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();

        for(Samples.Sample sample : SAMPLES) {
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(sample.uri);
            concatenatingMediaSource.addMediaSource(mediaSource);
        }

        player.prepare(concatenatingMediaSource);
        player.setWhenReady(true);

        // Will not only create the notification, but it will also keep it in sync
        // with the player, meaning, each time the player state changes, the manager
        // will post the notification
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            // Identifies are channel in which we post our notification
            PLAYBACK_CHANNEL_ID,
            // A string identifier that localized name for the channel which shows up
            // in the settings if the user wants to mute our notification
            R.string.playback_channel_name,
            // Identify notifcations
            PLAYBACK_NOTIFICATION_ID,
            // The manager will use the Adapter to get information about the currently
            // playing item
            new PlayerNotificationManager.MediaDescriptionAdapter() {

                // The title of the audio
                @Override
                public String getCurrentContentTitle(Player player) {
                    return SAMPLES[player.getCurrentWindowIndex()].title;
                }

                // What should happen if the user taps on our notification, in this
                // example, the user should return to our activity
                @Nullable
                @Override
                public PendingIntent createCurrentContentIntent(Player player) {
                    Intent intent = new Intent(context, MainActivity.class);
                    return new PendingIntent.getActivity(
                        context, 
                        // No extra flags since we won't use it
                        0, 
                        intent, 
                        // Update all pending intents or ours if none others
                        PendingIntent.FLAG_UPDATE_CURRENT);
                }

                // Return the description of the audio
                @Nullable
                @Override
                public String getCurrentContentText(Player player) {
                    return SAMPLES[player.getCurrentWindowIndex()].description;
                }

                // A bitmap for the audio to make the notification "nicer" haha
                @Nullable
                @Override
                public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                    return Samples.getBitmap(context, SAMPLE[player.getCurrentWindowIndex()].bitmapResource);
                }
            }
        );

        // Register a notification listener which allows us to be aware about the
        // lifecycle of the notification
        playerNotificationManager.setNotificationListener(new NotifcationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                // We are now officially a foreground service, meaning the system
                // will not kill our service anymore
                startForeground(notificationId, notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                // Stop our audio
                stopSelf();
            }
        });
        // So that the notification manager can sync its state with the player
        playerNotificationManager.setPlayer(player);
    }

    @Override
    public void onDestroy() {
        playerNotificationmanager.setPlayer(null);
        player.release();
        player = null;

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    // Return START_STICKY so the service is not immediately destroyed
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
}

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Intent pointing to our audio playing service that contains the static
        // .class method for retrieving a specific service
        Intent intent = new Intent(this, AudioPlayerService.class);
        // Start the service in the foreground
        Util.startForegroundService(this, intent);

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(
            new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emptyList())
        );
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
```

**Voice Assisted Stop and Start**
```
// Register service in the Manifest
<service android:name=".AudioPlayerService"/>

// Uses a foreground service to notify the user that something is still going on
// even though this application moves to the background
public class AudioPlayerService extends Service {
    private SimpleExoPlayer player;
    private PlayerNotificationManager playerNotificationManager;
    private MediaSessionCompat mediaSession;
    private MediaSessionConntector mediaSessionConnector;

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context = this;

        player = ExoPlayerFactory.newSimpleInstance(
            context,
            new DefaultTrackSelector()
        );
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "ApplicationName")
        );

        // Joins together a number of media sources to not face hitches and also
        // allows us to dynamically change/move media sources while the player is playing
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();

        for(Samples.Sample sample : SAMPLES) {
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(sample.uri);
            concatenatingMediaSource.addMediaSource(mediaSource);
        }

        player.prepare(concatenatingMediaSource);
        player.setWhenReady(true);

        // Will not only create the notification, but it will also keep it in sync
        // with the player, meaning, each time the player state changes, the manager
        // will post the notification
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            // Identifies are channel in which we post our notification
            PLAYBACK_CHANNEL_ID,
            // A string identifier that localized name for the channel which shows up
            // in the settings if the user wants to mute our notification
            R.string.playback_channel_name,
            // Identify notifcations
            PLAYBACK_NOTIFICATION_ID,
            // The manager will use the Adapter to get information about the currently
            // playing item
            new PlayerNotificationManager.MediaDescriptionAdapter() {

                // The title of the audio
                @Override
                public String getCurrentContentTitle(Player player) {
                    return SAMPLES[player.getCurrentWindowIndex()].title;
                }

                // What should happen if the user taps on our notification, in this
                // example, the user should return to our activity
                @Nullable
                @Override
                public PendingIntent createCurrentContentIntent(Player player) {
                    Intent intent = new Intent(context, MainActivity.class);
                    return new PendingIntent.getActivity(
                        context, 
                        // No extra flags since we won't use it
                        0, 
                        intent, 
                        // Update all pending intents or ours if none others
                        PendingIntent.FLAG_UPDATE_CURRENT);
                }

                // Return the description of the audio
                @Nullable
                @Override
                public String getCurrentContentText(Player player) {
                    return SAMPLES[player.getCurrentWindowIndex()].description;
                }

                // A bitmap for the audio to make the notification "nicer" haha
                @Nullable
                @Override
                public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                    return Samples.getBitmap(context, SAMPLE[player.getCurrentWindowIndex()].bitmapResource);
                }
            }
        );

        // Register a notification listener which allows us to be aware about the
        // lifecycle of the notification
        playerNotificationManager.setNotificationListener(new NotifcationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                // We are now officially a foreground service, meaning the system
                // will not kill our service anymore
                startForeground(notificationId, notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                // Stop our audio
                stopSelf();
            }
        });
        // So that the notification manager can sync its state with the player
        playerNotificationManager.setPlayer(player);

        // Make the media session now active
        mediaSession = new MediaSessionCompat(context, MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        // Make the playerNotificationManager aware of our session so that we can
        // provide an artwork for the lock screen on the device
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());
        // Give the mediaSessionConnect access to our session
        mediaSessionConnector = new MediaSessionConnector(mediaSession);

        // Synchronize our playlist with the queue of the media session so external
        // applications know what items we have in our playlist.
        // We use a TimeLineQueueNavigator, the timeline is a representation of the playlist
        // after the playlist has been declared. We have as many windows in our timeline
        // as the number of items in our playlist
        mediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
                // Look at the Samples Class below
                return Samples.getMediaDescription(context, SAMPLES[windowIndex]);
            }
        });

        // Pass the player to the connect so that it can sync the player with the media session
        // The second parameter is null because we do not want an external application
        // to initiate our playback 
        mediaSessionConnector.setPlayer(player, null);
    }

    @Override
    public void onDestroy() {
        mediaSession.release();
        mediaSessionConnector.setPlayer(null, null);
        playerNotificationmanager.setPlayer(null);
        player.release();
        player = null;

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    // Return START_STICKY so the service is not immediately destroyed
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
}

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Intent pointing to our audio playing service that contains the static
        // .class method for retrieving a specific service
        Intent intent = new Intent(this, AudioPlayerService.class);
        // Start the service in the foreground
        Util.startForegroundService(this, intent);

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(
            new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emptyList())
        );
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}

public class Samples {
    ...

    public static MediaDescriptionCompat getMediaDescription(Context context, Sample sample) {
        Bundle extras = new Bundle();
        Bitmap bitmap = getBitmap(context, sample.bitmapResource);
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap);
        return new MediaDescriptionCompat.Builder()
            .setMediaId(sample.mediaId)
            .setIconBitmap(bitmap)
            .setTitle(sample.title)
            .setDescription(sample.description)
            .setExtras(extras)
            .build();
    }

    public static Bitmap getBitmap(Context context, @DrawableRes int bitmapResource) {
        return ((BitmapDrawable) context.getResources().getDrawable(bitmapResource)).getBitmap();
    }
}
```

**Downloading**
**Voice Assisted Stop and Start**
```
// Register service in the Manifest
<service android:name=".AudioPlayerService"/>
<service android:name=".AudioDownloadService"/>

// Uses a foreground service to notify the user that something is still going on
// even though this application moves to the background
public class AudioPlayerService extends Service {
    private SimpleExoPlayer player;
    private PlayerNotificationManager playerNotificationManager;
    private MediaSessionCompat mediaSession;
    private MediaSessionConntector mediaSessionConnector;

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context = this;

        player = ExoPlayerFactory.newSimpleInstance(
            context,
            new DefaultTrackSelector()
        );
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "ApplicationName")
        );

        // Add support for Caching
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(
            DownloadUtil.getCache(this), 
            // Pass in the upstream data source factory for loading data from the network
            dataSourceFactory
        );

        // Joins together a number of media sources to not face hitches and also
        // allows us to dynamically change/move media sources while the player is playing
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();

        for(Samples.Sample sample : SAMPLES) {
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(sample.uri);
            concatenatingMediaSource.addMediaSource(mediaSource);
        }

        player.prepare(concatenatingMediaSource);
        player.setWhenReady(true);

        // Will not only create the notification, but it will also keep it in sync
        // with the player, meaning, each time the player state changes, the manager
        // will post the notification
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            // Identifies are channel in which we post our notification
            PLAYBACK_CHANNEL_ID,
            // A string identifier that localized name for the channel which shows up
            // in the settings if the user wants to mute our notification
            R.string.playback_channel_name,
            // Identify notifcations
            PLAYBACK_NOTIFICATION_ID,
            // The manager will use the Adapter to get information about the currently
            // playing item
            new PlayerNotificationManager.MediaDescriptionAdapter() {

                // The title of the audio
                @Override
                public String getCurrentContentTitle(Player player) {
                    return SAMPLES[player.getCurrentWindowIndex()].title;
                }

                // What should happen if the user taps on our notification, in this
                // example, the user should return to our activity
                @Nullable
                @Override
                public PendingIntent createCurrentContentIntent(Player player) {
                    Intent intent = new Intent(context, MainActivity.class);
                    return new PendingIntent.getActivity(
                        context, 
                        // No extra flags since we won't use it
                        0, 
                        intent, 
                        // Update all pending intents or ours if none others
                        PendingIntent.FLAG_UPDATE_CURRENT);
                }

                // Return the description of the audio
                @Nullable
                @Override
                public String getCurrentContentText(Player player) {
                    return SAMPLES[player.getCurrentWindowIndex()].description;
                }

                // A bitmap for the audio to make the notification "nicer" haha
                @Nullable
                @Override
                public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                    return Samples.getBitmap(context, SAMPLE[player.getCurrentWindowIndex()].bitmapResource);
                }
            }
        );

        // Register a notification listener which allows us to be aware about the
        // lifecycle of the notification
        playerNotificationManager.setNotificationListener(new NotifcationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                // We are now officially a foreground service, meaning the system
                // will not kill our service anymore
                startForeground(notificationId, notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                // Stop our audio
                stopSelf();
            }
        });
        // So that the notification manager can sync its state with the player
        playerNotificationManager.setPlayer(player);

        // Make the media session now active
        mediaSession = new MediaSessionCompat(context, MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        // Make the playerNotificationManager aware of our session so that we can
        // provide an artwork for the lock screen on the device
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());
        // Give the mediaSessionConnect access to our session
        mediaSessionConnector = new MediaSessionConnector(mediaSession);

        // Synchronize our playlist with the queue of the media session so external
        // applications know what items we have in our playlist.
        // We use a TimeLineQueueNavigator, the timeline is a representation of the playlist
        // after the playlist has been declared. We have as many windows in our timeline
        // as the number of items in our playlist
        mediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
                // Look at the Samples Class below
                return Samples.getMediaDescription(context, SAMPLES[windowIndex]);
            }
        });

        // Pass the player to the connect so that it can sync the player with the media session
        // The second parameter is null because we do not want an external application
        // to initiate our playback 
        mediaSessionConnector.setPlayer(player, null);
    }

    @Override
    public void onDestroy() {
        mediaSession.release();
        mediaSessionConnector.setPlayer(null, null);
        playerNotificationmanager.setPlayer(null);
        player.release();
        player = null;

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    // Return START_STICKY so the service is not immediately destroyed
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
}

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Intent pointing to our audio playing service that contains the static
        // .class method for retrieving a specific service
        Intent intent = new Intent(this, AudioPlayerService.class);
        // Start the service in the foreground
        Util.startForegroundService(this, intent);

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(
            new ArrayAdapter<>(
                this, 
                android.R.layout.simple_list_item_1, 
                Samples.SAMPLES
            )
        );
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // We are using the ProgressiveDownloadAction, but if you were downloading
                // a different stream such as DASH, then we would use a different
                // download action
                ProgressiveDownloadAction progressiveDownloadAction = new ProgressiveDownloadAction(
                    Samples.SAMPLES[position].uri,
                    // Do not want to remove from cache
                    false,
                    // No association with any custom data
                    null,
                    null
                );
                DownloadService.startWithAction(
                    MainActivity.this,
                    AudioDownloadService.class,
                    progressiveDownloadAction,
                    false
                );
            }
        });
    }
}

public class Samples {
    ...

    public static MediaDescriptionCompat getMediaDescription(Context context, Sample sample) {
        Bundle extras = new Bundle();
        Bitmap bitmap = getBitmap(context, sample.bitmapResource);
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap);
        return new MediaDescriptionCompat.Builder()
            .setMediaId(sample.mediaId)
            .setIconBitmap(bitmap)
            .setTitle(sample.title)
            .setDescription(sample.description)
            .setExtras(extras)
            .build();
    }

    public static Bitmap getBitmap(Context context, @DrawableRes int bitmapResource) {
        return ((BitmapDrawable) context.getResources().getDrawable(bitmapResource)).getBitmap();
    }
}

public class DownloadUtil {
    private static Cache chache;
    private Static DownloadManager downloadManager;

    public static synchronized Cache getCache(Context context) {
        if(cache == null) {
            File cacheDirectory = new File(context.getExternalFilesDir(null), "downloads")
            cache = new SimpleCache(
                cacheDirectory, 
                // Won't clean up the cache 
                new NoOpCacheEvictor()
            );
        }
        return cache;
    }

    public static synchronized DownloadManager getDownloadManager(Context context) {
        if(downloadManager == null) {
            File actionFile = new File(context.getExternalCacheDir(), "actions");
            downloadManager = new DownloadManager(
                getCache(context),
                new DefaultDataSourceFactory(context, Util.getUserAgent(context, "AppName")),
                actionFile,
                ProgressiveDownloadAction.DESERIZALIZER
            );
        }

        return downloadManager;
    }
}

public class AudioDownloadService extends DownloadService {
    public static final int DOWNLOAD_NOTIFICATION_ID = 3001;

    public AudioDownloadService() {
        super(
            // Must be different then the notification id used for playback
            DOWNLOAD_NOTIFICATION_ID,
            // Default interval in which to update the notification
            DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
            // Pass in a channel id for a download notification channel
            DOWNLOAD_CHANNEL_ID,
            // String resource that idenfities our channel
            R.string.download_channel_name
        );
    }

    // Provide a download manager
    @Override
    protected DownloadManager getDownloadManager() {
        return DownloadUtil.getDownloadManager(this);
    }

    // Provide a JobScheduler so that the system can start your download service when
    // your process ins't running in order to resume downloads which can be found
    // in the ExoPlayer full download demo
    @Nullable
    @Override
    protected Scheduler getScheduler() {
        return null;
    }

    // Icon associated with the download in progress
    @Override
    protected Notification getForegroundNotification(DownloadManager.TaskState[] taskStates) {
        return DownloadNotificationUtil.buildProgressNotification(
            this,
            R.drawable.exo_icon_play,
            DOWNLOAD_CHANNEL_ID,
            null,
            null,
            taskStates
        );
    }
}
```

Code Branch : github.com/google/ExoPlayer/tree/io18