# Advanced Java Protocol Buffer
## Messages
Given a simple message declaration
```
message Foo{}
```
The protocol buffer compiler generates a class called Foo, which implements the **Message** interface. The class will be **final**; no further subclassing is allowed. The **Message** interface defines methods that let you check, manipulate, read, or write the entire message. In addition to these methods, the **Foo** class defines the following **static** methods:
* static Foo getDefaultInstance(): Returns a singleton instance of Foo, which is identical to what you'd get if you called Foo.newBuilder().build() where all singular fileds are unset and all repeated fields are empty.
* static Descriptor getDescriptor() : Returns the type's descriptor. This contains information about the type, including what fields it has and what their types are. This can be used with the reflection methods of the **Message**, such as **getField()**.
* static Foo parseFrom(...) : Parses a message of type **Foo** from the given source and returns it. There is one **parseFrom** method corresponding to each variant of **mergeFrom()** in the Message.Builder interface. 
* static Parser parser(): Returns an instance of the **Parser**, which implements various **parseFrom()** methods.
* Foo.Builder newBuilder(): Creates a new builder
* Foo.Builder newBuilder(Foo prototype): Creates a new builder with all fields initialized to the same values that they have in prototype. Since embedded message and string objects are immutable, they are shared between the original and the copy.

## Builders
To construct a message object, you need to use a *builder*. Each message class has its own builder class. 

## Sub Builders
For messages containing sub-messages, the compiler also generates sub builders. This allows you to repeatedly modify deep-nested sub-messages without rebuilding them.
```
message Foo {
    int32 val = 1;
}

message Bar {
    Foo foo = 1;
}

maessage Baz {
    Bar bar = 1;
}
```
If you have a Baz message already and want to change the deeply nested val in Foo, do the following:
```
Baz.Builder builder = baz.toBuilder();
builder.getBarBuilder().getFooBuilder().setVal(10);
baz = builder.build();
```

## Service Interface
Given a service definition:
```
service Foo {
    rpc Bar(FooRequest) returns(FooResponse);
}
```
The protocol buffer compiler will generate an abstract class Foo to represent this service. Foo will have an abstract method for each method defined in the service definition. In this case, the method Bar is defined as :
```
abstract void bar(RpcController controller, FooRequest request, RpcCallback<FooResponse> done);
```
Foo subclasses the **Service** interface. The protocol buffer compiler automatically generates implementations of the methods of Service as follows:
* getDescriptorForType: Returns the service's ServiceDescriptor.
* callMethod: Determines which method is being called based on the provided method descriptor and calls it directly, down-casting the request message and callback to the correct types
* getRequestPrototype and getResponsePrototype: Returns the default instance of the request or response of the correct type for the given method. <br>
Foo will also generate the following static method:
* static ServiceDescriptor getDescriptor(): Returns the type's descriptor, which contains information about what methods this service has and what their input and output types are.

**Summary** <br>
To implement your own services:
* Subclass Foo and implement its methods as appropriate, then hand instances of your subclass directly to the RPC server implementation.
* Or implement Foo.Interface and use Foo.newReflectiveService(Foo.Interface) to construct a Service wrapping it, then pass the wrapper to your RPC implementation.

## Stub
The protocol buffer compiler also generates a **stub** implementation of every service interface, which is used by clients wishing to send requests to servers implementing the service. **For the Foo service, the stub implementation Foo.Stub will be defined as a nested class.** Foo.Stub is a subclass of Foo which also implements the following methods:
* Foo.Stub(RpcChannel channel) : Constructs a new stub which sends requests on the given channel
* RpcChannel getChannel(): Returns this stub's channel, as passed to the constructor
The Protocol Buffer library does not include an RPC implementation. However, it includes all of the tools you need to hook up a generated service class to any arbitrary RPC implementation of your choice. You need only provide implementations of **RpcChannel** and **RpcController**.

## Blocking Interfaces
The RPC classes described above all have non-blocking semantics: when you call a method, you provide a callback object which will be invoked once the method completes. Often it is easier to write code using blocking semantics, where the method simply doesn't return until it is done. To accomodate this, the protocol buffer compiler also generates blocking versions of your service class. **Foo.BlockingInterface** is equivalent to Foo.Interface except that each method simply returns the result rather than a callback.
```
abstract FooResponse bar(RpcController controller, FooRequest request) throws ServiceException;
```