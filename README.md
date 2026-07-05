# JumpNRun - A Demo

> **Status**: paused/discontinued

<img width="448" height="272.5" src="https://github.com/user-attachments/assets/9a344be2-2efe-4345-9b34-9b280ba67210" />

A 2D platformer prototype. Built entirely with Java and [libGDX](https://libgdx.com/).

## What it does

The player character moves and jumps using a simple but effective state machine.
It handles sprite-sheet animations seamlessly across multiple states: idle, walking, running, jumping, and landing. 

[🎥 See some gameplay here!](https://youtu.be/GMwI2tp1v2c?si=an3HCUximD3Tg5kM)

## Controls

* **A / ←** — Walk left
* **D / →** — Walk right
* **Walk left/right + W / ↑** — Run
* **Space** — Jump

## Tech Stack

* Java
* libGDX (LWJGL3 desktop backend)
* Gradle

## Running it

Fire up the demo from your terminal:

```bash
./gradlew lwjgl3:run
```