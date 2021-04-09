package org.storm.engine.example;

import javafx.scene.input.KeyEvent;
import org.storm.core.input.Translator;
import org.storm.engine.KeyActionConstants;

import java.util.Collections;
import java.util.Set;


public class TranslatorImpl implements Translator<KeyEvent, Set<String>> {

  @Override
  public Set<String> translate(KeyEvent keyEvent) {
    switch (keyEvent.getCode()) {
      case SPACE:
        return Collections.singleton(KeyActionConstants.SPACE);
      case DOWN:
        return Collections.singleton(KeyActionConstants.DOWN);
      case RIGHT:
        return Collections.singleton(KeyActionConstants.RIGHT);
      case LEFT:
        return Collections.singleton(KeyActionConstants.LEFT);
      case UP:
        return Collections.singleton(KeyActionConstants.UP);
      default:
        return Collections.emptySet();
    }
  }

}
