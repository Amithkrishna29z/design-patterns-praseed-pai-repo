# Static Factory Method Pattern

The **Static Factory Method** is a creational technique where a class hides its constructor and instead exposes **static methods that build and return instances**. Each method is a named "factory" for one way of constructing the object.

## When to use it

- A single constructor signature can't express the different *meanings* of the same parameters. Here a `Complex` number can be built from **Cartesian** coordinates `(real, imaginary)` or from **Polar** coordinates `(modulus, angle)` — but both are just two `double`s, so overloaded constructors would collide.
- You want construction to have a **descriptive name** (`createFromPolar`) instead of a bare `new`.
- You want to control or vary what actually gets returned (a cached object, a subclass, etc.) without the caller knowing.

## The code in this folder

| File | Role |
|------|------|
| `Complex.java` | A complex-number class with a **private** constructor and two static factory methods. |

---

## `Complex.java` — the factory

```java
public class Complex {
    double _r, _i;                                  // (1) internal Cartesian state

    public static Complex createFromCartesian(double real, double imaginary) {
        return new Complex(real, imaginary);        // (2) factory #1
    }

    public static Complex createFromPolar(double modulus, double angle) {
        return new Complex(modulus * Math.cos(angle),   // (3) factory #2 — converts polar -> cartesian
                           modulus * Math.sin(angle));
    }

    private Complex(double a, double b) {           // (4) private constructor
        _r = a;
        _i = b;
    }

    public double Modulus() { ... }
    public double getR() { return _r; }
    public double getI() { return _i; }
}
```

### How each piece drives the pattern

1. **Internal state (`_r`, `_i`)** — the object always stores itself in Cartesian form. Callers don't need to know that; they pick whichever coordinate system is convenient.
2. **`createFromCartesian`** — the straightforward factory: it just forwards its arguments to the constructor.
3. **`createFromPolar`** — the reason the pattern earns its keep. It takes `(modulus, angle)` and converts to Cartesian (`r·cos θ`, `r·sin θ`) *before* constructing. Two static methods with the same parameter types `(double, double)` are legal; two constructors with those same types would **not compile**.
4. **Private constructor** — `private Complex(...)` blocks `new Complex(...)` from outside the class. That forces every caller through a factory method, which is what lets the two named entry points coexist.

---

## `main` — the demo

```java
public static void main(String[] args) {
    Complex c = Complex.createFromCartesian(100, 100);
    Complex p = Complex.createFromPolar(10, Math.PI / 4);
    System.out.println(c.getR() + " + " + c.getI() + "i");
    System.out.println(p.getR() + " + " + p.getI() + "i");
}
```

`main` builds one `Complex` from each factory and prints its Cartesian components:

- `c` comes straight from `(100, 100)`.
- `p` comes from polar `(modulus = 10, angle = π/4)`, which the factory converts to Cartesian — both components land near `7.07` (`10 · cos 45°` and `10 · sin 45°`).

---

## How to compile and run

From the `Creational/Factory` folder:

```sh
javac Complex.java
java Complex.Complex   # the class is in package "Creational.Factory"
```

> Because the file declares `package Creational.Factory;`, Java expects the class on the classpath as `Creational.Factory.Complex`. The simplest way is to compile from the repository root that contains the `Creational` folder:
>
> ```sh
> cd "design patterns praseed pai"
> javac Creational/Factory/Complex.java
> java Creational.Factory.Complex
> ```

The program produces no output as written (see the demo section above to add some).

---

## Notes on this example

- **`Modulus()` is a stub**: it prints a message and returns `0xBEEF` (48879) instead of computing `√(r² + i²)`. A real implementation would be `return Math.hypot(_r, _i);`.
- **Static factory vs. Factory Method / Abstract Factory**: this is the *static factory method* idiom (named constructors on one class). It is related to, but distinct from, the GoF **Factory Method** pattern (a subclass overrides a method to decide which product to instantiate) and **Abstract Factory** (a family of related products behind an interface).
