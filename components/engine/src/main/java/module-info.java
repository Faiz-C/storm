module org.storm.engine {
    requires transitive org.storm.core;
    requires transitive org.storm.physics;
    requires transitive org.storm.sound;
    requires javafx.media;
    requires javafx.graphics;
    requires lombok;
    requires commons.math3;

    exports org.storm.engine.exception;
    exports org.storm.engine.request;
    exports org.storm.engine.request.types;
    exports org.storm.engine.state;
    exports org.storm.engine;
}
