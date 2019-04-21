# Services

## Starting a Service

**Using Context.bindService(Intent service, ServiceConnection conn, Int flags)**
Connect to an application service, creating it if needed. 

* service:  
* conn: Receives information as the service is started and stopped. This must be a valid ServiceConnection object and it must never be null. This object will receive the service object when it is created and be told if it dies and restarts. 
* flags: 
  * BIND_AUTO_CREATE: Automatically create the service as long as long as the binding exists.