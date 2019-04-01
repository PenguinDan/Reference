# Basic Syntax

## Commenting
```
// This is an end-of-line comment

/* This is a block comment
    on multiple lines*/
```

## Numbers

**Assignment**
```
// Decimals
val decimalValue = 123
val unsignedDecimalValue : UInt = 123

// Longs
val longValue = 123L
val unsignedLongValue : ULong = 123u
val unsignedLongValue = 123UL

// Hexadecimals
val hexValue = 0x0f

// Binaries 
val binValue = 0b0001011

// Double by Default, floats must be tagged by f or F
val defaultDoubleValue = 123.5
val defaultDoubleValue2 = 123.5e10

// Floats 
val floatValue = 123.5f or 123.5F
```

**Underscores for Readability**
```
val oneMillion = 1_000_000
val creditCardNumber = 1234_5678_9012_3456L
```

**Explicit Conversions**
```
val i: Int = b.toInt()

toByte(): Byte
toShort(): Short
toInt(): Int
toLong(): Log
toFloat(): Float
toDouble(): Double
toChar(): Char
```

**Arithmetic Operations with Implicit Typing**
```
// Long + Int => Long
val l = 1L + 3
```

## Type Aliases
```
typealias MyHandler = (Int, String, Any) -> Unit
typealias Predicate<T> = (T) -> Boolean

class A {
    inner class Inner
}
class B {
    inner class Inner
}

typealias AInner = A.Inner
typealias BInner = B.Inner

fun foo(p: Predicate<Int>) = p(42)

fun main() {
    val f: (Int) -> Boolean = { it > 0 }
    println(foo(f))

    val p: Predicate<Int> = { it > 0 }
    println(listOf(1, -2).filter(p))
}
```

## Delegation
An alternative pattern from inheritance.
```
interface Base {
    fun print()
}

class BaseImpl(val x: Int) : Base {
    override fun print() { print(x) }
}

class Derived(b: Base) : Base by b

fun main() {
    val b = BaseImpl(10)
    Derived(b).print()
}
```
The by-clause in the supertype list for Derived indicates that b will be stored internally in objects of Derived and the compiler will generate all the methods of Base that forward to b.

## Arrays

**Surface Level Representations**
```
class Array<T> private constructor() {
    val size: Int
    operator fun get(index: Int): T
    operator fun set(index: Int, value: T): Unit

    operator fun iterator(): Iterator<T>
}
```

**Creating an Array**
```
// Below creates [1,2,3]
val arrayOfNumbers = arrayOf(1,2,3)

// Creates an array of size 5 in which all indices are null values
val arrayOfNulls = arrayOfNulls(5)

// Using an Array Constructor
// Creates an Array<String> with values ["0", "1", "4", "9",...]
val asc = Array(5, { i -> (i * i).toString() })
asc.forEach { println(it) }
```

**Specialized Arrays (Performance)**
```
val x: IntArray = intArrayOf(1,2,3)
// Works the exact same as normal arrays
x[0] = x[1] + x[2]

val x: UIntArray = uIntArrayOf(1,2,3)
```

## Variables

**Read only variables**
```
// Explicit assignmnet
val a: Int = 1
// Inferred assignment
val b = 2
// Deferred assignment
val c: Int
c = 3
```

**Read/Write variables**
```
// Integer type is inferred
var x = 5
x += 1
```

**Top-Level Variables**
```
val PI = 3.14
var x = 0

fun incrementX() {
    x += 1
}
```

**Lazy Property**
```
// Maybe we do not want to instantiate a property of a class because we know its heavy and it won't be used all the time
class Person{
    val p : LargeAndHeavyObject by lazy {
        // Compute the heavy object 
    }
}
```

**Type Checks and Auto Casting**
```
fun getStringLength(obj: Any): Int? {
    // If we go into this line, obj is automatically cast into a String
    if(obj is String) {
        return obj.length
    }

    // Otherwise, return null
    return null
}

fun getStringLength(obj: Any): Int? {
    if(obj !is String) return null
    return obj.length
}

fun getStringLength(obj: Any): Int? {
    if(obj is String && obj.length > 0) {
        return obj.length
    }
    return null
}
```

**Compile-Time Constants**
Properties the value of which is known at compile time. Such properties need to fulfill the following requirements:
- Top-level, or member of an object delcaration, or a companion object
- Initialized with a value of type String or a primitive type
- No custom getter

