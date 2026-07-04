# JumpNRun - A demo

A 2D platformer prototype built with Java and [libGDX](https://libgdx.com/).

## What it does

The player character moves and jumps, with sprite-sheet animations for idle, walking, running, jumping, and landing states, driven by a simple state machine.

### Controls

- **A/←** — walk left
- **D/→** — walk right
- **Walk left/right + W/↑** — run
- **Space** — jump

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
