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
Exactly as its name states, it is the data source for a **MediaSource** object.
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