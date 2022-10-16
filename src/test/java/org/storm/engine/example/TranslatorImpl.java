package org.storm.engine.example;

import javafx.scene.input.KeyEvent;
import org.storm.core.input.Translator;
import org.storm.engine.KeyActionConstants;

public class TranslatorImpl implements Translator<KeyEvent, String> {

  @Override
  public String translate(KeyEvent keyEvent) {
    switch (keyEvent.getCode()) {
      case SPACE:
        return KeyActionConstants.SPACE;
      case NUMPAD1:
        return KeyActionConstants.ONE;
      case NUMPAD2:
        return KeyActionConstants.TWO;
      case NUMPAD3:
        return KeyActionConstants.THREE;
      case NUMPAD4:
        return KeyActionConstants.FOUR;
      case W:
        return KeyActionConstants.W;
      case S:
        return KeyActionConstants.S;
      case A:
        return KeyActionConstants.A;
      case D:
        return KeyActionConstants.D;
      default:
        return "";
    }
  }

}
