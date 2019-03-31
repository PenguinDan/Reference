# Multi-Threading

## Definitions
Deferrable Work:
* If the work does not have to happen right away, it is deferrable

## Processes
By default, all components of the same application run in the same process and most applications should not change this. However, if you would like control over which process a certain component belongs to, this can be done in the manifest file
* Components include **\<activity>**, **\<service>**, **\<receiver>**, **\<provider>**. Each of these support an `android:process` attribute that can specify a process in which that component should run.
* A process might be shut down when memory is low and required by other processes that are more immediately serving the user.

## Threads
When an application is launchded, the system creates a thread of execution for the Application, called "main" or also known as the UI thread.
* DO NOT block the UI thread
* Do not access the Android UI toolkit from outside the UI thread
If work needs to be done that are not instantaneous, the work should be performed on a separate thread ("background" or "worker" threads).

*Accessing the UI thread from other Threads*
* **Activity.runOnUiThread(Runnable)**
* **View.post(Runnable)**
* **View.postDelayed(Runnable, long)**

This implementation is thread-safe: The background operation is done from a separate thread while the ImageView is always manipulated from the UI thread.
```
public void onClick(View view) {
    new Thread(new Runnable() {
        // A potentially time consuming task
        final Bitmap bitmap = processBitMap("image.png");
        imageView.post(new Runnable() {
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    });
}
```

**Using AsyncTask**: Allows you to perform asynchronous work on your user interface. It performs the blocking operations in a worker thread and then published the results on the UI Thread without requiring you to handle threads and/or handlers yourself.
* To use it, subclass **AsyncTask** and implement the **doInBackground()** callback method, which runs in a pool of background threads. 
* To update your UI, implement onPostExecute(), which delivers the result from **doInBackground()** and runs on the UI thread, so you can safely update yoru UI
* You can then run the task by calling **execute()** from the UI Thread.

**IBinder**: The methods implemented must be written to be Thread-Safe.  <br>
When a call on a mehtod implemented in an **IBinder** originates in the same process in which the **IBinder** is running, the method is executed in the caller's thread. For example, I called manipulateService() on my UI thread, it runs on my UI thread. However, if the call originates in a different process, the method is executed in a thread chosen from a pool of threads that the system maintains in teh same process as the **IBinder**.


## Android Multi-Threading Solutions
**WorkManager** : For work that is deferrable and expected to run even if the device or application restarts.
* Gracefully runs deferrable background work when the work's conditions such as network availability and power are satisfied
**Foreground Services** : For user-initiated work that need to be run immediately and must execute to completion
* Using a foreground service tells the system that the app is doing something important and it shouldn't be killed. Foreground services are visible to users via a non-dismissible notification in the notification tray
**AlarmManager** : If you need to run a job at a *precise* time
* AlarmManager launches your app if necessary to do the job at the time specified.
**DownloadManager** : If your app is performing long-running HTTP downloads.
* Clients may request that a URI be downloaded to a particular destination file that may be outside of the app process. The download manager will conduct the download in the background, taking care of HTTP interactions and retrying downloads after failures or across connectivity changes and system reboots.

## Using Runnable
Important
* Runnable won't be running on the UI thread, so it can't directly modify UI objects such as View objects
* At the beginning of the run() method, set the thread to use background priority by calling `Process.setThreadPriority()` with **THREAD_PRIORITY_BACKGROUND**. This approach reduces resource competition between the **Runnable** object's thread and the UI thread.
* You should also store a reference to the **Runnable** object's **Thread** in the **Runable** itself, by calling **Thread.currentThread()**.

Class Implementing Runnable
```
public class PhotoDecodeRunnable implements Runnable {
    @Override
    public void run() {
        // Code you want to run on the thread goes here
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        
        ...

        // Stores the current thread in the PhotoTask instance, so that the instance can interrupt the Thread
        photoTask.setImageDecodeThread(Thread.currentThread());
    }
}
```

## IntentService
If you need to run a task repeatedly on different sets of data, but you only need on execution running at a time, an IntentService suits your needs. 

