# Language Guide

## Search Query Example
Define a search request message format, where each search request has: <br> 
* A query string
* The particular page of results you are interested in
* A number of results per page.
  
```
// You must specify the proto3 syntax, otherwise, the compiler will assume that you are using proto2
syntax = "proto3";

// Each piece of data has a name and a type and a unique field number
message SearchRequest {
  reserved 1, 15, 9 to 11;
  string query = 1;
  int32 page_number = 2; // Which page number do we want?
  int32 result_per_page = 3; /* Number of results to return per page */
}

message SearchResposne {
    ...
}
```
**Unique Field Numbers**: <br>
Field numbers are used to identify your fields in the message binary format and should not change once the message type is in use
* Field numbers in the range 1 through 15 take one byte to encode, including the field number and the field's type, and should be reserved for very frequently occurring message elements.
* Field numbers in the range 16 ~ 2047 take two bytes

## Field Rules
**Singular**: <br>
A well-formed message can have zero or one of this field, but never more than one <br>
**Repeated**: <br>
This field can be repeated any number of times in a well-formed message where the order of the repeated values will be preserved <br>
**Reserved Fields**:<br>
If you updated a message type by removing a field or commenting it out, future users can accidentally reuse the field numbers when making their own updates to the type which can cause severe issues. Specify reserved values so the field values cannot be used <br>

## Generated Files
**C++**: <br>
The compiler generates a .h and .cc file from each .proto with a class for each message type described in the file<br>
**Java**:<br>
The compiler generates a .jave file with a class for each message type, as well as special Builder classes for creating message class instances.<br>
**Python**:<br>
The python compiler generates a module with a static descriptor of each message type in your .proto, which is then used with a *metaclass* to create the necessary Python data access class at runtime.<br>
**Go**: <br>
The compiler generates a .pb.go file with a type for each message type in your file.<br>
**Ruby**: <br>
The compiler generates a .rb file with a Ruby module containing your message types<br>
**Objective-C**:<br>
The compiler generates a pbobjc.h and pbobjc.m file from each .proto, with a class for each message type described in your file.<br>
**C#**:<br>
The compiler generates a .cs file from each .proto, with a class for each message type described in your file.<br>
**Data**:<br>
The compiler generates a pb.dart file with a class for each message type in your file. <br>

## Default Values
When a message is parsed, if the encoded message does not contain a particular singular element, the corresponding field in the parsed object is set to the default value or zero values for that field.
IMPORTANT: <br>
* There is no way of telling whether a field was explicitly set to the default value or just not set at all: you should bear this in mind when defining your message types. For example, don't have a boolean that switches on some behaviour when set to false if you don't want that behavious to also happen by default.

## Enumerations
When you're defining a message type, you might want one of its fields to only have one of a pre-defined list of values such as the following
```
message SearchRequest {
  string query = 1;
  int32 page_number = 2;
  int32 result_per_page = 3;
  enum Corpus {
    UNIVERSAL = 0; // Every enum definition must contain a constant that maps to zero as its first element as the default value
    WEB = 1;
    IMAGES = 2;
    LOCAL = 3;
    NEWS = 4;
    PRODUCTS = 5;
    VIDEO = 6;
  }
  Corpus corpus = 4;
}
```
**Enum Aliases**
Aliases can be defined by assigning the same value to different enum constants and setting the allow_alias option to true.
```
enum EnumAllowingAlias {
  option allow_alias = true;
  UNKNOWN = 0;
  STARTED = 1;
  RUNNING = 1;
}
```
**Reserved Enum**
If you update an enum type by entirely removing an enum entry, make sure to use the reserved values option so that no future users can use the values. <br>
```
enum Foo {
  reserved 2, 15, 9 to 11, 40 to max;
  reserved "FOO", "BAR";
}
```

## Using other message types
Example: If you wanted to include Result messages in each SearchResponse message. To do this, you can define a Result message type in the same .proto and then specify a field of type Result in SearchResponse: 
```
message SearchResponse {
  repeated Result results = 1;
}

message Result {
  string url = 1;
  string title = 2;
  repeated string snippets = 3;
}
```
In the above case, both of the messages are conveniently in the same .proto file. If you need a message type from a different .proto file, simply import them. <br>
```
import "myproject/other_protos.proto";
```

