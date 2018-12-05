# ProtocolBufferReference

## What are protocol buffers
Flexible, efficient, automated mechanism for serializing data.
Define how you want the data to be structured once, then you can use special generated source code to easily write and read the structured data to and from a variety of data streams and using a variety of languages.

## How do they work
Specify how you want the information you're serializing to be strucutred by defining protocol vuffer message types in .proto files. Each protocol buffer message is a small logical record of information, containing a series of name-value pairs.<br>
For example the following : <br>
```
message Person {
  required string name = 1;
  required int32 id = 2;
  optional string email = 3;

  enum PhoneType {
    MOBILE = 0;
    HOME = 1;
    WORK = 2;
  }

  message PhoneNumber {
    required string number = 1;
    optional PhoneType type = 2 [default = HOME];
  }

  repeated PhoneNumber phone = 4;
}
```
Once the above is defined, run the protocol buffer compiler for your application's language on your .proto file to generate data access classes.<br>
Doing the above initializes the following: <br>
1. Simple accessors for each field such as name() and set_name()
2. Methods to serialize and parse the whole structure to and from raw bytes
3. You can now use this class in the application to populate, serialize, and retrieve ```Person``` Protocol buffer messages.
The following code can now be written: <br>
```
Person person;
person.set_name("John Doe");
person.set_id(1234);
person.set_email("jdoe@example.com");
fstream output("myfile", ios::out | ios::binary)
person.SerializeToOstream(&output);

// Then later, the message can be read back in
fstream input("myfile", ios::in | ios::binary);
Person person;
person.ParseFromIstream(&input);
cout << "Name: " << person.name() << endl;
cout << "Email: " << person.email() << endl;
```

## Benefits of using Protocol Buffers over XML
Protcol buffers have many advantages: <br>
* Are simpler
* Are 3 to 10 times smaller
* Are 20 to 100 times faster
* Are less ambiguous
* Generate data access classes that are easier to use programmatically
