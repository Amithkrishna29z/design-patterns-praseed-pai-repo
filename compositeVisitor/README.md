# Composite + Visitor — Expression AST

## Concept

This example combines **two** patterns over the same object structure:

- **Composite** builds the tree. An arithmetic expression like `-(10 + 20)` is a
  part-whole hierarchy: numbers are leaves, operators are composites that hold
  operand expressions. See the standalone version in
  [`structural/composite/ast`](../structural/composite/ast/README.md).
- **Visitor** adds operations *to* that tree without changing the node classes.
  Each new thing you want to do (evaluate, print, count nodes, ...) is a new
  visitor class. See the standalone version in
  [`behavioral/visitor`](../behavioral/visitor/README.md).

The two fit together naturally. Composite gives you a recursive structure; Visitor
gives you a clean way to walk it and do work. The **composite node itself drives the
traversal** — its `accept()` forwards the visitor to its children before visiting
itself. So the client calls `accept()` **once** on the root and the whole tree gets
walked.

**Why combine them?** In the plain Composite example, every operation (`Evaluate`)
lives *inside* the node classes. Adding a second operation (say, printing) means
editing every node. With Visitor, the nodes only know how to `accept` a visitor — the
operations live outside, in visitor classes you can add freely.

## The roles in this code

| Pattern role                 | Class(es)                                   | Responsibility                                             |
|------------------------------|---------------------------------------------|------------------------------------------------------------|
| Component (Composite)        | `Expr` (abstract class)                      | Declares `accept(ExprVisitor)`                             |
| Leaf (Composite)             | `NumericConstant`                            | Holds a value; `accept` visits itself                     |
| Composite (Composite)        | `BinaryPlus`, `UnaryMinus`                   | Hold child `Expr`s; `accept` recurses then visits itself  |
| Visitor interface (Visitor)  | `ExprVisitor`                                | One `visit` overload per concrete node type               |
| Concrete visitors (Visitor)  | `EvaluateVisitor`, `PrintVisitor`            | The operations — computed / rendered from the same tree   |

## The structure built by `main`

`CompositeVisitorTest` builds `-(10 + 20)`:

```
UnaryMinus
└── BinaryPlus
    ├── NumericConstant(10)
    └── NumericConstant(20)
```

Two visitors run over this **same** tree — one prints it, one evaluates it — and
neither required a change to the node classes.

## Full Code Walkthrough

Each class lives in its own file under `compositeVisitor/`.

### `Expr` — the Component

```java
abstract class Expr {
    public abstract void accept(ExprVisitor visitor);
}
```

Compare this to the plain Composite version, whose base declared
`abstract double Evaluate()`. Here the base declares **`accept`** instead. That is the
whole shift: nodes no longer hard-code an operation — they accept a visitor that
brings the operation with it.

### `ExprVisitor` — the Visitor interface

```java
interface ExprVisitor {
    void visit(NumericConstant node);
    void visit(BinaryPlus node);
    void visit(UnaryMinus node);
}
```

One overloaded `visit` **per concrete node type**. Any operation over the tree must
know how to handle every kind of node, so it implements this interface.

### `NumericConstant` — the Leaf

```java
public class NumericConstant extends Expr {
    private double _value;

    public NumericConstant(double value) {
        _value = value;
    }

    public double getValue() {
        return _value;
    }

    public void accept(ExprVisitor visitor) {
        visitor.visit(this);
    }
}
```

A leaf has no children, so `accept` just visits itself — this is where traversal
**bottoms out**. Note it exposes `getValue()` rather than doing arithmetic; the *work*
now lives in the visitors, not the node.

### `BinaryPlus` — a two-operand Composite

```java
public class BinaryPlus extends Expr {
    private Expr _left;
    private Expr _right;

    public BinaryPlus(Expr left, Expr right) {
        _left = left;
        _right = right;
    }

    public Expr getLeft()  { return _left; }
    public Expr getRight() { return _right; }

    public void accept(ExprVisitor visitor) {
        _left.accept(visitor);    // recurse into left child
        _right.accept(visitor);   // recurse into right child
        visitor.visit(this);      // then visit this node
    }
}
```

This is the heart of the combination. `accept` does two jobs:
1. **Composite recursion** — it forwards the visitor to each child. Because the
   children are typed `Expr`, they may be leaves or whole sub-expressions; the code
   doesn't care.
2. **Visitor dispatch** — after the children, it calls `visitor.visit(this)` on
   itself.

Children are visited **before** the parent — a **post-order** walk. That order matters
for the visitors below (they rely on child results being ready first).

### `UnaryMinus` — a single-operand Composite

