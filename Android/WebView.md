# WebView

## Javascript Enabling
```
// Allows javascript to run
webView.getSettings().setJavaScriptEnabled(true);
// User does not need to interact with the phone in order for the media to start/stop
webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
// Run the HTML script in the web-view
webView.loadData(
    html,
    "text/html",
    "UTF-8"
);
```