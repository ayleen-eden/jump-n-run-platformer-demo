# JumpNRun - A demo

<img width="448" height="272.5" src="https://github.com/user-attachments/assets/9a344be2-2efe-4345-9b34-9b280ba67210" />

A 2D platformer prototype built with Java and [libGDX](https://libgdx.com/).

## What it does

The player character moves and jumps, with sprite-sheet animations for idle, walking, running, jumping, and landing states, driven by a simple state machine.

### Controls

- **A/←** — walk left
- **D/→** — walk right
- **Walk left/right + W/↑** — run
- **Space** — jump

[See some gameplay!](https://youtu.be/GMwI2tp1v2c?si=an3HCUximD3Tg5kM)

## Tech

- Java
- libGDX (LWJGL3 desktop backend)
- Gradle

## Status

Paused/Discontinued — core movement and animation states are implemented; jump animation timing and level design are still on the to-do list.

## Running it
```bash
./gradlew lwjgl3:run
```
