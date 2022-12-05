module org.storm.sound {
  requires javafx.graphics;
  requires javafx.media;
  requires org.slf4j;
  requires lombok;

  exports org.storm.sound.exception;
  exports org.storm.sound.manager;
  exports org.storm.sound.types;
  exports org.storm.sound;
}
