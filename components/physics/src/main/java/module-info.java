module org.storm.physics {
  requires javafx.graphics;
  requires transitive org.storm.core;
  requires lombok;
  requires commons.math3;

  exports org.storm.physics.collision;
  exports org.storm.physics.constants;
  exports org.storm.physics.entity;
  exports org.storm.physics.enums;
  exports org.storm.physics.math;
  exports org.storm.physics.math.geometry;
  exports org.storm.physics.math.geometry.shapes;
  exports org.storm.physics.structures;
  exports org.storm.physics.transforms;
  exports org.storm.physics;
}
