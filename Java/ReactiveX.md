#What is ReactiveX?

## Why use ReactiveX
Allows you to treat streams of asynchronous events with the same sort of simple, composable operatins that you use for collections of data items like arrays. It frees you from tangled webs of callbacks, and thereby makes your code more readable and less prone to bugs.

## Setting up Java Dependencies:
implementation "io.reactivex.rxjava2:rxjava:2.x.y"

## The Reactive Manifesto
Systems must be:
* Responsive - systems should response in a timely manner
* Message Driven - Systems should use asynchronous message-passing between components to ensure loose coupling
* Elastic - Systems should stay responsible under high load
* Resilient - Systems should stay responsive when some components fail

## ReactiveX Observables
### What are Observables: <br>
**Observables** 
* Any object that can get data from a data source and whose state may be of interest in a way that other objects may regist an interest.
  
**Observer** 
* Any object that wishes to be notified when the state of another object changes. 
* Subscribes to an Observable sequence. **The sequence sends items to the observer one at a time**.
* The observer handles each one before processing the next one. If many events come in asynchronously, they must be stored in a queue or dropped.
* In Rx, an observer will never be called with an item out of order or called before the callback has returned from the previous item.

### Types of Observables
**Non-Blocking**:
* Asynchronous execution is supported and is allowed to unsubscribe at any point in the event stream.

**Blocking**:
* All **onNext** observer calls will be synchronous and it is not possible to unsubscribe in the middle of an event stream. We can always convert an Observable into a Blocking Observable, using the method **toBlocking**
```
BlockingObservable<String> blockingObservable = observable.toBlocking();
```

###Operators
Operators are functions that take on Observable **(the source)** as its first argument and returns another Observable **(the destination)**. For every item that the source observable emits, it will apply a function to that item, and then emit the result on the destination Observable.
* Operators can be chained together to create complex data flows that filter event based on certain criteria. Multiple operators can be applied to the same observable.
* Read the **How to deal with Backpressure** reference to see how to deal with a situation where an Observable is emitting items faster than an operator or observer can consume them.

### Creating a Simple Observable
```
// Emit a single generic instance before completing which in this case is the string "Hello"
Observable<String> observable = Observable.just("Hello");
// Implement an observer interface and then call subscribe on the desired Observable
observable.subscribe(s -> result = s);
// Asser the value
assertTrue(result.equals("Hello"));
```

### The Observer Interface
1. OnNext
* Called on our observer each time a new event is published to the attached Observable. This is the method where we'll perform some action on each event.
2. OnCompleted
* Called when the sequence of events associated with an Observable is complete, indicating that we should not expect any more onNext calls on our observer.
3. OnError
* Called when an unhandled exception is thrown during the RxJava framework code or our event handling code
```
// The return value for the Observables subscribe method is a subscribe interface
String[] letters = {"a", "b", "c", "d", "e", "f", "g"};
// Convert an Array into an ObservableSource that emits the items in the Array
Observable<String> observable = Observable.from(letters);
// Implement the interfaces
observable.subscribe(
    // OnNext
    i -> result += i,
    // OnError
    Throwable::printStackTrace,
    // OnCompleted
    () -> result += "_Completed"
);
// Assert the values
assertTrue(result.equals("abcdefg_Completed"));
```

### Actions and Consumers
Consumers can define each part of a Subscriber. 
* Action
* Consumer
* BiConsumer
* Consumer<Object[]>
  
