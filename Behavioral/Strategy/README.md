# Strategy Pattern

## Concept

The **Strategy** pattern is a *behavioral* design pattern that lets you define a
family of algorithms, put each of them in a separate class, and make their
objects interchangeable at runtime.

Instead of hard-coding a single algorithm into a class, the class holds a
reference to a *strategy* object and delegates the work to it. Because every
strategy shares the same interface, you can swap one for another without
changing the code that uses it.

### When to use it

- You have several variants of an algorithm and want to switch between them at runtime.
- You want to avoid large `if/else` or `switch` blocks that select behavior.
- You want to isolate the algorithm's details from the code that uses it.

### Participants

| Role | In this example | Responsibility |
|------|-----------------|----------------|
| **Strategy** | `Strategy` (interface) | Declares the common operation `execute(a, b)`. |
| **Concrete Strategy** | `ConcreteStrategyAdd`, `ConcreteStrategySubtract`, `ConcreteStrategyMultiply` | Implement the algorithm in different ways. |
| **Context** | `Context` | Holds a `Strategy` and delegates calls to it. |
| **Client** | `StrategyTest.main` | Picks a concrete strategy and hands it to the context. |

## Main Code

```java
// The common interface every algorithm implements.
interface Strategy {
    int execute(int a, int b);
}

// Interchangeable concrete algorithms.
class ConcreteStrategyAdd implements Strategy {
    public int execute(int a, int b) { return a + b; }
}
class ConcreteStrategySubtract implements Strategy {
    public int execute(int a, int b) { return a - b; }
}
class ConcreteStrategyMultiply implements Strategy {
    public int execute(int a, int b) { return a * b; }
}

// The Context delegates to whichever strategy it was given.
class Context {
    private Strategy strategy;

    public Context(Strategy strategy) {
        this.strategy = strategy;
    }

    public int executeStrategy(int a, int b) {
        return strategy.execute(a, b);
    }
}

// The client selects and swaps strategies at runtime.
public class StrategyTest {
    public static void main(String[] args) {
        Context context;

        context = new Context(new ConcreteStrategyAdd());
        int resultA = context.executeStrategy(5, 2);   // 7

        context = new Context(new ConcreteStrategySubtract());
        int resultB = context.executeStrategy(5, 2);   // 3

        context = new Context(new ConcreteStrategyMultiply());
        int resultC = context.executeStrategy(5, 2);   // 10
    }
}
```

## How it works

1. The client creates a `Context`, injecting a concrete strategy through the constructor.
2. When `context.executeStrategy(5, 2)` is called, the `Context` forwards the
   call to `strategy.execute(5, 2)` without knowing which algorithm runs.
3. Swapping the algorithm is just a matter of passing a different strategy
   object — no `Context` code changes.

## Run it

```bash
javac StrategyTest.java
java Strategy.StrategyTest
```

## Key benefit

The `Context` is **closed for modification but open for extension**: adding a new
operation (e.g. division) means writing one new class that implements
`Strategy` — no existing code has to change.