## Using nested message types
You can define and use message types inside other message types, as in the following example.
```
message SearchResponse {
  message Result {
    string url = 1;
    string title = 2;
    repeated string snippets = 3;
  }
  repeated Result results = 1;
}
```
If you want to use the nestded response type out its parent message type, you have to refer to it as Parent.Type
```
message SomeOtherMessage {
  SearchResponse.Result result = 1;
}
```

## Updating a Message Type
It is very simple to update message types without breaking any of your existing code. Just remember the following rules:
* Don't change the field numbers of any existing fields
* If you add new fields, any messages serialized by the code using the "old" format can still be parsed by the new generated code.
  * Similarly, messages generated by the new code can be parsed by the old code, old binaries will simply ignore the new field when parsing.
* Fields can be removed as long as :
  * The field number is not used again
  * You may want to rename the field with the prefix OBSOLETE_
  * Make the field number reserved
  
## The Any Type
The **Any** message type lets you use messages as embedded types without having their .proto definition. An **Any** contains an arbitrary serialized messages as bytes, along with a URL that acts as a globally unique identifier for an resolves to that message's type.
```
import "google/protobuf/any.proto";

message ErrorStatus {
  string message = 1;
  repeated google.protobuf.Any details = 2;
}
```
**Java**
```
// Storing an arbitrary message type in Any.
NetworkErrorDetails details = ...;
ErrorStatus status;
status.add_details()->PackFrom(details);

// Reading an arbitrary message from Any.
ErrorStatus status = ...;
for (const Any& detail : status.details()) {
  if (detail.Is<NetworkErrorDetails>()) {
    NetworkErrorDetails network_error;
    detail.UnpackTo(&network_error);
    ... processing network_error ...
  }
}
```

## Oneof
Oneof fields are like regular fields except all the fields in a oneof share memory, and at most one field can be set at the same time. Setting any member of the oneof automatically clears all the other members. <br>
To define a oneof, use the oneof keywork followed by your oneof name, in this case test_oneof:
```
message SampleMessage {
  oneof test_oneof {
    string name = 4;
    SubMessage sub_message = 9;
  }
}
```

## Maps
Map syntax:
```
map<key_type, value_type> map_field = N;
```


## Defining Services
If you want to use your message types with an RPC system, you can define an RPC service interface in a .proto file and the protocol buffer compiler will generate service interface code and stubs in your chosen language. <br>
If you want to generate an RPC service with a method that takes your SearchRequest and returns a SearchResponse, you can define it in the following manner:
```
service SearchService{
  rpc Search (SearchRequest) return (SearchResponse);
}
```

## Options
Options do not change the overall meaning of a declartion, but may affect the way it is handled in a particular context. Some options are file-level options, meaning they should be written at the top-level scope, not inside any message, enum, or service definition.<br>

**java_package** (file option) : <br>
The package to be used in the generated Java classes. If no explicit java_package option is given in the .proto file, then by default, the proto package will be used.
```
option java_package = "com.example.foo";
```

**java_multiple_files** (file option) : <br>
Causes top-level messages, enums, and services to be defined at the package level, rather than inside an outer class named after the .proto file.
```
option java_multiple_files = true;
```

**java_outer_classname** (file option) : <br>
The class name for the outermost Java class, and hence, the filename you want to generate.
```
option java_outer_classname = "Ponycopter";
```

**optimize_for** (file option)
  * SPEED (default) : The protocol vuffer compiler will generate code for serializing, parsing, and performing other common operations on your message types. The code will be highly optimized.
  * CODE_SIZE : The protocol buffer compiler will generate minimal classes and will rely on shared, reflection-based code to implement serialization, parsing, and various other operations.
  * LITE_RUNTIME: The protocol buffer compiler will generate classes that depend only on the "lite" runtime library which is optimized for constrained platforms such as mobile phones. This will still use the same SPEED standards for optimization also.

## Generating Classes
Protocol Compiler is invoked in the following manner:
```
protoc --proto_path=IMPORT_PATH --cpp_out=DST_DIR --java_out=DST_DIR --python_out=DST_DIR --go_out=DST_DIR --ruby_out=DST_DIR --objc_out=DST_DIR --csharp_out=DST_DIR path/to/file.proto
```
**IMPORT_PATH** <br>
Specifies a directory in which to look for .proto files when resolving **import** directives. If ommited, the current directory is used. Multiple directories can be specified by passing the --proto_path option multiple times; they will be searched in order. <br>

