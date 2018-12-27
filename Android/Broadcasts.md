# Broadcasts

## Manifest-Declared Receivers
If you declare a broadcast receiver in your manifest, the system launches your application when the broadcast is sent. The system package manager registers the receiver when the app is installed. The receiver then becomes a separate entry point into the application which means that the system can start the application and deliver the broadcast if the application is not currently running. The system creates a new BroadcastReceiver component object to handle each broadcast that it receives. This object is valid only for the duration of the call to **onReceive(Context, Intent)**. Once your code returns from this method, the system considers the component no longer active.<br>
To declare a broadcast receiver in the manifest, perfrom the following steps:
```
---------------- Manifest -----------------
// Specify the <receiver> element in your app's manifest
<receiver android:name=".MyBroadcastReceiver" android:exported="true">
    // Specify the broadcast actions your receiver subscribes to
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED">
        <action android:name="android.intent.action.INPUT_METHOD_CHANGED">
    </intent-filter>
</receiver>


------------ Java ----------------------
// Subclass BroadcastReceiver and implement onReceive(Context, Intent). The Broadcast Receiber in the
// following example logs and displays the contents of the broadcast:
public class MyBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "MyBroadcastReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuilder sb = new StringBuilder();
            sb.append("Action: " + intent.getAction() + "\n");
            sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
            String log = sb.toString();
            Log.d(TAG, log);
            Toast.makeText(context, log, Toast.LENGTH_LONG).show();
        }
    }
```

The following snippet shows a BroadcastReceiver that uses goAsync() to flag that it needs more time to finish after onReceive() is complete. This is especially useful if the work you want to complete in your onReceive() is long enough to cause the UI thread to miss a frame (>16ms), making it better suited for a background thread.
```
public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();
        Task asyncTask = new Task(pendingResult, intent);
        asyncTask.execute();
    }

    private static class Task extends AsyncTask {

        private final PendingResult pendingResult;
        private final Intent intent;

        private Task(PendingResult pendingResult, Intent intent) {
            this.pendingResult = pendingResult;
            this.intent = intent;
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            sb.append("Action: " + intent.getAction() + "\n");
            sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
            String log = sb.toString();
            Log.d(TAG, log);
            return log;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Must call finish() so the BroadcastReceiver can be recycled.
            pendingResult.finish();
        }
    }
}

```

## Context Registered Receivers
Context-Registered receivers receive broadcast as long as their registering context is valid. For an example, *if you register within
an Activity context*, you receive broadcasts as long as the activity is not destroyed. *If you register with the Application context*, you receive broadcasts
as long as the application is running.
```
// Create an instance of BroadcastReceiver
BroadcastReceiver br = new MyBroadcastReceiver();

// Create an IntentFilter and register the receiver by calling registerReceiver(BroadcastReceiver, IntentFilter)
IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
this.registerReceiver(br, filter);
```
**LocalBroadcastManager.registerReceiver(BroadcastReceiver, IntentFilter)** to register for local broadcasts. <br>
**To stop receiving broadcasts**, call **unregisterReceiver(android.content.BroadcastReceiver)**. Be sure to unregister the receiver when you no
longer need it or the context is no longer valid.

Registration best practice: <br>
* If you register a receiver in **onCreate(Bundle)** using the activity's context, you should unregister it in **onDestroy()** to prevent leaking the receiver
out of the activity context.<br>
* If you register a receiver in **onResume()**, you should unregister it in **onPause()** to prevent registering it multiple times.

## Sending Broadcasts
1. **sendOrderedBroadcast(Intent, String)**: Sends broadcasts to one receiver at a time. As each receiver executes in turn, it can propagate a result to the next
receiver or it can completely abord the broadcast so that it won't be passed to other receivers.
2. **sendBroadcast(Intent)**: Sends broadcasts to all receivers in an undefined order. This is called a Normal Broadcast which is more efficient that the previously mentioned one.
3. **LocalBroadcastManager.sendBroadcast(..)**: Sends a broadcasts to receivers that are in the same application as the sender. This is the most efficient and you do not need to worry about any security issues related to other applications being able to receive or send your broadcasts.
```
Intent intent = new Intent();
intent.setAction("com.example.broadcast.MY_NOTIFICATION");
intent.putExtra("data", "Notice me senpai!");
sendBroadcast(intent);
```
a
