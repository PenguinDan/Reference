# MediaPlayer 

## States
**Idle State**: <br>
* When a MediaPlayer object is just created using **new** or after **reset()** is called

**End State**: <br>
* After **release()** is called, it is in the *End* state.

## Best Practices
1. It is a programming error to call the following methods in the *Idle* state:
  * **getCurrentPosition()**
  * **getDuration()**
  * **getVideoHeight()**
  * **getVideoWidth()**
  * **setAudioAttributes(AudioAttributes)**
  * **setLooping(boolean)**
  * **setVolume(float, float)**
  * **pause()**
  * **start()**
  * **stop()**
  * **seekTo(long, int)**
  * **prepare()**
  * **prepareAsync()**

2. Once a *MediaPlayer* object is no longer being used, call **release()** immediately so that resourced used by the internal player engine associated with the *MediaPlayer* object can be released immediately.
3. A *MediaPlayer* object must first enter the *Prepared* state before palyback can be started.