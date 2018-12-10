package example;

import io.reactivex.*;

public class ReactiveX_Examples {
    /**
     * Creates a simple Observer object that emits "Hello, World!" and then completes
     */
    private static Observable<String> createSimpleObserver() {
        return new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscribe<? super String> sub) {
                sub.onNext("Hello World!");
                sub.onCompleted();
            }
        };
    }

    /**
     * Creates a simple Subscriber object that prints each string emitted by the Observable
     * @return
     */
    private static Subscriber<String> createSimpleSubscriber() {
        return new Subscriber<String> () {
            @Override
            public void onNext(String s) { System.out.println(s); }
        
            @Override
            public void onCompleted() { }
        
            @Override
            public void onError(Throwable e) { }
        };
    }

    public static void main(String[] args) {
        // Boilerplate Version
        // Create a basic observable
        Observable<String> myObservable = createSimpleObserver();

        // Create a simple subscriber
        Subscriber<String> mySubscriber = createSimpleSubscriber();

        // Make the subscription, once the subscription is made, myObservable calls the 
        // subscriber's onNext() and onComplete() methods.
        myObservable.subscribe(mySubscriber);

        // Simple Version
        // Observable.just() emits a single item then completes just like the example above
        Observable<String> myObservable2 = Observable.just("Hello, World!");

        // Consumers can define each part of a Subscriber. Observable.subscribe can handle 1 - 3 Consumer
        // parameters that take the place of onNext(), onError(), and onComplete(). Replicating our Subscriber 
        // from before
        Consumer<String> onNextConsumer = new Consumer<String>() {
            @Override
            public void accept(String t) {
                System.out.println(t);
            }
        };
        Consumer<String> onCompleteConsumer = new Consumer<String>() {
            @Override
            public void accept(String t) {

            }
        };
        Consumer<String> onErrorConsumer = new Consumer<String>() {
            @Override
            public void accept(String t) {

            }
        };
        myObservable2.subscribe(onNextConsumer, onErrorConsumer, onCompleteConsumer);
        // or through chaining
        Observable.just("Hello, World").subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    System.out.println(s);
                }
        });
        // use lambda
        Observable.just("Hello, World").subscribe(s -> System.out.println(s));
    }
}