## Conditionals

**If statement**
```
fun maxOf(a: Int, b: Int) : Int {
    if (a > b) {
        return a
    } else {
        return b
    }
}
```


**When statement**
```
fun describe(obj: Any): String = 
    when(obj) {
        1 -> "One"
        "Hello" -> "Greeting"
        is Long -> "Long"
        !is String -> "Not a string"
        else -> "Unknown"
    }

// Check if a collection contains an object in operator
val items = listOf("apple", "orange", "banana")
when {
    "cheese" in items -> println("nope")
    "apple" in items -> println("apple is fine too")
}

// Many of the same cases handled the same way
when(x) {
    0, 1 -> print("x == 0 or x == 1")
    else -> print("otherwise")
}
```

**Capture when subject in a variable**
```
fun Request.getBody() = 
    when(val response = executeRequest()) {
        is Success -> response.body
        is HttpError -> throw HttpException(response.status)
    }
```

**Range Check**
```
val x = 10
val y = 9
if(x in 1..y+1) {
    println("fits in range")
}

val list = listOf("a", "b", "c")
if(-1 !in 0..list.lastIndex) {
    println("-1 is out of range")
}
if(list.size !in list.indices) {
    println("list size is out of valid list indices range, too")
}
```

## Loops

**Returns and Jumps**
Kotlin supports the following jump expressions:
* return
* break
* continue

**Labeled Jumps**
```
fun foo() {
    listOf(1,2,3,4,5).forEach {
        if(it == 3) return
        print(it)
    }
    println("this point is unreachable")
}

// Explicit Label
fun foo() {
    listOf(1,2,3,4,5).forEach lit@{
        if(it == 3) return@lit
        print(it)
    }
    print(" it is now reachable")
}

// Implicit label
fun foo(){
    listOf(1,2,3,4,5).forEach {
        if(it == 3) return@forEach
        print(it)
    }
    print(" still reachable")
}


// Or we can create an anonymous function, a return statement in an anonymous function will return from the anonymous function itself
fun foo() {
    listOf(1,2,3,4,5).forEach(fun(value: Int) {
        if(value == 3) return
        print(value)
    })
    print(" done with anonymous function")
}
```

**For**
```
val items = listOf("apple", "banana", "kiwifruit")
for(item in items) {
    println(item)
}

val items = listOf("apple", "banana", "kiwifruit")
for(index in items.indices) {
    println("item at $index is ${items[index]}")
}

// Iterating over a range
for(x in 1..5) {
    print(x)
}

// Iterating over a progression
for(x in 1..10 step 2) {
    print(x)
}
for(x in 9 downTo 0 step 3) {
    print(x)
}

// Same as Python enumerate function in loops
for((index, value) in array.withIndex()) {
    println("the element at $index is $value")
}
```

**While**
```
val items = listOf("apple", "banana", "kiwifruit")
var index = 0
while(index < items.size) {
    println("item at $index is ${items[index]})
    index++
}
```

## String Utilities

**String Interpolation**
```
var a = 1
val s1 = "a is $a"
a = 2
val s2 = "${s1.replace("is", "was")}, but now is $a"
```

**Character Iteration**
```
for(c in str) {
    println(c)
}
```

**String Concatenation**
It is prefered to use string interpolation than string concatenation
```
val s = "abc" + 1
println(s + "def")
```

**Literal Raw Strings**
```
val text = """
    for (c in "foo")
        print(c)
"""
```

**Remove leading Whitespaces**
```
val text = """
    |Tell me and I Forget.
    |Teach me and I remember.
    |Involve me and I learn.
    |(Benjamin Franklin)
""".trimMargin()
```

## Collections 

**Read-Only List and Map**
```
val list = listOf("a", "b", "c")
val map = mapOf("a" to 1, "b" to 2, "c" to 3) 
```

**Accessing a Map**
```
println(map["key"])
map["key"] = value
```

**Filtering and Mapping**
```
// Below, creating a list from filtering a list
val positives = list.filter { x -> x > 0}
val positives = list.filter{ it > 0 }
```

```
val fruits = listOf("banana", "avocado", "apple", "kiwifruit")
fruits
    .filter { it.startsWith("a")}
    .sortedBy {it}
    .map {it.toUpperCase()}
    .forEach{println(it)}
```

