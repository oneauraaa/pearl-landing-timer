# Pearl Landing Timer Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a Minecraft Fabric 1.21.11 client mod that predicts thrown ender pearl landing time, exposes options through Cloth Config and Mod Menu, and lets the HUD timer be dragged.

**Architecture:** The mod is client-only. A tick manager scans loaded ender pearls, simulates vanilla projectile motion forward until collision, and stores predictions for HUD and particle marker output. Config is loaded from JSON and edited through Cloth Config via Mod Menu.

**Tech Stack:** Java 21, Gradle, Fabric Loom remap plugin, Minecraft 1.21.11, Yarn 1.21.11+build.5, Fabric API 0.141.3+1.21.11, Cloth Config 21.11.153, Mod Menu 17.0.0.

---

### Task 1: Scaffold Project

**Files:**
- Create: `settings.gradle`
- Create: `build.gradle`
- Create: `gradle.properties`
- Create: `src/main/resources/fabric.mod.json`
- Create: `src/main/resources/assets/pearl-landing-timer/lang/en_us.json`

- [ ] Create a Fabric client-only Gradle project with the verified 1.21.11 dependency coordinates.
- [ ] Configure `net.fabricmc.fabric-loom-remap` because 1.21.11 is still an obfuscated/Yarn target.
- [ ] Add Fabric API, Cloth Config, and Mod Menu dependencies.
- [ ] Add Fabric metadata with client and Mod Menu entrypoints.

### Task 2: Config And Mod Menu

**Files:**
- Create: `src/client/java/com/selim/pearllandingtimer/PearlLandingTimerConfig.java`
- Create: `src/client/java/com/selim/pearllandingtimer/PearlLandingTimerModMenu.java`

- [ ] Implement a small JSON config stored in Fabric Loader config dir.
- [ ] Expose toggles and numeric settings through Cloth Config.
- [ ] Register the screen through Mod Menu's `ModMenuApi`.

### Task 3: Prediction And Display

**Files:**
- Create: `src/client/java/com/selim/pearllandingtimer/PearlLandingTimerClient.java`
- Create: `src/client/java/com/selim/pearllandingtimer/PearlPrediction.java`
- Create: `src/client/java/com/selim/pearllandingtimer/PearlPredictor.java`

- [ ] Register client tick and HUD callbacks.
- [ ] Find loaded `EnderPearlEntity` instances, optionally filtering to pearls owned by the local player.
- [ ] Simulate projectile motion with gravity, drag, and collision raycasts.
- [ ] Display the shortest predicted countdown on the HUD.
- [ ] Register a keybind that opens a HUD editor screen where the countdown preview can be dragged and saved.
- [ ] Spawn a subtle client-side particle marker at predicted landing points.

### Task 4: Verification

**Files:**
- Modify any source files with mapping/API fixes found during compile.

- [ ] Run `gradle wrapper` if Gradle exists locally, otherwise use the installed Gradle command directly.
- [ ] Run `./gradlew build` or `gradle build`.
- [ ] Fix compile errors caused by 1.21.11 API naming differences.
- [ ] Confirm the remapped jar is produced under `build/libs`.
