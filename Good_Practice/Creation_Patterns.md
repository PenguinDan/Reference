# Creational Patterns

## Abstract Factory or Kit
**Intent** <br>
Provide an interface for creating families of related or dependent objects without specifying their concrete classes. 

**When to Use** <br>
1. A system should be independent of how its products are created, composed, and represented
2. A system should be configured with one of multiple families of products.
3. A family of related product objects is designed to be used together, and you need to enforce this constraint.
4. You want to provide a class library of products, and you want to reveal just their interfaces, not their implementations. 

**Participants** <br>
1. AbstractFactory
    * Declares an interface for operations that create abstract product objects.
2. ConcreteFactory
    * Implements the operations to create product objects. 
3. AbstractProduct
    * Declares an interface for a type of product object.
4. ConcreteProduct
    * Defines a product object to be created by the corresponding concrete factory.
    * Implements the AbstractProduct interface
5. Client
    * Uses only interfaces declared by AbstractFactory and AbstractProduct classes.

**Consequences** <br>
1. The Abstract Factory pattern helps you control the classes of objects that an application creates. Because a Factory encapsulates the responsibility and the process of creating product objects, it isolates clients from implementation classes.

**Implementation** <br>
1. Factories as Singletons
    * An application typically only needs one instance of a ConcreteFactory per product family.
2. Creating the products
    * Abstract family only declares an interface for creating products. It's up to ConcreteProduct subclass to actually create them. The most common way to do this is to define a factory method for each product.
    * A concrete factory will specify its products by overriding the factory method for each.