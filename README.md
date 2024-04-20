# Storm Engine

## _What is the Storm Engine?_
The Storm Engine is an open source and transparent 2D game engine written in <s>Java</s> Kotlin. It aims to be a complete package for
those looking to build 2D games while also offering the core components separately. It's also built to be easily
extended for those wanting to add their own components. 

The framework which the Storm Engine offers is not tightly coupled to <s>Java</s> Kotlin, aside from the use of JavaFx behind the covers
for certain portions. This allows the framework to be ported to other languages if wanted. 

## _Why Build Another Game Engine?_
There are a couple of reasons for this, but the most important reason was to learn. I wanted to learn how the internals
of a game engine work and for me the best way to do that was to write them myself. This reasoning is also why I chose
to make it open source and transparent, even writing the framework and documentation in an easy-to-understand way. I 
want others to be able to look at this project to learn and try things out themselves.

## _Repo Structure and Components_
The Storm Engine is actually a monorepo which includes the engine along with all its core components as separate modules.
There are quite a few benefits to separating the components into individual modules, here are some:

* Better separation of code made it easier to develop and maintain
* Components can be used on their own
* For those learning it can be easier to handle the information in self-contained sections
* Monorepo pattern + Gradle made managing the components as separate modules easy

Below are the individual components of the Storm Engine:

### _[Core](components/core)_
The main components of the engine reside here. The interfaces and code in this component match very closely to the core
concepts of game engines in general including [input handling](components/core/src/main/java/org/storm/core/input), 
[rendering](components/core/src/main/java/org/storm/core/render), [updating](components/core/src/main/java/org/storm/core/update),
and [UI](components/core/src/main/java/org/storm/core/ui).

### _[Sound](components/sound)_
A small component which defines how the engine manages sound. It encompasses both BGM (Background Music) and sound
effects. It provides a [SoundManager](components/sound/src/main/java/org/storm/sound/manager/SoundManager.java) to help track and use a collection of sounds as well.

If you'd like to run the tests for any reason you will have to add in your own songs/sounds to the test resources.
The files used in existing tests are *bgm.mp3*, *bgm2.mp3* and *effect.mp3*.

### _[Physics](components/physics)_
The largest, most complex, and my favourite component of the engine. It encompasses the Storm Engine's math and physics 
components (e.g., collision detection and resolution) and offers an out-of-the-box impulse resolution strategy 
([ImpulseResolutionPhysicsEngine](components/physics/src/main/java/org/storm/physics/ImpulseResolutionPhysicsEngine.java))
to allow for a quick start when working with it. The Storm Engine can also be provided a custom 
[PhysicsEngine](components/physics/src/main/java/org/storm/physics/ImpulseResolutionPhysicsEngine.java) in case you want
to make one of your own.


The collision detection strategy uses the [SAT (Separation Axis Theorem)](https://en.wikipedia.org/wiki/Hyperplane_separation_theorem)
as its base and adds in interaction with circles for a wider coverage. Since the engine is in 2D we deal with Euclidean
Space and this theorem can be applied fairly easily once understood. 

### _[Animation](components/animation)_
A small component which focuses on how the engine could handle animations. It also includes the ability to handle sprite animation
as I found that to be a common use case. **This is not a required component for the Storm Engine, just an extension if wanted**.

### _[Maps](components/maps)_
This component offers a potential way of handling game maps with the engine. It offers a game map in the form of map layers
and game objects on those layers. It also provides Tile support out-of-the-box as that is a common way to build 2D maps.
**This is not a required component for the Storm Engine, just an extension if wanted**.

### _[Engine](components/engine)_
This component includes the [StormEngine](components/engine/src/main/java/org/storm/engine/StormEngine.java) which 
combines the [core](components/core), [physics](components/physics) and [sound](components/sound) components together 
to offer an out-of-the-box engine to use. It also uses the concept of states through the abstract [State](components/engine/src/main/java/org/storm/engine/state/State.java)
to encapsulate portions of a game together (e.g., a level or a menu).

Note, for similar reasons the test sound referenced by *bgm.mp3* which would be found under *components/engine/src/test/resources*
was **excluded**. If you would like to run the Gradle execution tasks for this component please substitute *bgm.mp3* with
a song/sound first.

## _How to Use_

1. Install [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=21) (Java 21.0.2) if you haven't already.
2. Clone this repo.
3. Use gradle wrapper to build the component(s) you would like to: `./gradlew build`, `./gradlew :components:{component}:build`, 
   or through your IDE (e.g., Intellij).
4. Use gradle wrapper to install the component(s) you would like to use: `./gradlew :component:{component}:publishToMavenLocal`
   or through your IDE (e.g., Intellij).
5. You can now use the component(s) as dependencies in your other projects.
