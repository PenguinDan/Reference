# RX Java 

## How to Design Using RxJava
1. Create Observables which emit data items
2. Transform those Observables in various ways to get the precise data items that interest you by using Observable Operators
3. Observe and react to these sequences of interesting items by implementing Observers or Subscribers and then subscribing them to the resulting transformed Observables

## Reactive Types
1. Observable<br>
An *observer* subscribes to an *Observable*. Then that *observer* reacts to whatever item or sequence of items the Observable *emits*. Concurrent operations are facilitated because it does not need to block whilewaiting for the *Observable* to emit objects, but instead it creates a sentry in the form of an *observer* that stands ready to react appropriately at whatever future time the *Observable* does so.

2. Completeable<br>
The completable class represents a deferred computation without any value but only indication for completion or exception. Completable behaves similarly to Observable except that it can only emit a completion or error signal (there is no onNext or onSuccess as with the other reactive types). **Completable class implements the CompletableSource base interface and the default consumer type it interacts with is the CompletableObserver via the subscribe(CompletableObserver) method**.

3. Flowable<br>


4. Single<br>
The *Single* class implements the Reactive Pattern for a single value response. Single behaves similarly to Observable except that it can only emit either a single successfuly value or an error.

5. Maybe<br>
The *Maybe* class represents a deferred computation and emission of a single value, no value at all or an exception.

## Other Reactive Classes
1. Action <br>
A functional interface similar to Runnable but allows throwing a checked exception.

## 2 Ways to Implement Creating Observables
### Using create() to create an Observable manually and calling the onNext() and onComplete() ... yourself
Implement the Observable's behavior manually by passing a function to **create()** that exhibits Observable behavior. The **create()** function constructs a safe reactive type instance and provides a type-specific Emitter for this function to generate the signal(s) the designated business logic requires. This method allows bridging the non-reactive, usually listener/callback-style world, with the reactive world.
```
ScheduledExecutorService executor = Executors.newSingleThreadedScheduledExecutor();

// What should happen upon an observable has been subscribed to.
ObservableOnSubscribe<String> handler = emitter -> {
    Future<Object> future = executor.schedule(() -> {
        emitter.onNext("Hello");
        emitter.onNext("World");
        emitter.onComplete();
        return null;
    }, 1, TimeUnit.SECONDS);

    emitter.setCancellable(() -> future.cancel(false));
}

Observable<String> observable = Observable.create(handler);

// Defines the parameters for onNext, onError, and onComplete
observable.subscribe(item -> System.out.println(item), error -> error.printStackTrace(), ()-> System.out.println("Done"));
Thread.sleep(2000);
executor.shutdown();
```
### Convert existing data structures into Observables
1. **Using just()** <br>
Constructs a reactive type by taking a pre-existing object and emitting that specific object to the downstream consumer upon subscription. There are overloads with 2 - 9 arguments for convenience in which the objects will be emitted in the order they are specified
```
String greeting = "Hello World!";
Observable<String> observable = Observable.just(greeting);
observable.subscribe(item -> System.out.println(item));
```
**OVERLOADED just()**
```
Observable<Object> observable = Observable.just("1", "A", "3.2", "def");
observable.subscribe(item -> System.out.print(item), error -> error.printStackTrace(), ()-> System.out.println());
```
2. Using from() <br>