**Traversing a Map/List of pairs**
```
for ((k,v) in map) {
    println("$k -> $v")
}
```

## Nullable Values
A reference must be explicitly marked as nullable when `null` value is possible
```
fun parseInt(str: String) : Int? {
    if (str.isEmpty()) return null
}
```

**Null Check Short Hand**
```
val files = File("Test").listFiles()
// Bottom will print "null" if files is null
println(files?.size)

// Bottom will print out "empty" if files is null
println(files?.size ?: "empty")
```

**Auto Non-Null Casting**
```
fun printProduct(arg1: String, arg2: String) {
    val x = parseInt(arg1)
    val y = parseInt(arg2)

    // Automatically cast to non-nullable after these null checks
    if(x != null && y != null) {
        println(x * y)
    } else {
        println("either '$arg1' or '$arg2' is not a number")
    }
}
```

**Get first item of a possibly empty collection**
```
// Value emails might be empty
val emails = ... 
val mainEmail = emails.firstOrNull() ?: ""
```

**Execute if not null**
```
val value = ...
value?.let {
    // Execute this block if not null
}
```

**Executing a statment if null**
```
val values = ...
val email = values["email"] ?: throw IllegalStateException("Email is missing")
```

## Functions

**Simple Function Definition**
```
// Two integer parameters, returning an Integer type
fun sum(a: Int, b: Int) : Int {
    return a + b
}
```

**Functions with Default Values**
```
fun foo(a: Int = 0, b: String = "") {
    ...
}
```

**Overrding functions with Default Parameters**
```
open class A {
    open fun foo(i: Int = 10) { ... }
}
class B : A() {
    override fun foo(i: Int) { ... }
}
```

**Inferred Return type function**
```
fun sum(a: Int, b: Int) = a + b
fun maxOf(a: Int, b: Int) = if (a > b) a else b
```

**"Void" Function**
```
fun printSum(a: Int, b: Int){
    println("sum of $a and $b is ${a + b}")
}
```

**Lambda Parameters**
If the last argument after default parameters is a lambda, it can be passed in either as a named argument or outside the parantheses
```
fun foo(bar: Int = 0, baz: Int = 1, qux: () -> Unit) {
    ...
}

foo(1) { println("Hello") }
foo(qux = { println("Hello") })
foo { println("Hello") }
```

**Variable Number of Arguments** 
```
fun foo(vararg strings: String) { ... }

foo(strings = *arrayOf("a", "b", "c"))
foo("a", "b", "c")
```

**Closure**
```
fun dfs(graph: Graph) {
    val visited = HashSet<Vertex>()
    fun dfs(current: Vertex) {
        if(!visited.add(current)) return
        for(v in current.neighbors)
            dfs(v)
    }

    dfs(graph.vertices[0])
}
```

## Classes and Instances

**Basic Class intantiation**
```
val rectangle = Rectangle(5.0, 2.0)
val triangle = Triangle(3.0, 4.0, 5.0)
```

**Extension Functions**
Giving additional functionality to functions without creating an anonymous class <br>
Example 1)
```
fun String.spaceToCamelCase() {
    ...
}
"Covert this to camelcase".spaceToCamelCase()
```
Example 2)
```
fun MutableList<Int>.swap(index1: Int, index2: Int) {
    // 'this' corresponds to the list, the receiver object
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

val l = mutableListOf(1,2,3)
l.swap(0,2)

// Can also be generic since it make sense in the generic scope
fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}
```
If a class has a member function, and an extension function is defined which ahs the same receiver type, the same name is applicable to given arguments, the **member always wins**.
```
class C {
    fun foo() { println("member") }
}
fun C.foo() { println("extension ) }
```
In the above case, c.foo() will print "member". However, it's perfectly OK for extension functions to overload member functions which have the same name but a different signature.
```
class C {
    fun foo() { println("member") }
}
fun C.foo(i: Int) { println("extension") }
```
In the above case, C().foo(1) will print "extension"

**Nullable Receiver**
```
fun Any?.toString(): String{
    if(this == null) return "null"

    return toString()
}
```

**Extension Properties**
Initializers are not allowed for extension properties
```
// Below allowed
val <T> List<T>.lastIndex: Int
    get() = size - 1

// Below, not allowed
val Foo.bar = 1
```

