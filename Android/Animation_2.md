## Start an Activity Using an Animation
You can specify custom animations for enter and exit transitions and for transitions of shared elements between activities.
* **Enter Transitions**: Determines how views in an activity enter the scene. For example, in the *explode* enter transition, the views enter the scene from the outside and fly towards the center of the screen.
* **Exit Transition**: Determines how views in an activity exit the scene. For example, in the *explode* exit transition, the views exit the scene away from the center.
* **Shared Elements Transitions**: Determines how views that are shared between two activities transition between these activities. For example, if two activities have the same image in different positions and sizes, the **channelImageTransform** shared element transition translates and scales the image smoothly between these activities.
    * changeBounds: Animates the changes in layout bounds of target views.
    * changeClipBounds: Animates the changes in clip bounds of target views
    * changeTransform: Animates the changes in scale and roatation of target views
    * changeImageTransform: Animates changes in size and scale of target images


### Supported Enter and Exit Transitions
**Explode**: Moves views in or out from the center of the scene <br>
**Slide**: Moves views in or out from one of the edges of the scene <br>
**Fade**: Adds or removes a view from the scene by changing its opacity <br>


### Specifying Custom Transitions
1. Through XML: <br>
Enable window content transitions with the ```android:windowActivityTransitions``` attribute when you define a style that inherits from material theme.
```
<style name="BaseAppTheme" parent="android:Theme.Material">
  <!-- enable window content transitions -->
  <item name="android:windowActivityTransitions">true</item>

  <!-- specify enter and exit transitions -->
  <item name="android:windowEnterTransition">@transition/explode</item>
  <item name="android:windowExitTransition">@transition/explode</item>

  <!-- specify shared element transitions -->
  <item name="android:windowSharedElementEnterTransition">
    @transition/change_image_transform</item>
  <item name="android:windowSharedElementExitTransition">
    @transition/change_image_transform</item>
</style>

```
2. Programatically: <br>
```
// Enable window content transitions
getWindow().requestFeatures(Window.FEATURE_CONTENT_TRANSITIONS);

// Set an exit transition
getWindow().setExitTransition(new Explode());
```
To get the full effect of a transition, you msut enable window content transitions on both the calling and called activities. To start an enter transition as soon as possible, use the **Window.setAllowEnterTransitionOverlap()** function on the called activity. This lets you have a more dramatic enter transitions.

## Start an Activity Using Transitions
If you enable transitions and set an exit transition for an activity, the transition is activated when you launch another activity as follows:
```
startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
```
