# Coroutines

Coroutines are light-weight threads. They are launched with **launch** coroutine builder in a context of some CoroutineScope. 


## Scope
GlobalScope: <br>
The lifetime of the new coroutine is limited only by the lifetime of the whole application.

## Functions
delay: A suspending function that does not block a thread, but suspends a coroutine
```
import kotlinx.coroutines.*

fun main() {
    // Launch new coroutine in background and continue
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    // Main thread continues immediately
    println("Hello,")
    // Block main thread for 2 seconds
    runBlocking {
        delay(2000L)
    }
}
```

join: Delaying for a time while another coroutine is working is not a good approach. Explicitly wait until the background **Job** that we have launched is complete
```
val job = GlobalScope.launch {
    delay(1000L)
    println("World!")
}
println("Hello,")
// Wait until child coroutine completes
job.join()
```

runBlocking: Does not allow a method to complete until all the coroutines launched in its scope complete
```
fun main() = runBlocking {
    launch { 
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}
```

coroutineScope: Creates a new coroutine scope and does not complete until all launched children complete. The main difference between **runBlocking** and **coroutineScope** is that the latter does not block the current thread while waiting for all children to complete.
```
fun main() = runBlocking {
    launch {
        delay(200L)
        println("Task from runBlocking")
    }

    coroutineScope {
        launch {
            delay(500L)
            println("Task from nested launch")
        }

        delay(100L)
        println("Task from coroutine scope")
    }

    println("Coroutine scope is over")
}
```

suspend: Suspending functions can be used inside coroutines just like regular functions, but their additional feature is that they can use other suspending functions such as `delay`
```
fun main() = runBlocking {
    launch { doWorld() }
    println("Hello,")
}

suspend fun doWorld() {
    delay(1000L)
    println("World!")
}
```

## Cancellation

Cancelling coroutine execution
```
val job = launch {
    repeat(1000) { i -> println("I'm sleeping $i ...")
    // Suspending function, is cancellable
    delay(500L)
    }
}

delay(1300L)
println("main: I'm tired of waiting")
job.cancel()
job.join()
println("main: Now I can quit")

// The above can also be 
job.cancelAndJoin()
```

A coroutine code has to cooperate to be cancellable. All the suspending functions in kotlinx.coroutines are cancellable. However, if a coroutine is working in a computation and does not check for cancellation, then it cannot be cancelled
```
val startTime = System.currentTimeMillis()
val job = launch(Dispatchers.Default) {
    var nextPrintTime = startTime
    var i = 0
    // isActive is an extension property that turns to false if cancelled
    while(isActive) {
        if(System.currentTimeMillis() >= nextPrintTime) {
            println("I'm sleeping ${i++} ...")
            nextPrintTime += 500L
        }
    }
}
delay(1300)L
println("main: I'm tired of waiting")
job.cancelAndJoin()
println("main: Now I can quit.")
```

Closing resources with finally. Cancellable suspending functions throw CancellableException on cancellation
```
val job = launch {
    try {
        repeat(1000) { i ->
                println("I'm sleeping $i ...")
            delay(500L)
        }
    } finally {
        println("I'm running finally")
    }
}
delay(1300L) // delay a bit
println("main: I'm tired of waiting!")
job.cancelAndJoin() // cancels the job and waits for its completion
println("main: Now I can quit.")
```

## Timeout
```
withTimeout(1300L) {
    repeat(1000) { i -> println("Sleeping")
        delay(500L)
    }
}

val result = withTimeoutOrNull(1300L) {
    repeat(1000) { i-> println("Sleeping")}
}
println("Result is $result")
```

## Channels
```
val channel = Channel<Int> ()
launch {
    for(x in 1..5) channel.send(x * x)
}

// Blocks
repeat(5) {println(channel.receive())}
println("Done!")
```

**Closing Channels**
```
val channel = Channel<Int>() 
launch {
    for(x in 1..5) channel.send(x * x)
    channel.close()
}

for(y in channel) println(y)
println("Done!")
```

**Building Channel Producers**
```
fun CoroutineScope.produceSquares() : ReceiveChannel<Int> = produce {
    for(x in 1..5) send(x * x)
}

val squares = produceSquares()
squares.consumeEach { println(it) }
println("Done!")
```

## Pipelining
A pattern in which a coroutine is producing a stream of values and another coroutine consume the stream, do some processing, and produce more results. 
```
fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while(true) send(x++)
}

fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
    for (x in numbers) send(x * x)
}

val numbers = produceNumbers()
val squares = square(numbers)
for(i in 1..5) println(squares.receive())
println("Done!")
coroutineContext.cancelChildren()
```

## Fan Out
Multiple coroutines may receive from the same channel, distributing work between themselves. 
```
fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) {
        send(x++)
        delay(100)
    }
}

fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Int>) = launch {
    for(msg in channel) {
        println("Processor #$id received $msg")
    }
}

val producer = produceNumbers()
repeat(5) { launchProcessor(it, producer) }
delay(950)
producer.cancel()
```