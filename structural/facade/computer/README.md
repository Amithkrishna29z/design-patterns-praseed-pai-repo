# Facade Pattern — Computer Startup

## Concept

The **Facade** is a structural design pattern that provides a single, simplified
interface to a larger body of code — a set of subsystems that are complex to use
directly. The client talks to the facade instead of coordinating each subsystem
itself.

**Intent:** hide complexity behind one easy-to-use entry point.

**When to use it:**
- A subsystem has many moving parts and clients only need a common, high-level workflow.
- You want to decouple clients from the internal classes so those internals can change freely.
- You want a clear layering boundary between "how to use the system" and "how the system works".

**Trade-off:** the facade can become a god-object if you keep piling responsibilities
onto it. It simplifies the *common* path; clients that need fine-grained control can
still reach the subsystems directly.

## This Example

Starting a computer really involves several coordinated steps across independent
hardware components. Getting the order right is fiddly, and a caller shouldn't have
to know it. `Computer` is the facade that hides those details.

### Subsystems

| Class       | Responsibility                                  |
|-------------|-------------------------------------------------|
| `CPU`       | `freeze()`, `jump(position)`, `execute()`       |
| `Memory`    | `load(position, data)`                          |
| `HardDrive` | `read(lba, size)` — returns the boot sector     |

### The Facade

```java
class Computer {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;

    public void startComputer() {
        cpu.freeze();
        memory.load(BOOT_ADDRESS, hardDrive.read(BOOT_SECTOR, SECTOR_SIZE));
        cpu.jump(BOOT_ADDRESS);
        cpu.execute();
    }
}
```

`startComputer()` encapsulates the full boot sequence: freeze the CPU, read the
boot sector from disk, load it into memory, jump to the boot address, and execute.

### The Client

```java
public class FacadeTest {
    public static void main(String[] args) {
        Computer facade = new Computer();
        facade.startComputer();
    }
}
```

The client makes **one call**. It never touches `CPU`, `Memory`, or `HardDrive`,
and it doesn't know the correct ordering of the boot steps. That knowledge lives
inside the facade.

## Full Code Walkthrough

The whole example lives in a single file, `FacadeTest.java`. It contains the
public entry-point class plus four package-private classes.

### `FacadeTest` — the client / entry point

```java
public class FacadeTest {
    public static void main(String[] args) {
        Computer facade = new Computer();
        facade.startComputer();
    }
}
```

- `main` is the program's entry point.
- It creates one `Computer` object and calls `startComputer()`.
- This is the *entire* client-side code — proof that the facade did its job of
  hiding complexity.

### `CPU` — a subsystem

```java
class CPU {
    public void freeze()            { System.out.println("CPU Freeze ..."); }
    public void jump(long position) { System.out.println("Jumping to the position"); }
    public void execute()           { System.out.println("started execution...."); }
}
```

Models the processor with three low-level operations used during boot:
- `freeze()` — halts the CPU so nothing runs while boot code is being loaded.
- `jump(position)` — moves the instruction pointer to a memory address.
- `execute()` — begins running instructions from that address.

Each method just prints — this is a teaching stub, not a real emulator.

### `Memory` — a subsystem

```java
class Memory {
    public void load(long position, byte[] data) {
        System.out.println("Loading data...");
        return;
    }
}
```

- `load(position, data)` copies a block of bytes into memory at a given address.
- The `data` it receives is whatever `HardDrive.read(...)` returned.
- The explicit `return;` at the end of a `void` method is redundant (does nothing),
  but harmless.

### `HardDrive` — a subsystem

```java
class HardDrive {
    public byte[] read(long lba, int size) {
        System.out.println("Reading the boot sector..");
        return null;
    }
}
```

- `read(lba, size)` reads `size` bytes starting at a logical block address (`lba`).
- Here it returns `null` (stub). In the boot flow that `null` is passed straight
  into `Memory.load(...)`, which is fine because `load` ignores the contents.

### `Computer` — the facade

```java
class Computer {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;
    final static int BOOT_SECTOR  = 256;
    final static int SECTOR_SIZE  = 512;
    final static int BOOT_ADDRESS = 0xFF00;

    public Computer() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
    }

    public void startComputer() {
        cpu.freeze();
        memory.load(Computer.BOOT_ADDRESS, hardDrive.read(Computer.BOOT_SECTOR, Computer.SECTOR_SIZE));
        cpu.jump(BOOT_ADDRESS);
        cpu.execute();
    }
}
```

- **Fields** — the facade *owns* one instance of each subsystem. They are `private`,
  so the outside world cannot reach them directly.
- **Constants** — `BOOT_SECTOR`, `SECTOR_SIZE`, and `BOOT_ADDRESS` (hex `0xFF00`)
  are the fixed hardware parameters of the boot process. `final static` means they
  are shared, read-only values.
- **Constructor** — wires up the subsystems, so a client only writes `new Computer()`.
- **`startComputer()`** — the heart of the pattern. It runs the boot steps *in the
  correct order*:
  1. `cpu.freeze()` — stop the CPU.
  2. `hardDrive.read(BOOT_SECTOR, SECTOR_SIZE)` — read the boot sector from disk…
  3. …and `memory.load(BOOT_ADDRESS, …)` — load those bytes into memory. (The read
     is a nested argument, so it happens first, then its result feeds `load`.)
  4. `cpu.jump(BOOT_ADDRESS)` — point the CPU at the loaded code.
  5. `cpu.execute()` — run it.

The ordering and coordination — the genuinely tricky part — is captured once,
here, instead of being scattered across every caller.

### Execution order

`FacadeTest.main` → `Computer.startComputer` → `freeze` → `read` → `load` →
`jump` → `execute`, producing the output shown below.

## Run It

```bash
# from the repo root
javac structural/facade/computer/FacadeTest.java
java structural.facade.computer.FacadeTest
```

Expected output:

```
CPU Freeze ...
Reading the boot sector..
Loading data...
Jumping to the position
started execution....
```