**Extensions as Members**
```
class D {
    fun bar() { ... }
}

class C {
    fun baz() { ... }

    fun D.foo() {
        bar()
        baz()
    }
    
    fun caller(d: D) {
        d.foo()
    }

    fun D.biz() {
        // Calls D.toString()
        toString()
        // Calls C.toString()
        this@C.toString)()
    }
}
```
With overriding
```
open class D { }

class D1 : D() {  }

open class C {
    open fun D.foo() {
        println("D.foo in C")
    }
    open fun D1.foo() {
        println("D1.foo in C")
    }
    fun caller(d: D) {
        d.foo()
    }
}
class C1: C() {
    override fun D.foo() {
        println("D.foo in C1")
    }

    override fun D1.foo() {
        println("D1.foo in C1")
    }
}

fun main() {
    // Prints "D.foo in C"
    C().caller(D())
    // Prints "D.foo in C1"
    C1().caller(D())
    // Prints "D.foo in C" 
    C().caller(D1())
}
```

**Companion Object Extensions**
```
class MyClass {
    companion object {} 
}

fun MyClass.Companion.foo() { ... }
```

## Expressions

**Assigning a Value from try/catch**
```
fun test() {
    val result = try {
        count()
    } catch(e: ArithmeticException) {
        throw IllegalStateException(e)
    }

    // Work with result
}
```

**Assigning a Value from if**
```
val max = if (a > b) a else b

// if branches can be blocks, and the last expression is the value
of a block:
val max = if(a > b) {
    print("Choose a")
    a
} else {
    print("Choose b")
    b
}

fun foo(param: Int) {
    val result = if(param == 1) {
        "one"
    } else if(param == 2) {
        "two"
    } else {
        "three"
    }
}
```

## Classes and Objects

**Constructors**
A Kotlin class can have a **primary constructor** and one or more **secondary constructors**.  Primary constructors cannot contain any code. Initialization code can be placed in initializer blocks prefixed with the **init** keyword.

**Primary Constructor Syntax**
```
class Person constructor(firstName: String) {

}
// If the primary constructor does not have any annotations or visibility modifiers, the constructor keyword can be ommited
class Person(firstName: String) {

}

// With visibility modifiers and keywords
class Customer public @Inject constructor(name: String) {

}
```

**Secondary Constructors**
If the class has a primary constructor, each secondary constructor needs to delegate to the primary constructor, either directly or indirectly through another secondary constructor.
```
class Person(val name: String) {
    constructor(name: String, parent: Person) : this(name) {
        parent.children.add(this)
    }
}
```

**Class Initializations**
Initializer blocks are executed in the same order as they appear in the class body, interleaved with the property initializers. Code inside of initializer blocks also run before secondary constructors.
```
class InitOrderDemo(name: String) {
    val firstProperty = "First property: $name".also(::println)

    init {
        println("First initializer block that prints ${name}")
    }

    val secondProperty = "Second property: ${name.length}".also("println")

    init {
        println("Second initializer block that prints ${name.length}")
    }
}
```
Above, the values printed are: <br>
First property: hello
First initializer block that prints hello
Second property: 5
Second initializer block that prints 5

```
class Constructors {
    init {
        println("Init block")
    }

    constructor(i: Int) {
        println("Constructor")
    }
}
```
Above, the following values are printed: <br>
Init block
Constructor

**Getter and Setter**
```
class TestClass{
    // Custom getter
    val isEmpty: Boolean
        get() = this.size == 0
    // Custom setter
    var stringRepresentation: String
        get() = this.toString()
        set(value) {
            setDataFromString(value)
        }
    // Defining visibility scope or annotation
    var setterVisibility: String = "abc"
        private set // Private setter
    // Annotated setter
    var setterWithAnnotation: Any? = null
        @Inject set
}
```

**Backing Fields**
```
var count = 0
    set(value) {
        if (value >= 0) field = value
    }
```
In the above example, field is equal to this.count. If we did `counter = 0` instead, we would get a StackOverflow error because it would set off a recursive call to counter.

**Creating a Singleton**
```
object Resource {
    val name = "Name"
}
```

**Calling multiple methods on an Object instance**
```
class Turtle {
    fun penDown()
    fun penUp()
    fun turn(degrees : Double)
    fun forward(pixels : Double)
}

val myTurtle = Turtuel()

with(myTurtle) {
    penDown()
    for(i in 1..4) {
        forward(100.0)
        turn(90.0)
    }
    penUp()
}
```

