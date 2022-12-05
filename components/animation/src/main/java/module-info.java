module org.storm.animations {
  requires javafx.graphics;
  requires transitive org.storm.core;
  requires lombok;

  exports org.storm.animations.sprite;
  exports org.storm.animations;
}