```java
public class UnaryMinus extends Expr {
    private Expr _right;

    public UnaryMinus(Expr right) {
        _right = right;
    }

    public Expr getRight() { return _right; }

    public void accept(ExprVisitor visitor) {
        _right.accept(visitor);
        visitor.visit(this);
    }
}
```

Same shape with one child: recurse into it, then visit itself.

### `EvaluateVisitor` — operation #1

```java
public class EvaluateVisitor implements ExprVisitor {
    private Deque<Double> _stack = new ArrayDeque<>();

    public double getResult() {
        return _stack.peek();
    }

    public void visit(NumericConstant node) {
        _stack.push(node.getValue());
    }

    public void visit(BinaryPlus node) {
        double right = _stack.pop();
        double left = _stack.pop();
        _stack.push(left + right);
    }

    public void visit(UnaryMinus node) {
        _stack.push(-_stack.pop());
    }
}
```

Because nodes are visited post-order, a **stack** is the natural tool:
- Visiting a `NumericConstant` **pushes** its value.
- By the time `visit(BinaryPlus)` runs, both operands have already been visited, so
  their results are the top two stack entries. It **pops** them, adds, and **pushes**
  the sum.
- `visit(UnaryMinus)` pops one value, negates it, pushes it back.

When the root has been visited, the single remaining stack entry is the answer, which
`getResult()` peeks.

### `PrintVisitor` — operation #2

```java
public class PrintVisitor implements ExprVisitor {
    private Deque<String> _stack = new ArrayDeque<>();

    public String getResult() {
        return _stack.peek();
    }

    public void visit(NumericConstant node) {
        _stack.push(String.valueOf(node.getValue()));
    }

    public void visit(BinaryPlus node) {
        String right = _stack.pop();
        String left = _stack.pop();
        _stack.push("(" + left + " + " + right + ")");
    }

    public void visit(UnaryMinus node) {
        _stack.push("-" + _stack.pop());
    }
}
```

Structurally **identical** to `EvaluateVisitor`, but it builds `String`s instead of
computing `double`s. This is the payoff of Visitor: a whole new operation over the tree
with **zero** changes to `Expr`, `NumericConstant`, `BinaryPlus`, or `UnaryMinus`.

### `CompositeVisitorTest` — the client / entry point

```java
public class CompositeVisitorTest {
    public static void main(String[] args) {
        Expr e = new UnaryMinus(new BinaryPlus(new NumericConstant(10), new NumericConstant(20)));

        PrintVisitor printer = new PrintVisitor();
        e.accept(printer);
        System.out.println(printer.getResult());

        EvaluateVisitor evaluator = new EvaluateVisitor();
        e.accept(evaluator);
        System.out.println("Value is " + evaluator.getResult());
    }
}
```

Step by step:
1. Build the tree inside-out — two `NumericConstant` leaves inside a `BinaryPlus`
   inside a `UnaryMinus`. A composite is passed to another composite's constructor
   just like a leaf, because both are `Expr`. (Composite.)
2. Call `e.accept(printer)` **once**. That single call cascades through the whole tree,
   post-order, letting the visitor build the string. (Visitor.)
3. Call `e.accept(evaluator)` on the **same** tree to compute its value.

The client never writes a loop or recursion — the traversal lives in each node's
`accept`, and the work lives in the visitors.

## Execution order (evaluating `-(10 + 20)`)

`e.accept(evaluator)` on the `UnaryMinus`:

1. `UnaryMinus.accept` → `_right.accept` (the `BinaryPlus`)
2. `BinaryPlus.accept` → left `NumericConstant(10).accept` → `visit` pushes `10.0`
3. `BinaryPlus.accept` → right `NumericConstant(20).accept` → `visit` pushes `20.0`
4. `BinaryPlus.accept` → `visit(BinaryPlus)` pops `20, 10`, pushes `30.0`
5. back in `UnaryMinus.accept` → `visit(UnaryMinus)` pops `30`, pushes `-30.0`

Stack now holds just `-30.0` → the result.

## Run It

```bash
# from the repo root
javac compositeVisitor/*.java
java compositeVisitor.CompositeVisitorTest
```

Expected output:

```
-(10.0 + 20.0)
Value is -30.0
```

## Trade-off

- ✅ **Easy to add operations** — a new visitor class (e.g. `CountNodesVisitor`)
  requires no change to any node.
- ❌ **Hard to add node types** — a new node (e.g. `BinaryMul`) forces a new
  `visit(BinaryMul)` in `ExprVisitor` *and* in every existing visitor.

Combine Composite with Visitor when your tree's **node types are stable** but the
**operations over the tree keep growing**. If instead you keep adding node types and
have only one operation, plain Composite (with the operation baked into each node, as
in `structural/composite/ast`) is simpler.
