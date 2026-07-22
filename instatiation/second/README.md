# Second.java — Instantiating a Class by Name

`Second.java` shows how to create an object **without calling `new` directly**.
Instead of `new Simple()`, it asks `java.beans.Beans` to load and instantiate a
class given only its **name as a String**. This is the first step toward
patterns (like factories) where the concrete type is decided at runtime rather
than hard-coded.

The code lives in [`Second.java`](Second.java) and [`Simple.java`](Simple.java).

## The two classes

### `Simple` — the class being created

```java
public class Simple {
    private String message;

    public Simple() {
        message = null;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) {
        this.message = message;
    }
}
```

A plain data holder with a single `String` field and its getter/setter. It must
be **`public`** — `Beans.instantiate` refuses to create non-public classes.

### `Second` — instantiation by name

```java
public class Second {
    public static void main(String[] args) {
        try {
            Simple sm = (Simple)Beans.instantiate(
                Second.class.getClassLoader(), "instatiation.second.Simple");
            sm.setMessage("Hello world...");
            System.out.println(sm.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to load the class....");
        }
    }
}
```

`Beans.instantiate(classLoader, name)` loads the class named by `name` using the
given class loader and creates an instance via its no-argument constructor. The
result is typed as `Object`, so it is cast back to `Simple`.

## Three things that must be right

`Beans.instantiate` is stricter than `new`, and each requirement maps to a real
error you hit if you get it wrong:

1. **Checked exceptions** — it throws `IOException` **and**
   `ClassNotFoundException`. Both must be caught (or declared with `throws`),
   otherwise the code will not compile.
2. **Fully-qualified name** — pass `"instatiation.second.Simple"`, not just
   `"Simple"`. Only the short name throws `ClassNotFoundException` at runtime.
3. **A usable class loader** — `Second.class.getClassLoader()` returns the loader
   that loaded your app. (`Second.class.getClass().getClassLoader()` is a common
   mistake: it returns the loader of `java.lang.Class`, not your code.)

And, as noted above, the target class (`Simple`) must be `public`.

## Running it

From the project root (the directory that contains the `instatiation` folder):

```bash
javac instatiation/second/*.java
java instatiation.second.Second
```

Output:

```
Hello world...
```

## Takeaways

- You can create objects from a **class name string**, deferring the concrete
  type until runtime — the seed of the Factory idea.
- `Beans.instantiate` trades the convenience of `new` for extra rules: checked
  exceptions, a fully-qualified name, a real class loader, and a public class.