## Observable Transformations and Conditional Operators
Map Operator:
* The map opeator transforms items emitted by an Observable by applying a function to each item.
```
// Declared array of strings that contain some letters from the alphabet and we want to print them in capital mode:
Observable.from(letters)
    .map(String::toUpperCase)
    .subscribe(letter -> result += letter);
assertTrue(result.equals("ABCDEFG"));
```
Flat Map Operator:
* The flatMap operator can be used to flatted Observables whenever we end up with nested Observables
* Takes the emissiosn of one Observable and returns the emissions of another Observable to take its place
```
Observable<String> getTitle() {
    return Observable.from(titleList);
}
Observable.just("book1", "book2")
    .flatMap(s -> getTitle())
    .subscribe(l -> result += l);
assertTrue(result.equals("titletitle"));
```
```
// Another example
// Suppose the below method is available that returns a list of URLs
Observable<List<String>> query(String text);
// Therefore we have the following which is ugly
query("Hello, World")
    .subscribe(urls -> {
        for (String url : urls) {
            System.out.println(url);
        }
    });
// We COULD use the following, but it uses a very ugly code also
query("Hello, world!")
    .subscribe(urls -> {
        Observable.from(urls)
            .subscribe(url -> System.out.println(url));
    });
// We could use flatMap in the following manner
query("Hello, world")
    .flatMap(urls -> Observable.from(urls))
    .subscribe(url -> System.out.println(url));
// flatMap() returns another Observable. They key concept here is that the new Observable returned is what the Subscriber sees. It doesn't receive a List<String>, it gets a series of individual Strings as returned by Observable.from()
```
```
// ANOTHER Example
// flatMap can literally return ANY Observable
// Suppose we want to print the title of each website received. But there's a few issues: my method only works on a single URL at a time, and it doesn't
// return a String, it returns an Observable that emits the String if it is not 404
Observable<String> getTitle(String URL);
// With flatMap, we can do the following
query("Hello, world")
    .flatMap(urls -> Observable.from(urls))
    .flatMap(url -> getTitle(url))
    .filter(title -> title != null) // Only if not null
    .take(5) // Show max 5 at time
    .doOnNext(title -> saveTitle(title))
    .subscribe(title -> System.out.println(title));
```
Scan Operator:
* The scan operator applies a function to each item emitted by an Observable sequentially and emits each successive value. This allows us to carry forward state from event to event:
```
// Instantiate String array
String[] letters = {"a", "b", "c"};
// Create the Observable instance from the String array and use the Scan operator
Observable.from(letters)
    // Create a StrinBuilder object and call the append method every single time and return it inside of onNext while persisting the current object
    .scan(new StringBuilder(), StringBuilder::append)
    .subscribe(total -> result += total.toString());
// total = a -> ab -> abc
assertTrue(result.equals("aababc"));
```
GroupBy Operator:
* Classify the events in the input Observable into output categories
```
// Use GroupBy to divide the inputs into even and odd
Observable.from(numbers)
    .groupby(i -> 0 == (i % 2)? "EVEN" : "ODD")
    .subscribe(group -> 
        group.subscribe((number) -> {
            if(group.getKey().toString.equals("EVEN")) {
                EVEN[0] += number;
            } else {
                ODD[0] += number;
            }
        }));
assertTrue(EVEN[0].equals("0246810"));
assertTrue(ODD[0].equals("12579"));
```
Filter Operator:
* Emits only those items from an observable that pass a predicate test
```
// Filter an integer array for the odd numbers
Observable.from(numbers)
    .filter(i -> (i % 2 == l))
    .subscribe(i -> result += i);
assertTrue(result.equals("13579"));
```
Conditional Operators:
* **DefaultIfEmpty** emits item from the source Observable, or a default item if the source Observable is empty:
```
Observable.empty()
    .defaultIfEmpty("Observable is empty")
    .subscribe(s -> result += s);
assertTrue(result.equals("Observable is empty"));
```
* **TakeWhile** discards items emitted by an Observable after a specified condition becomes false:
```
Observable.from(numbers)
    .takeWhile(i -> i < 5)
    // 1+2+3+4
    .subscribe(s -> sum[0] += s);
assertTrue(sum[0] == 10);
```
* Other conditional operators include:
    * Contain, SkipWhile, SkipUntil, TakeUntil, etc.

## Connectable Observables
A ConnectableObservable resembles an ordinary Observable, except that it doesn't begin emitting items when it is subscribed to, but only when the **connect** operator is applied to it. This way, we can wait until all intended observers to subscribe to the Observable before the Observable begins emitting items.
```
String[] result = {""};
// .interval emits a sequential number every specified interval of time
// .publish returns a ConnectableObservable
ConnectableObservable<Long> connectable = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
connectableObserver.subscribe(i -> result[0] += i);
assertFalse(result[0].equals("01"));

connectable.connect();
Thread.sleep(500);

assertTrue(result[0].equals("01"));
```

## Single
An Observable who, instead of emitting a series of values, emits one value or an error notification. 
* OnSuccess returns a Single that also calls a method we specify
* OnError returns a Single that immediately notifies subscribers of an error
```
String[] result = {""};
Single<String> single = Observable.just("Hello");
    .toSingle()
    .doOnSuccess(i -> result[0] += i)
    .doOnError(error -> {
        throw new RuntimeException(error.getMessage())
    });

single.subscribe();

assertTrue(result[0].equals("Hello"));
```

## Subjects
A Subject is simultaneously two elements, a subscriber and an observable. As a subscriber, a subject can be used to publish the events coming from more than one observable and because it is also an observable, the events from multiple subscribers can be reemitted as its events to anyone obserbing it.
```
Integer subscriber1 = 0;
Integer subscriber2 = 0;
Observer<Integer> getFirstObserver() {
    return new Observer<Integer>() {
        @Override
        public void onNext(Integer value) {
            subscriber1 += value;
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("Error");
        }

        @Override
        public void onCompleted() {
            System.out.println("Subscriber1 completed");
        }
    };
};

Observer<Integer> getSecondObserver() {
    return new Observer<Integer> () {
                @Override
        public void onNext(Integer value) {
            subscriber2 += value;
        }
 
        @Override
        public void onError(Throwable e) {
            System.out.println("error");
        }
 
        @Override
        public void onCompleted() {
            System.out.println("Subscriber2 completed");
        }
    };
};

PublishSubject<Integer> subject = PublishSubject.create();
subject.subscribe(getFirstObserver());
subject.onNext(1);
subject.onNext(2);
subject.onNext(3);
subject.subscribe(getSecondObserver());
subject.onNext(4);
subject.onCompleted();

assertTrue(subscriber1 + subscriber2 == 14);
```

## Resource Management
The **Using** operation allows us to associate resources such as a JDBC database connection, a network connection, or open files to our observables.
```
String[] result = ("");
Observable<Character> values = Observable.using(
    () -> "MyResource",
    r -> {
        return Observable.create(o -> {
            for (Character c: r.toCharArray()) {
                o.onNext(c);
            }
            o.onCompleted();
        });
    },
    r -> System.out.println("Disposed: " + r)
);
values.subscribe(
    v -> result[0] += v,
    e -> result[0] += e
);

assertTrue(result[0].equals("MyResource"));
```

