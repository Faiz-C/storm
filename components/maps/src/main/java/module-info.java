module org.storm.maps {
  requires javafx.graphics;
  requires transitive org.storm.core;
  requires transitive org.storm.physics;
  requires lombok;

  exports org.storm.maps.exception;
  exports org.storm.maps.layer;
  exports org.storm.maps.tile;
  exports org.storm.maps;
}
