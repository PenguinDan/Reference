# LifeCycle Components

## What is it
Lifecycle-aware components perform actions in response to a change in the lifecycle status of another component, such as Activities and Fragments.

## Implementation
**LifecycleObserver**
A class can monitor the component's lifecycle status by adding annotations to its methods. Then you can add an observer by calling the addObserver() method of the Lifecycle class and passing an instance of your observer as showing in the following example:
```
class MyObserver: LifecycleObserver {
    @OnLifecycleEvent(Lifecyle.Event.ON_RESUME)
    fun connectListener() {
        ...
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun disconnectListener() {
        ...
    }
}

myLifeCycleOwner.getLifecycle().addObserver(MyObserver())
```

**LifecycleOwner**
Interface that denotes that the class has a Lifecycle. It has one method, `getLifecycle()`, which must be implemented by the class. Components that implement `LifecycleObserver` work seamlessly with components that implement `LifecycleOwner` because an owner can provide a lifecycle, which an observer can register to watch.

## Examples

**Avoid callbacks if the Lifecycle isn't in a good state**: With the below implementation, our `LocationListener` is completely lifecycle-aware. All of the setup and teardown operations are managed by the class itself.
```
internal class MyLocationListener(
    private val context: Context,
    private val lifecycle: Lifecycle,
    private val callback: (Location) -> Unit
) : LifecycleObserver {
    private var enabled = false

    @OnLifecyleEvent(Lifecycle.Event.ON_START)
    fun start() {
        if(enabled) {
            // connect
        }
    }

    fun enable() {
        enabled = true
        if(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            // Connect if not connected
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        // Disconnect if connected
    }
}
```