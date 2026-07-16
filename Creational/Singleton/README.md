# Singleton Pattern

The **Singleton** is a creational design pattern that ensures a class has **only one instance** and provides a **single global point of access** to it.

## When to use it

- Exactly one object is needed to coordinate actions (e.g. a configuration store, logger, connection pool, cache).
- You want that single object to be reachable from anywhere, but you don't want it to be a free-floating global variable that anyone can overwrite.

## The code in this folder

Two files make up the example:

| File | Role |
|------|------|
| `SingleInstance.java` | The singleton class itself. |
| `Caller.java` | A client that fetches the instance twice and proves both references point to the same object. |

---

## `SingleInstance.java` — the singleton

```java
public class SingleInstance {

    private int i = 0;

    private SingleInstance() {      // (1) private constructor
        i = 0;
    }

    private static SingleInstance _st = null;   // (2) the single, static instance

    public int Bump() {             // (3) mutates the shared state
        return ++i;
    }

    public int GetCount() {
        return i;
    }

    public static SingleInstance GetInstance() {   // (4) global access point
        if (_st == null) {
            _st = new SingleInstance();
        }
        return _st;
    }
}
```

### How each piece enforces "one instance"

1. **Private constructor** — `private SingleInstance()` means no other class can write `new SingleInstance()`. This is the key that blocks uncontrolled creation.
2. **Private static field `_st`** — one class-level slot that holds the only instance. `static` means it belongs to the class, not to any object, so it is shared by everyone. It starts as `null`.
3. **Instance state (`i`, `Bump`, `GetCount`)** — ordinary members. Because everyone shares the same object, calls to `Bump()` accumulate on the same counter. This is what the demo uses to *prove* it's a singleton.
4. **`GetInstance()`** — the public gate. This is **lazy initialization**:
   - First call: `_st` is `null`, so it creates the object and stores it.
   - Every later call: `_st` is already set, so it returns the existing object.

   Result: no matter how many times you call `GetInstance()`, you always get the **same** object.

---

## `Caller.java` — the demo

```java
public class Caller {
    private static SingleInstance CreateSingleInstance() {
        return SingleInstance.GetInstance();
    }

    public static void main(String[] args) {
        SingleInstance st = Caller.CreateSingleInstance();  // gets the instance
        st.Bump();                                          // counter: 0 -> 1

        SingleInstance mt = Caller.CreateSingleInstance();  // gets the SAME instance
        mt.Bump();                                          // counter: 1 -> 2

        if (st.GetCount() == mt.GetCount())                 // both read 2
            System.out.println("SingleTon object");
        else
            System.out.println("Not a Singleton Object");
    }
}
```

### Step by step

1. `st = CreateSingleInstance()` — first `GetInstance()` call creates the object; `i = 0`.
2. `st.Bump()` — increments the shared counter to **1**.
3. `mt = CreateSingleInstance()` — `GetInstance()` returns the **already existing** object, so `st` and `mt` are two references to *one* object.
4. `mt.Bump()` — increments the *same* counter to **2**.
5. `st.GetCount()` and `mt.GetCount()` both return **2** because they read the same `i`.

Since the counts match, the program prints:

```
SingleTon object
```

If `st` and `mt` were separate objects, each would have its own `i == 1`, the counts would still be equal here — but the real signal is that the two `Bump()` calls **stacked on one counter** (reaching 2), which only happens when a single object is shared.

---

## How to compile and run

From this folder:

```sh
javac SingleInstance.java Caller.java
java Caller
```

Expected output:

```
SingleTon object
```

---

## Caveat: this version is not thread-safe

The lazy `GetInstance()` here uses a plain `if (_st == null)` check. If two threads call it at the same time, both can see `null` and each create an instance — breaking the guarantee. Common fixes:

- Make `GetInstance()` `synchronized`.
- Use the **initialization-on-demand holder** idiom (a static nested class).
- Use an `enum` singleton.
- Eagerly initialize: `private static final SingleInstance _st = new SingleInstance();`

For a single-threaded teaching example like this one, the simple version is fine and keeps the pattern easy to read.
