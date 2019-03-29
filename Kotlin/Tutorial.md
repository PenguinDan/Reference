# Kotlin Tutorial

## Commenting
```
// This is an end-of-line comment

/* This is a block comment
    on multiple lines*/
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

**String Templates**
```
var a = 1
val s1 = "a is $a"
a = 2
val s2 = "${s1.replace("is", "was")}, but now is $a"
```

## Collections 

**Filtering and Mapping**
```
val fruits = listOf("banana", "avocado", "apple", "kiwifruit")
fruits
    .filter { it.startsWith("a")}
    .sortedBy {it}
    .map {it.toUpperCase()}
    .forEach{println(it)}
```

## Nullable Values
A reference must be explicitly marked as nullable when `null` value is possible
```
fun parseInt(str: String) : Int? {
    if (str.isEmpty()) return null
}
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

## Functions

**Simple Function Definition**
```
// Two integer parameters, returning an Integer type
fun sum(a: Int, b: Int) : Int {
    return a + b
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

## Classes and Instances

**Basic Class intantiation**
```
val rectangle = Rectangle(5.0, 2.0)
val triangle = Triangle(3.0, 4.0, 5.0)
```

