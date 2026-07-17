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
