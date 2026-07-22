# First.java — A Minimal Java Program

`First.java` is a starter example showing the smallest useful shape of a Java
program: one plain data class and one class with a `main` method that uses it.
All code lives in [`First.java`](First.java).

## The two classes

### `Simple` — a plain data holder

```java
class Simple {
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

`Simple` wraps a single `String` field, `message`. It follows the classic
JavaBean shape:

- **Private field** — `message` is hidden from outside code.
- **Constructor** — sets `message` to `null` when a `Simple` is created.
- **Getter / setter** — `getMessage()` reads the value, `setMessage(...)`
  writes it. The setter uses `this.message = message` to tell the field apart
  from the parameter of the same name.

`Simple` is **package-private** (no `public` keyword), so it is only visible
inside the `instatiation.first` package.

### `First` — the entry point

```java
public class First {
    public static void main(String[] args) {
        Simple sm = new Simple();
        sm.setMessage("Hello World");
        System.out.println(sm.getMessage());
    }
}
```

`First` holds the `main` method — where the JVM starts running. It:

1. Creates a `Simple` object with `new Simple()` (message is `null`).
2. Stores `"Hello World"` into it via `setMessage(...)`.
3. Reads it back with `getMessage()` and prints it.

`First` is `public` and its name matches the file name, as Java requires for the
public class in a file.

## Running it

From the project root (the directory that contains the `instatiation` folder):

```bash
javac instatiation/first/First.java
java instatiation.first.First
```

Output:

```
Hello World
```

## Takeaways

- A `.java` file may contain several classes, but only one may be `public`, and
  its name must match the file.
- Field access goes through getters and setters rather than touching the field
  directly — the encapsulation habit this example is meant to build.
- Execution begins at `public static void main(String[] args)`.
