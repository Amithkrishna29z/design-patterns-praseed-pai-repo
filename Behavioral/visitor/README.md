# Visitor Pattern

The **Visitor pattern** lets you add new operations to a set of object types
**without modifying those types**. It separates two concerns:

- **The object structure** — the "things" (`Wheel`, `Engine`, `Body`, `Car`).
  These rarely change.
- **The operations** — what you *do* to those things (print them, operate them,
  price them...). These change often.

Instead of putting every operation as a method inside each element class, each
operation lives in its own **visitor** class. Want a new operation? Write a new
visitor — you never touch the element classes again.

This example is the classic "car parts" demonstration. All classes live in
[`VisitorTest.java`](VisitorTest.java).

## The two interfaces

### `CarElement` — the element (the thing being visited)

```java
interface CarElement {
    void accept(CarElementVisitor visitor);
}
```

Every part promises one method: `accept(visitor)` — "come in, do your thing to me."

### `CarElementVisitor` — the visitor (the operation)

```java
interface CarElementVisitor {
    void visit(Wheel wheel);
    void visit(Engine engine);
    void visit(Body body);
    void visit(Car car);
}
```

One overloaded `visit` method **per concrete element type**. A visitor must know
how to handle every kind of part.

## The elements (object structure)

`Wheel`, `Engine`, and `Body` are leaf parts. Each implements `accept` with the
same key line:

```java
class Wheel implements CarElement {
    private String name;
    public Wheel(String name) { this.name = name; }
    public String getName() { return this.name; }

    public void accept(CarElementVisitor visitor) {
        visitor.visit(this);   // <-- the key line (double dispatch)
    }
}
```

`Car` is a composite element — it holds an array of parts, forwards the visitor
to each part, then visits itself last:

```java
class Car implements CarElement {
    CarElement[] elements;

    public Car() {
        this.elements = new CarElement[]{
            new Wheel("front left"), new Wheel("front right"),
            new Wheel("back left"),  new Wheel("back right"),
            new Body(), new Engine()};
    }

    public void accept(CarElementVisitor visitor) {
        for (CarElement element : this.getElements()) {
            element.accept(visitor);   // pass visitor to each part
        }
        visitor.visit(this);           // then visit the car itself
    }
}
```

`getElements()` returns `elements.clone()` so callers can't mutate the internal
array.

## The visitors (operations)

Two operations over the same car, neither requiring any change to the element
classes:

```java
class CarElementPrintVisitor implements CarElementVisitor {
    public void visit(Wheel wheel)   { System.out.println("Visiting " + wheel.getName() + " wheel"); }
    public void visit(Engine engine) { System.out.println("Visiting engine"); }
    public void visit(Body body)     { System.out.println("Visiting body"); }
    public void visit(Car car)       { System.out.println("Visiting car"); }
}

class CarElementDoVisitor implements CarElementVisitor {
    public void visit(Wheel wheel)   { System.out.println("Kicking my " + wheel.getName() + " wheel"); }
    public void visit(Engine engine) { System.out.println("Starting my engine"); }
    public void visit(Body body)     { System.out.println("Moving my body"); }
    public void visit(Car car)       { System.out.println("Starting my car"); }
}
```

## Running it

```java
Car car = new Car();
car.accept(new CarElementPrintVisitor());
car.accept(new CarElementDoVisitor());
```

Output:

```
Visiting front left wheel
Visiting front right wheel
Visiting back left wheel
Visiting back right wheel
Visiting body
Visiting engine
Visiting car
Kicking my front left wheel
Kicking my front right wheel
Kicking my back left wheel
Kicking my back right wheel
Moving my body
Starting my engine
Starting my car
```

Compile and run from the package root (the folder containing `behavioral/`):

```
javac behavioral/visitor/*.java
java behavioral.visitor.VisitorTest
```

## Double dispatch — the heart of the pattern

The behavior that runs depends on **two** types at once:

1. **Which element** it is (`Wheel` vs `Engine`) — decided by which class's
   `accept` runs.
2. **Which visitor** it is (`Print` vs `Do`) — decided by which visitor object
   was passed in.

Java's method dispatch only picks a method based on one object's runtime type.
Visitor fakes "double dispatch" by bouncing the call twice:

- `wheel.accept(printVisitor)` runs `Wheel.accept`, which calls `visitor.visit(this)`.
- Because `this` is statically typed `Wheel` there, the compiler picks the
  `visit(Wheel)` overload — and because `visitor` is a `PrintVisitor` at runtime,
  it runs *that* class's version.

So the combination `(Wheel, PrintVisitor)` correctly selects
`CarElementPrintVisitor.visit(Wheel)`. Neither dispatch alone could do it.

## Trade-off

- ✅ **Easy to add operations** — new visitor class, zero changes to elements.
- ❌ **Hard to add element types** — a new part (e.g. `Transmission`) forces a
  new `visit(Transmission)` in the `CarElementVisitor` interface *and* in every
  existing visitor.

Visitor is the right choice when your set of types is stable but the operations
over them keep growing.
