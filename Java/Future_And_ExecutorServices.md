# Future API

## What is a Future
The Future class represents a future result of an asynchronous computation - a result that will eventually appear in the Future after the processing is complete. 

## When to use it
Long running methods are good candidates for asynchronous processing andthe Future interface. This enables us to execute some other process while we are waiting for the task encapsulated in Future to complete. Other tasks include:
* Compoutational intensive processes
* Manipulating large data structures
* Remote method calls

## Implementing Futures with FutureTask
```
public class SquareCalculator {
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public Future<Integer> calculate(Integer input) {
        // Submit a callable, an interface representing a task that returns a result and has a single call() method.
        // Sumitting through an ExecutorService which returns a FutureTask object, an implementation of the Future interface.
        return executor.submit(() -> {
            Thread.sleep(1000);
            return input * input;
        });
    }
}
```

## Future Methods
**isDone()**:<br>
Tells us if the executor has finished processing the task returning true if the task has been completed, false otherwise.
**get()**:<br>
Returns the actual result from the calculation. This method blocks the execution until the task is complete, but we can continuously check if the task is completed by calling isDone() continuously.
```
Future<Integer> future = new SquareCalculator().calculate(10);
 
while(!future.isDone()) {
    System.out.println("Calculating...");
    Thread.sleep(300);
}
 
Integer result = future.get();
```
**cancel()**: <br>
Suppose we've triggered a task but, for some reason, we don't care about the result anymore. We can use this method to tell the executor to stop the operation and interrupt its underlying thread.
```
Future<Integer> future = new SquareCalculator().calculate(4);
boolean canceled = future.cancel(true);
```

## Multithreading with ThreadPools
```
SquareCalculator squareCalculator = new SquareCalculator();
 
Future<Integer> future1 = squareCalculator.calculate(10);
Future<Integer> future2 = squareCalculator.calculate(100);
 
while (!(future1.isDone() && future2.isDone())) {
    System.out.println(
      String.format(
        "future1 is %s and future2 is %s", 
        future1.isDone() ? "done" : "not done", 
        future2.isDone() ? "done" : "not done"
      )
    );
    Thread.sleep(300);
}
 
Integer result1 = future1.get();
Integer result2 = future2.get();
 
System.out.println(result1 + " and " + result2);
 
squareCalculator.shutdown();
```
Single Threaded Execution using Executors.newSingleThreadExecutor() output
```
calculating square for: 10
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
calculating square for: 100
future1 is done and future2 is not done
future1 is done and future2 is not done
future1 is done and future2 is not done
100 and 10000
```
Multi Threaded Execution using Executors.newFixedThreadPool(2);
```
calculating square for: 10
calculating square for: 100
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
100 and 10000
```