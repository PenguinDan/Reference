# Location

## Battery Optimization
**Accuracy**: <br>
The precision of the location data. In general, the higher the accuracy, the higher the battery drain. Use `setPriority()` to specify location accuracy.
- PRIORITY_HIGH_ACCURACY: Provides the most accurate location possible but may cause significant battery drain.
- PRIORITY_BALANCED_POWER_ACCURACY: Accurate location while optimizing for power
- PRIORITY_LOW_POWER: City-level accuracy with minimal battery drain
- PRIORITY_NO_POWER: Receives locations passively from other apps for which location has already been computed.

**Frequency**: <br>
How often location is computed. The more frequent location is computed, the more battery is used. Use `setInterval()` to specify the interval at which locatoin is computed for your application and use `setFastestInterval()` to specify the interval at which location computed for other apps is delivered to your application.

**Latency**: <br>
How quickly location data is delivered. Less latency usually requires more battery. Use `setMaxWaitTime()` method to specify the latency. 

## Examples

**A weather application that wants to know the device's location once**
- Use `getLastLocation()` method, which returns the most recently available location which may be null in rare cases. This method provides a simple way of getting location and doesn't incur costs associated with actively requesting location updates. 
- Use in conjunction with the `isLocationAvailable()` method, which returns `true` when the location returned by `getLastLocation()` is reasonably up-to-date.

**Starting updates when a user is at a specifc location** 
- Use geofencing in conjunction with fused location provider updates. Request updates when the app receives a geofence entrance trigger, and remove updates when the app receives a geofence exit trigger. This ensures that the app gets more granular location updates only when the user has entered a defined area.
- The typical workflow for this scenario could involve surfacing a notification upon the geofence enter transition, and launching an activity which contains code to request updates when the user taps the notification.

## Adding Timeouts
