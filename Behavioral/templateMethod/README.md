# Template Method Pattern

## Concept

The **Template Method** pattern is a *behavioral* design pattern that defines the
*skeleton* of an algorithm in a base class method, while deferring one or more
specific steps to subclasses.

The base class fixes the overall structure and order of the algorithm in a
single `public` method (the *template method*). It calls one or more `abstract`
steps that each subclass fills in. Subclasses can change *how* an individual
step works, but they cannot change the *shape* of the algorithm itself.

### When to use it

- Several classes share the same overall algorithm but differ in a few steps.
- You want to lock down the sequence of steps and prevent subclasses from
  reordering or skipping them.
- You want to avoid duplicating the common parts of the algorithm across
  subclasses.

### Participants

| Role | In this example | Responsibility |
|------|-----------------|----------------|
| **Abstract Class** | `Logger` | Defines the template method `Log(...)` and declares the abstract step `DoLog(...)`. |
| **Template Method** | `Logger.Log(app, key, cause)` | Fixes the algorithm: build the log line, then delegate to `DoLog`. |
| **Primitive Step** | `Logger.DoLog(logitem)` | The abstract hook each subclass must implement. |
| **Concrete Classes** | `DbLogger`, `FileLogger`, `NullLogger` | Provide their own version of `DoLog`. |
| **Client** | `TemplateMethod.main` | Obtains a logger and calls `Log`. |

## Main Code

```java
// The abstract class owns the algorithm skeleton.
abstract class Logger {
    // Subclasses fill in this single step.
    protected abstract boolean DoLog(String logitem);

    // Template method: fixes the structure, defers the detail to DoLog.
    public boolean Log(String app, String key, String cause) {
        return DoLog(app + " " + key + " " + cause);
    }
}

// Each concrete logger only customizes the DoLog step.
class DbLogger extends Logger {
    protected boolean DoLog(String logitem) {
        System.out.println("Db log " + logitem);
        return true;
    }
}

class FileLogger extends Logger {
    protected boolean DoLog(String logitem) {
        System.out.println("File Log " + logitem);
        return true;
    }
}

class NullLogger extends Logger {
    protected boolean DoLog(String logitem) {
        System.out.println("Ignoring the log");
        return true;
    }
}
```

## How it works

1. The client calls the **template method** `Log(app, key, cause)`.
2. `Log` performs the common work — assembling the log line — and then calls the
   `DoLog` step.
3. Because `DoLog` is overridden by the subclass, the *specific* behavior (write
   to DB, write to file, or ignore) runs — but the overall sequence never
   changes.

## Run it

```bash
javac TemplateMethod.java
java Behavioral.templateMethod.TemplateMethod
```

## Key benefit

The algorithm's structure lives in **one place** (`Log`). Adding a new kind of
logger means writing one new subclass that implements `DoLog` — the shared
skeleton is reused, not copied.
