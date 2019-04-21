# Looper

## What is it
Class used to run a message loop for a thread. Threads by default do not have a message loop associated with them; to create one, call prepare() in the thread that is to run the loop, and the loop() to have it process messages until the loop is stopped. 

Most interaction with a message loop is through the `Handler` class.

## Example
```
class LooperThread extends Thread {
    public Handler mHandler;

    public void run() {
        Looper.prepare();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                // Process incoming messages here
            }
        }

        Looper.loop();
    }
}
```