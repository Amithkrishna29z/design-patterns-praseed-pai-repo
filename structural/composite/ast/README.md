# Composite Pattern — Expression AST

## Concept

The **Composite** is a structural design pattern that lets you compose objects into
tree structures and then treat individual objects and compositions of objects
**uniformly**. The client calls the same method on a leaf as it does on a whole
branch — it doesn't need to know whether it is holding one value or a whole
sub-expression.

**Intent:** treat "a single thing" and "a group of things" through one common
interface, so recursive structures can be handled without special-casing.

**The three roles:**
- **Component** — the shared interface (or abstract class) declaring operations
  common to both leaves and composites.
- **Leaf** — a primitive object with no children; it does the actual work.
- **Composite** — an object that holds children (leaves or other composites) and
  usually implements each operation by delegating to those children.

**When to use it:**
- You have a **part-whole hierarchy** (files/folders, menus/menu-items, and — as
  here — an arithmetic expression made of sub-expressions).
- You want clients to ignore the difference between a single object and a group.

An **Abstract Syntax Tree (AST)** is a textbook use of Composite: a mathematical
expression like `-(10 + 10)` is naturally a tree. Numbers are leaves; operators are
composites that hold their operands. Because operands are themselves expressions,
the structure nests to any depth.

## This Example

The example models arithmetic expressions. Every expression can be `Evaluate()`-d to
a `double`. An operator node (e.g. `BinaryPlus`) is itself an expression that holds
other expressions, so evaluating it evaluates everything inside — recursively, to any
depth.

### The roles in this code

| Role       | Class(es)                                          | Responsibility                                              |
|------------|----------------------------------------------------|-------------------------------------------------------------|
| Component  | `Expr` (abstract class)                            | Declares `Evaluate()`                                       |
| Leaf       | `NumbericConstant`                                 | Holds a value; `Evaluate()` returns it                      |
| Composite  | `UnaryPlus`, `UnaryMinus`, `BinaryPlus`, `BinaryMinus`, `BinaryMul`, `BinaryDiv` | Hold operand(s); `Evaluate()` combines their results |

### The structure built by `main`

`MainTest` builds the expression `-(10 + 10)`:

```
UnaryMinus
└── BinaryPlus
    ├── NumbericConstant(10)
    └── NumbericConstant(10)
```

Calling `Evaluate()` on the root walks this tree depth-first: `BinaryPlus` adds
`10 + 10` → `20`, then `UnaryMinus` negates it → `-20`.

## Full Code Walkthrough

Each class lives in its own file under `structural/composite/ast/`.

### `Expr` — the Component

```java
public abstract class Expr {
    public abstract double Evaluate();
}
```

The shared abstract base. **Both** leaves and composites extend it, which is exactly
what lets the client treat them the same way. It declares only `Evaluate()` — the one
operation every expression supports.

### `NumbericConstant` — the Leaf

```java
public class NumbericConstant extends Expr {
    private double _value;

    public NumbericConstant(double value) {
        _value = value;
    }

    public double Evaluate() {
        return _value;
    }
}
```

A primitive number. It has no children and implements `Evaluate()` by simply
returning its stored value. This is where recursion **bottoms out** — the actual
values at the leaves of the tree.

### `UnaryMinus` / `UnaryPlus` — single-operand Composites

```java
public class UnaryMinus extends Expr {
    private Expr _right;

    public UnaryMinus(Expr right) {
        _right = right;
    }

    public double Evaluate() {
        return -_right.Evaluate();
    }
}
```

A composite that holds **one** child, `_right`. Its `Evaluate()` first evaluates the
child (the recursive step) and then negates the result. `UnaryPlus` is identical but
returns `_right.Evaluate()` unchanged. Note the field is typed `Expr`, so the operand
can be a leaf **or** another operator — that uniformity is the whole point.

### `BinaryPlus`, `BinaryMinus`, `BinaryMul`, `BinaryDiv` — two-operand Composites

```java
public class BinaryPlus extends Expr {
    Expr _left;
    Expr _right;

    public BinaryPlus(Expr pleft, Expr pright) {
        _left = pleft;
        _right = pright;
    }

    public double Evaluate() {
        return _left.Evaluate() + _right.Evaluate();
    }
}
```

Each binary operator holds **two** children, `_left` and `_right`. `Evaluate()`
recursively evaluates both operands and combines them with the operator's arithmetic.
The four binary classes are identical except for that one operator: `+`, `-`, `*`, `/`
respectively. None of them checks whether an operand is a leaf or a composite —
polymorphism handles that, so an operand can itself be a whole sub-expression.

### `MainTest` — the client / entry point

```java
public class MainTest {
    public static void main(String[] args) {
        Expr e = new UnaryMinus(new BinaryPlus(new NumbericConstant(10), new NumbericConstant(10)));
        double val = e.Evaluate();
        System.out.println("Value is " + val);
    }
}
```

Step by step:
1. Build the tree inside-out: two `NumbericConstant(10)` **leaves**, wrapped in a
   `BinaryPlus` **composite**, wrapped in a `UnaryMinus` **composite**.
2. A composite is passed to another composite's constructor exactly like a leaf would
   be — because both are just `Expr`s. This is the key move that makes the structure
   recursive.
3. Call `Evaluate()` **once** on the root. The single call cascades down the whole
   tree.

The client builds a two-level tree but issues just one `Evaluate()`. It never writes a
loop or a recursion itself — that logic lives inside each operator's `Evaluate()`.

### Execution order

`e.Evaluate()` (the `UnaryMinus`) calls `_right.Evaluate()` — the `BinaryPlus`, which
evaluates its left `NumbericConstant` (`10`) and right `NumbericConstant` (`10`) and
returns `20`. `UnaryMinus` negates that to `-20`. Depth-first, producing the output
below.

## Run It

```bash
# from the repo root
javac structural/composite/ast/*.java
java structural.composite.ast.MainTest
```

Expected output:

```
Value is -20.0
```