## ThreadPoolExecutor
To automatically run tasks as resources become available, or to allow multiple tasks to run at the same time (or both), you need to provide a managed collection of threads. To do this, use an instance of **ThreadPoolExecutor**, which runs a task from a queue when a thread in its pool becomes free. To run a task, all you have to do is add it to the queue.

STEPS TO INSTANTIATING THREADPOOLEXECUTOR <br>

1. **Use static variables for thread pools**  
You may only want a single instance of thread pool for your app, in order to have a single control point of restricted CPU or network resources.  If you have different **Runnable** types, you may want to have a thread pool for each one, but each of these can be a single instance. 

2. **Use a Private Constructor**  
Making the constructor private ensures that it is a singleton, which means that you don't have to enclose accesses to the class in a **synchronized** block.


3. **Start your tasks by calling methods in the thread pool class**  
Define a method in the thread pool classt hat adds a task to a thread pool's queue. 


4. **Instantiate a Handler in the constructor and attach it to your app's UI Thread**  
A **Handler** allows your app to safely call the methods of UI objects such as View objects. Most UI objects may only be safely altered from the UI thread.

5. **Initialize pool size and maximum pool size**  
The initial number of threads to allocate to a pool, andthe maximum allowable number. The number of threads you can have in a thread pool depends primarily on the number of cores available on a device. This number is available from the system environment

6. **Define the keep alive time and time unit**  
The duration that a thread will remain idle before it shuts down. The duration is interpreted by the time unit value, one of the constants defined in TimeUnit.

7. **Define a queue of tasks**  
To start code on a thread, a thread pool manager takes a **Runnable** object from a first-in, first-out queue and attaches it to the thread. You provide this queue object when you create the thread pool, using any queue class that implements the **BlockingQueue** interface. To match the requirements of your app, you can choose from the available queue implementations; to learn more about them, see the class overview for **ThreadPoolExecutor**. 

```
public class PhotoManager {
    ...
    static {
        // Creates a single static instance of PhotoManager
        sInstance = new PhotoManger();
    }
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP-ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static ThreadPoolExecutor decodeThreadPool = new ThreadPoolExecutor(
        NUMBER_OF_CORES, // Initial pool size
        NUMBER_OF_CORES, // Max pool size
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_TIME_UNIT,
        decodeWorkQueue
    );
    // A queue of Runnables
    private final BlockingQueue<Runnable> decodeWorkQueue;
    // Instantiate the queue of Runnables as a LinkedBlockingQueue
    decodeWorkQueue = new LinkedBlockingQueue<Runnable>();

    // Constructs the work queues and thread pools used to download and decode images. Because the constructor is marked as private, it's unavailable
    // to other classes, even in the same package.
    private PhotoManger() {
        // Defines a Handler object that's attached to the UI Thread
        handler = new Handler(Looper.getMainLooper()) {
            // handleMessage() defines the operations to perform when the Handler receives a new Message to process
            @Override
            public void handleMessage(Message inputMessage) {

            }
        }
    }

    // Called by the PhotoView to get a photo
    static public PhotoTask startDownload(PhotoView imageView, DownloadTask downloadTask, boolean cacheFlag) {
        // Adds a download task to the thread pool for execution
        sInstance.downloadThreadPool.execute(
            downloadTask.getHTTPDownloadRunnable())
        );
    }
}
```

**Important**
* Enclose variables that can be accessed by more than one thread in a **synchronized** block. This approache will prevent one thread from reading the variable while another is writing to it.

## Communicating with the UI Thread
**Define a Handler on the UI Thread** <br>
**Handler** is part of the Android system's framework for managing threads. A **Handler** object receives messages and runs code to handle the messages. Normally, you create a **Hanlder** for a new thread, but you can also create a **Handler** that's connected to an existing thread. When you connect a **Handler** to your UI thread, the code that handles messages runs on the UI Thread.
```
private PhotoManager() {
    handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            // Gets the image tasks from the incoming Message object
            PhotoTask photoTask = (PhotoTask) inputMessage.obj;
        }
    }
}
```