## Inheritance
All classes in Kotlin have a common superclass `Any`

**Declaring super types**
If the derived class has a primary constructor, the base class can (and must) be initialized right there, using the parameters of the primary constructor
```
open class Base(p: Int)
class Derived(p: Int) : Base(p)

class MyView : View {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
}
```

**Overriding Methods**
```
open class Base {
    // Methods that are overridable must be declared with "open" keyword
    open fun v() {
        ...
    }
    fun nv() {...}
}

class Derived() : Base() {
    // A member marked override is itself open, it may be overridden in subclasses. To prohibit this, use "final"
    override fun v() { 

    }

    // Un-overridable
    final override fun v() { ... }
}
```

**Overriding Properties**
```
open class Foo {
    open val x: Int get() {...}
}

class Bar1: Foo() {
    override val x: Int = ...
    // Or
    override var x: Int = ...
}

interface Foo {
    val count: Int
}

class Bar1(override val count: Int) : Foo

class Bar2: Foo {
    override var count: Int = 0
}
```

**Derived class Initialization Order**
```
open class Base(val name: String) {
    init { println("Initializing Base")}
    open val size: Int = 
        name.length.also { println("Initializing size in Base: $it )}
}

class Derived(
    name: String,
    val lastName: String
) : Base(name.capitalize().also { println("Argument for Base: $it" )}) {
    init { println("Initializing Derived")}

    override val size: Int = 
        (super.size + lastName.length).also{ println("Initialization")}
}
```

**Calling the Super-Class Implementation**
```
open class Foo {
    open fun f() { println("Foo.f()")}
    open val x: Int get() = 1
}

class Bar : Foo() {
    override fun f() {
        super.f()
        println("Bar.f()")
    }

    override val x: Int get() = super.x + 1
}
```
Inside an inner class, accessing the superclass of the outer class is done with the `super` keyword qualified with the outer class name: `super@Outer`
```
class Bar: Foo() {
    override fun f() { }
    override val x: Int get() = 0

    inner class Baz {
        fun g() {
            // Calls Foo's implementation of f()
            super@Bar.f()
            // Users Foo's implementation of x
            println(super@Bar.x)
        }
    }
}
```

**Conflicting Super-Class**
```
open class A {
    open fun f() { print("A") }
    fun a() { print("a") }
}

interface B {
    // Interface methods are open by default
    fun f() { print("B") }
    fun b() { print("b") }
}

class C() : A(), B {
    // The compiler requires f() to be overriden
    override fun f() {
        // Overrides A specific function of f()
        super<A>.f() 
        // Overrides B specific function of f()
        super<B>.f()
    }
}
```

**Abstract Classes**
```
open class Base {
    open fun f() {}
}
abstract class Derived : Base() {
    override abstractr fun f()
}
```

## Data Classes
Classes whose main purpose is to hold data. Data classes have the following requirements:
- The primary constructor needs to have at least one parameter
- All primary constructor parameters need to be marked as val or var
- Data classes cannot be abstract, open, sealed, or inner
- Data classes may only implement interfaces
```
data class User(val name: String, val age: Int)
```
If you define a property outside of the primary constructor, functions will not be generated automatically. In the below example, only the value of `name` will be used to check for equality since it only will be used inside the toString(), equals(), hashCode(), and copy() implementations
```
data class Person(val name: String) {
    var age: Int = 0
}
```

**Copying**
In some cases, we need to copy an object while altering some of its properties, but keeping the rest unchanged. 
```
fun copy(name: String = this.name, age: Int = this.age) = User(name, age)

val jack = User(name = "Jack", age = 1)
val olderJack = jack.copy(age = 2)
```

**Destructing Declarations**
```
val jane = User("Jane", 35)
val (name, age) = jane
println("$name, $age years of age")
```


## Sealed Classes
Sealed classes are used for representing class hierarchies, when a value can have one of the types from a limited set, but cannot have any other type. A sealed class can have subclasses, but all of the must be declred in the same file as the sealed class itself.
- A sealed class is **abstract** by itself, it cannot be instantiated directly and can have abstract members.
- Sealed classes are not allowed to have non-private constructors. Their constructs are private by default
```
sealed class Expr
data class Const(val number: Double) : Expr()
data class Sum(val e1: Expr, val e2: Expr) : Expr()
object NotANumber: Expr()

fun eval(expr: Expr): Double = when(expr) {
    is Const -> expr.number
    is Sum -> eval(expr.e1) + eval(expr.e2)
    NotANumber -> Double.NaN
}
```

