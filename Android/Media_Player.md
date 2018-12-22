# MediaPlayer 

## States

**Idle State**: <br>
* When a MediaPlayer object is just created using **new** or after **reset()** is called
* It is a programming error to call the following methods in the *Idle* state:
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

**End State**: <br>
* After **release()** is called, it is in the *End* state.