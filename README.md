# Project Storm #
Project Storm is a personal side project of mine to create a transparent and well-rounded set of 2D game engine components
and a game engine itself using said components (StormEngine). The goal of this project was to gain more knowledge of how
game engines work and share it in a fun way. Each of the separate components (storm-sound, storm-core, storm-animation, 
storm-physics) can be used independently or together (as we can see with the StormEngine). The project outlines the key
components of a 2D game engine and implements them transparently to help others understand how things work.
These components do not cover everything an engine such as Unity might do, but they cover the core concepts of most game
engines.

# Why Separate Libraries? #
The choice of having separate smaller libraries is because I found that certain components of the engine could be used as
standalone and without the need for the entire engine if desired. It also opens up the ability for people to create their
own version of the StormEngine by putting together these components (and maybe even their own) in their own way.

# Why Java? #
Since my goal with this was to be more educational than competitive Java served as a good middle ground between usability
and performance. Java is cross-platform, a good language for showcasing high level design concepts, is widely used, and 
many people understand it. Now this doesn't mean the components or engine itself was written with Java in mind. Quite the 
opposite actually, I wrote the components and engine with the goal of them being as language independent as possible. So 
there is no real reason you cannot say port the Storm projects to C++ for example. Plus, there are very few game engines
in Java, so why not help out?

# Version #
Version is currently at 1.0.0

# Usage #
In order to use this project there is a small amount of setup required.

1) clone the other required libs: storm-sound, storm-physics, storm-core (these are not on a public maven repo)
2) mvn clean install them all including this project as well
3) due to javafx no longer being bundled with java for jdk 13+ (which this engine uses) you have to install it separately
   and run your java application with the following VM args: `-path ${PATH_TO_FX} --add-modules=javafx.controls,javafx.media`
4) add the StormEngine maven dependency to your project:
   ````
   <dependency>
      <groupId>org.storm</groupId>
      <artifactId>storm</artifactId>
      <version>1.0.0</version>
   </dependency>
   ````
   
After you do the above three steps the StormEngine should be ready to use!