## Interfaces
The only difference between interfaces and abstract classes is that interfaces cannot hold state
```
interface MyInterface {
    // Below is an abstract property
    val prop: Int 
    val propertyWithImplementation: String
        get() = "foo"

    fun bar()
    fun foo() {
        // Optional body
        print(prop)
    }
}

class Child : MyInterface {
    override val prop: Int = 29

    override fun bar() {

    }
}
```

**Interface Inheritance**
```
interface Named {
    val name: String
}

interface Person : Named {
    val firstname: String
    val lastName: String

    override val name: String get() =
}
```

## Visibility Modifiers

**Package level Visibility Modifiers**
There are four visibility modifiers in Kotlin: <br>
**private**: <br>
Only visible inside the file containing the declaration

**protected**: <br>
Not available for top-level declarations
  
**internal**: <br>
Visible everywhere in the same module
  
**public**: <br>
This is the default visibility if there is no explicit modifier
```
// File name: example.kt
package foo

// Visible inside example.kt
private fun foo() { ... }

// Property is visible everywhere
public var bar: Int = 5
    // Setter is visible only in example.kt
    private set

// Visible inside the same module
internal val baz = 6
```

**Class and Interface level Visibility Modifiers**
**private**: <br>
Visible inside this class only (including all its members)

**protected**: <br>
Same as private but also visible in subclasses too

**internal**: <br>
Any clients inside this module who sees the declaring class sees its internal members also

**public**: <br>
Any client who sees the delcaring class sees its public members
```
open class Outer{
    private val a = 1
    protected open val b = 2
    internal val c = 3
    val d = 4 // Public by default

    protected class Nested {
        public val e: Int = 5
    }
}

class Subclass : Outer() {
    // a is not visible
    // b,c and d are visible
    // Nested and e are visible

    override val b = 5
}

class Unrelated(o : Outer) {
    // o.a, o.b are not visible
    // o.c and o.d are visible (same module)
    // Outer.Nested is not visible, and Nested::e is not visible either
}
```

## Generics
```
class Box<T>(t: T) {
    var value = t
}

val box: Box<Int> = Box<Int>(1)
// Inferred typing
val box = Box(1)
```

## Nested and Inner Classes

**Nested Classes**
```
class Outer{
    private val bar: Int = 1
    class Nested {
        fun foo() = 2
    }
}
val demo = Outer.Nested().foo()
```

 **Inner Classes**
 A class may be marked as **inner** to be able to access members of outer class. Inner classes carry a reference to an object of an outer class.
 ```
 class Outer {
     private val bar: Int = 1
     inner class Inner {
         fun foo() = bar
     }
 }
 val demo = Outer().Inner().foo() 
 ```

 **Anonymous Inner Classes**
 ```
 window.addMouseListener(object: MouseAdapter() {
     override fun mouseClicked(e: MouseEvent) { ... }
     override fun mouseEntered(e: MouseEvent) { ... }
 })
 ```
 If the object is an instance of a functional Java interface (a Java Interface with a single abstract method), you can create it using a lambda expression
 ```
 val listener = ActionListener { println("clicked") }
 ```

 ## Enum Classes
 ```
 enum class Direction {
     NORTH, SOUTH, WEST, EAST
 }
 ```
 Since each enum is an instance of the enum class, they can be initialized as:
 ```
 enum class Color(val rgb: Int) {
     RED(0xFF0000),
     GREEN(0x00FF00),
     BLUE(0x0000FF)
 }
 ```
 
 **Anonymous Classes**
 Enum constants can also declare their own anonymous classes
 ```
 enum class ProtocolState {
     WAITING {
         override fun signal() = TALKING
     },
     TALKING {
         override fun signal() = WAITING
     };

     abstract fun signal(): ProtocolState
 }
 ```

 ## Objects
 ```
 open class A(x: Int) {
     public open val y: Int = x
 }
 interface B { ... }
 val ab: A = object : A(1), B {
     override val y = 15
 }
 ```
