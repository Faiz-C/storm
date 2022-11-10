module org.storm.core {
  requires org.slf4j;
  requires java.desktop;
  requires java.datatransfer;
  requires lombok;
  requires javafx.graphics;

  exports org.storm.core.exception;
  exports org.storm.core.input;
  exports org.storm.core.render;
  exports org.storm.core.ui;
  exports org.storm.core.update;
  exports org.storm.core.utils;
}