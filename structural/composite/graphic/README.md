# Composite Pattern — Graphics Tree

## Concept

The **Composite** is a structural design pattern that lets you compose objects into
tree structures and then treat individual objects and compositions of objects
**uniformly**. The client calls the same method on a leaf as it does on a whole
branch — it doesn't need to know whether it is holding one shape or a group of
shapes.

**Intent:** treat "a single thing" and "a group of things" through one common
interface, so recursive structures can be handled without special-casing.

**The three roles:**
- **Component** — the shared interface (or abstract class) declaring operations
  common to both leaves and composites.
- **Leaf** — a primitive object with no children; it does the actual work.
- **Composite** — an object that holds children (leaves or other composites) and
  usually implements each operation by delegating to those children.

**When to use it:**
- You have a **part-whole hierarchy** (files/folders, menus/menu-items, shapes/groups).
- You want clients to ignore the difference between a single object and a group.

**Trade-off:** a single, uniform interface can make the design overly general —
`add`/`remove` make sense on a composite but not on a leaf, so you must decide
whether to put those methods on the shared `Component` (transparent, but leaves
inherit meaningless operations) or only on `Composite` (safe, but the client must
distinguish the two). This example takes the **safe** route: `add`/`remove` live
only on `CompositeGraphic`.

## This Example

The example models drawable graphics. Every shape can be `print()`-ed. A
`CompositeGraphic` is itself a graphic that contains other graphics, so printing it
prints everything inside — recursively, to any depth.

### The roles in this code

| Role       | Class(es)                          | Responsibility                                    |
|------------|------------------------------------|---------------------------------------------------|
| Component  | `Graphic` (interface)              | Declares `print()`                                |
| Leaf       | `Ellipse`, `Rectangle`, `Arc`      | Print themselves                                   |
| Composite  | `CompositeGraphic`                 | Holds children; `print()` delegates to each child |

### The structure built by `main`

```
graphic  (CompositeGraphic)
├── graphic1  (CompositeGraphic)
│   ├── ellipse1  (Ellipse) → "Ellipse"
│   ├── ellipse2  (Ellipse) → "Ellipse"
│   └── arc       (Arc)     → "Arc"
└── graphic2  (CompositeGraphic)
    └── rect       (Rectangle) → "Rectangle"
```

Calling `graphic.print()` walks this tree depth-first and prints each leaf.

## Full Code Walkthrough

The whole example lives in a single file, `CompositeTest.java`. It contains the
public entry-point class plus the interface and several package-private classes.

### `Graphic` — the Component

```java
interface Graphic {
    public void print();
}
```

The shared interface. **Both** leaves and composites implement it, which is exactly
what lets the client treat them the same way. It declares only `print()` — the one
operation every graphic supports. Notice it deliberately does **not** declare
`add`/`remove`; those belong only to things that can hold children.

### `CompositeGraphic` — the Composite

```java
class CompositeGraphic implements Graphic {
    private List<Graphic> mChildGraphics = new ArrayList<Graphic>();

    public void print() {
        for (Graphic graphic : mChildGraphics) {
            graphic.print();
        }
    }

    public void add(Graphic graphic) {
        mChildGraphics.add(graphic);
    }

    public void remove(Graphic graphic) {
        mChildGraphics.remove(graphic);
    }
}
```

- `mChildGraphics` holds the children. Because the list is typed `List<Graphic>`,
  a child can be a leaf **or** another composite — that uniformity is the whole point.
- `print()` is the recursive step: it loops over its children and calls `print()` on
  each. It never checks whether a child is a leaf or a composite; polymorphism
  handles that. A composite child prints its own children, and so on, all the way down.
- `add` / `remove` manage the children. They exist **only** here, not on the
  `Graphic` interface, so you can't accidentally call `add` on an `Ellipse`.

### `Ellipse` and `Rectangle` — Leaves

```java
class Ellipse implements Graphic {
    public void print() {
        System.out.println("Ellipse");
    }
}

class Rectangle implements Graphic {
    public void print() {
        System.out.println("Rectangle");
    }
}
```

The primitive shapes. Each has no children and implements `print()` by simply
printing its name. This is where the "actual work" happens at the bottom of the tree.

### `Arc` — a Leaf via inheritance

```java
class Arc extends Ellipse {
    public void print() {
        System.out.println("Arc");
    }
}
```

`Arc` extends `Ellipse` (an arc is a piece of an ellipse) and **overrides** `print()`
to print `"Arc"` instead. It's still a `Graphic`, so it slots into a composite just
like any other leaf. This shows the pattern doesn't care *how* a leaf came to
implement the interface — inheritance or direct implementation both work.

### `CompositeTest` — the client / entry point

```java
public class CompositeTest {
    public static void main(String[] args) {
        Ellipse ellipse1 = new Ellipse();
        Ellipse ellipse2 = new Ellipse();
        Graphic arc = new Arc();
        Graphic rect = new Rectangle();

        CompositeGraphic graphic = new CompositeGraphic();
        CompositeGraphic graphic1 = new CompositeGraphic();
        CompositeGraphic graphic2 = new CompositeGraphic();

        graphic1.add(ellipse1);
        graphic1.add(ellipse2);
        graphic1.add(arc);

        graphic2.add(rect);

        graphic.add(graphic1);
        graphic.add(graphic2);

        graphic.print();
    }
}
```

Step by step:
1. Create four **leaves**: two ellipses, an arc, and a rectangle.
2. Create three **composites**: `graphic`, `graphic1`, `graphic2`.
3. Fill `graphic1` with the two ellipses and the arc; fill `graphic2` with the
   rectangle.
4. Add `graphic1` and `graphic2` **into** `graphic` — composites nested inside a
   composite. This is the key move: a composite is added exactly like a leaf,
   because both are just `Graphic`s.
5. Call `graphic.print()` **once**. The single call cascades down the whole tree.

The client builds a two-level tree but issues just one `print()`. It never writes a
loop or a recursion itself — that logic lives inside `CompositeGraphic.print()`.

### Execution order

`graphic.print()` iterates its children in insertion order:
`graphic1` first, then `graphic2`. `graphic1.print()` in turn prints `ellipse1`,
`ellipse2`, `arc`; then `graphic2.print()` prints `rect`. Depth-first, producing the
output below.

## Run It

```bash
# from the repo root
javac structural/composite/graphic/CompositeTest.java
java structural.composite.graphic.CompositeTest
```

Expected output:

```
Ellipse
Ellipse
Arc
Rectangle
```