**fromIterable()** -> Flowable, and Observable <br>
Signals the items from a java.lang.Iterable source (such as Lists, Sets, or Collections or custom Iterables) and then completes the sequence.
```
List<Integer> list = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9));
Observable<Integer> observable = Observable.fromIterable(list);
observable.subscribe(item -> System.out.println(item), error -> error.printStackTrace(),() -> System.out.println("Done"));
```
**fromArray()** -> Flowable, and Observable <br>
Signals the elements of the given array and then completes the sequence. RxJava does not support primitive arrays, only generic reference arrays.
```
Integer[] array = new Integer[10];
for (int i = 0; i < array.length; i++) {
    array[i] = i;
}

Observable<Integer> observable = Observable.fromIterable(array);

observable.subscribe(item -> System.out.println(item), error -> error.printStackTrace(), () -> System.out.println("Done"));
```
**fromCallable()** -> Flowable, Observable, Maybe, Single, Completable <br>
When a consumer subscribes, the given java.util.concurrent.Callable is invoked and its returned value (or thrown exception) is relayed to taht consumer.
```
Callable<String> callable = () -> {
    System.out.println("Hello World!");
    return "Hello World!");
}

Observable<String> observable = Observable.fromCallable(callable);

observable.subscribe(item -> System.out.println(item), error -> error.printStackTrace(), () -> System.out.println("Done"));
```
**fromAction()** -> Maybe, Completable <br>
When a consumer subscribes, the given io.reactivex.function.Action is invoked and the consumer completes or receives the exception the Action threw.
```
Action action = () -> System.out.println("hello world");
Completable completable = Completable.fromAction(action);
completable.subscribe(() -> System.out.println("Done"), error -> error.printStackTrace());
```
**fromRunnable()** -> Maybe, Completable <br>
When a consumer subscribes, the given io.reactivex.function.Action is invoked and the consumer completes or receives the exception the Action threw.
```
Runnable runnable = () -> System.our.println("Hello, World!");
Completable completable = Completable.fromRunnable(runnable);
completable.subscribe(() -> System.out.println("Done"), error -> error.printStackTrace());
```
**fromFuture()** -> Flowable, Observable, Maybe, Single, Completable <br>
Given a pre-existing, already running or already completed java.util.concurrent.Future, wait for the Future to complete or with an exception in a blocking fashion and relaty the produced value or exception to the consumers.
```
ScheduledExecutorService executor = Executors.newSingleThreadedScheduledExecutor();
Future<String> future = executor.schedule(() -> "Hello World", 1, TimeUnit.SECONDS);
Observable<String> observable = Observable.fromFuture(future);
observable.subscribe(
    item -> System.out.println(item),
    error -> error.printStackTrace(),
    () -> System.out.println("Done")
);
executor.shutdown();
```
3. Using generate() -> Flowable, Observable <br>
Creates a cold, synchronous and stateful generator of values
```
int startValue = 1;
int incrementValue = 1;
Flowable<Integer> flowable = Flowable.generate(() -> startValue, (s, emitter) -> {
	int nextValue = s + incrementValue;
	emitter.onNext(nextValue);
	return nextValue;
});
flowable.subscribe(value -> System.out.println(value));
```
4. Using defer() -> Flowable, Observable, Maybe, Single, Completable <br> 
Does not create the Observable until the observer subscribes, and create a fresh Observable for each observer. The **defer()** operator waits until an observer subscribes to it, and then it generates an Observable, typically with an Observable factory function. It does this afresh for each subscriber, so although each subscriber may think it is subscribing to the same Observable, in fact, each subscriber gets its own individual sequence.
```
Observable<Long> observable = Observable.defer(() -> {
    long time = System.currentTimeMillis();
    return Observable.just(time);
});
observable.subscribe(time -> System.out.println(time));
Thread.sleep(1000);
observable.subscribe(time -> System.out.println(time));
```
5. Using range() -> Flowable, Observable <br>
Creates an Observable that emits a particular range of sequential integers
```
String greeting = "Hello World";
Observable<Integer> indexes = Observable.range(0, greeting.length());
Observable<Char> charactgers = indexes.map(index -> greeting.charAt(index));
characters.subscribe(character -> System.out.print(character), error -> error.printStackTrace(), () -> System.out.println());
```
6. Using interval() -> Flowable, Observable <br>
Periodically generates an infinite, ever increasing numbers. The intervalRange variant generates a limited amount of such numbers.
```
Observal<Long> clock = Observable.interval(1, TimeUnit.SECONDS);
clock.subscribe(time -> {
    if(time % 2 == 0) {
        System.out.println("TICK");
    } else {
        Sytem.out.println("TOCK");
    }
});
```
7. Using timer() -> Flowable, Observable, Maybe, Single, Completable <br>
After the specified time, this reactive source signals a single 0L
```
Observable<Long> eggTimer = Obervable.timer(5, TimeUnit.MINUTES);
eggTimer.blockingSubscribe(v -> System.out.println("Egg is ready!"));
```
7. Using empty() -> Flowable, Observable, Maybe, Completable <br>
This type of source signals completion immediately upon subscription
```
Observable<String> empty = Observable.empty();

empty.subscribe(
    v -> System.out.println("This should never be printed!"), 
    error -> System.out.println("Or this!"),
    () -> System.out.println("Done will be printed."));
```


## Apply Transformations
1. Using compose(ObservableTransformer<? super T, ? extends R> composer) <br>
Transforms an ObservableSource by applying a particular Transformer function to it. <br>
@Param: The function that transforms the source ObservableSource
@Return: Returns the source ObservableSource, transformed by the transformer function. 

## Merging Observable Sources
Flattens multiple ObservableSources that emits into a single ObservableSource. For example, you can have multiple ObservableSource objects that is combined and each call onNext() from that single ObservableSource that was created after merging.<br>
**Observable.merge(..)**

