# Third.java — Instantiating via the Reflection API

`Third.java` creates an object the low-level way: it uses **`java.lang.reflect`**
to load a class by name, find its no-argument constructor, and invoke it. Where
[`Second`](../second/README.md) delegated to `Beans.instantiate`, here we do the
same work by hand — this is essentially what factories and frameworks do under
the hood.

The code lives in [`Third.java`](Third.java) and [`Simple.java`](Simple.java).

## The class being created

```java
public class Simple {
    private String message;

    public Simple() { message = null; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
```

A plain data holder with a public no-argument constructor — exactly what the
reflection code below looks for.

## How reflection instantiation works

```java
Class c = Class.forName("instatiation.third.Simple");   // 1. load the class

Constructor[] ctors = c.getDeclaredConstructors();       // 2. list constructors
Constructor ct = null;
for (int i = 0; i < ctors.length; i++) {
    ct = ctors[i];
    if (ct.getParameterTypes().length == 0) break;       // 3. pick the no-arg one
}

Simple s = (Simple) ct.newInstance();                    // 4. invoke it
s.setMessage("Hello world");
System.out.println(s.getMessage());
```

1. **`Class.forName(name)`** loads the class object for the given name.
2. **`getDeclaredConstructors()`** returns every constructor the class declares.
3. The loop scans for a constructor that takes **zero parameters**.
4. **`newInstance()`** calls that constructor, producing a new `Simple`. The
   result is `Object`, so it is cast back to `Simple`.

## Known issue in the current code

As written, line 12 passes the **short** name:

```java
Class c = (Class)Class.forName("Simple");
```

`Class.forName` requires the **fully-qualified** name, so this throws
`ClassNotFoundException` at runtime. The fix:

```java
Class c = Class.forName("instatiation.third.Simple");
```

A few other cleanups worth noting:

- The `(Class)` cast is redundant — `forName` already returns a `Class`.
- The `if (c == null)` check is dead code — `forName` throws rather than
  returning `null`.
- The `throws` clause lists `IOException`, `ClassNotFoundException`,
  `InstantiationException`, **and** `Exception`; since `Exception` covers them
  all, the rest are redundant (and `IOException` is never actually thrown).

## Running it (after applying the fix above)

From the project root (the directory that contains the `instatiation` folder):

```bash
javac instatiation/third/*.java
java instatiation.third.Third
```

Output:

```
Hello world
```

## Takeaways

- The reflection API (`Class.forName` → `getDeclaredConstructors` →
  `newInstance`) lets you build objects when the type is only known as a string
  at runtime.
- Like `Beans.instantiate`, `Class.forName` needs the **fully-qualified** class
  name — the most common mistake in this style of code.
