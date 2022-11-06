package org.storm.sound.manager;

import org.storm.sound.Sound;
import org.storm.sound.exception.SoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * A SoundManager is helpful way of tracking and using sounds within a game
*/
public class SoundManager {

  private static final String SOUND_NOT_FOUND_ERROR_TEMPLATE = "no sound found for id: %s";

  private final Map<String, Sound> soundMap;

  public SoundManager() {
    this.soundMap = new HashMap<>();
  }

  /**
   * Adds the given sound to the SoundManager under the given id
   *
   * @param id the id reference for the Sound
   * @param sound Sound to track
   */
  public void add(String id, Sound sound) {
    this.soundMap.put(id, sound);
  }

  /**
   * Removes the sound associated to the given id reference
   *
   * @param id the id reference for the Sound
   */
  public void remove(String id) {
    this.soundMap.remove(id);
  }

  /**
   * Plays the Sound associated with the given id
   *
   * @param id the id reference for the Sound
   */
  public void play(String id) {
    if (!this.soundMap.containsKey(id)) {
      throw new SoundException(String.format(SOUND_NOT_FOUND_ERROR_TEMPLATE, id));
    }
    this.soundMap.get(id).play();
  }

  /**
   * Stops the Sound associated with the given id
   *
   * @param id the id reference for the Sound
   */
  public void stop(String id) {
    if (!this.soundMap.containsKey(id)) {
      throw new SoundException(String.format(SOUND_NOT_FOUND_ERROR_TEMPLATE, id));
    }
    this.soundMap.get(id).stop();
  }

  /**
   * Pauses the Sound associated with the given id
   *
   * @param id the id reference for the Sound
   */
  public void pause(String id) {
    if (!this.soundMap.containsKey(id)) {
      throw new SoundException(String.format(SOUND_NOT_FOUND_ERROR_TEMPLATE, id));
    }
    this.soundMap.get(id).pause();
  }

  /**
   * Adjusts the volume of the Sound associated with the given id
   *
   * @param id the id reference for the Sound
   * @param volume the new volume for the Sound
   */
  public void adjustVolume(String id, double volume) {
    if (!this.soundMap.containsKey(id)) {
      throw new SoundException(String.format(SOUND_NOT_FOUND_ERROR_TEMPLATE, id));
    }
    this.soundMap.get(id).adjustVolume(volume);
  }

  /**
   * Adjusts the volume of all the Sounds tracked by the SoundManager
   *
   * @param volume the new volume for all the Sounds
   */
  public void adjustAllVolume(double volume) {
    this.soundMap.forEach((id, sound) -> sound.adjustVolume(volume));
  }

  /**
   * Stops all sounds held by the manager
   */
  public void stopAll() {
    this.soundMap.forEach((id, sound) -> sound.stop());
  }

  /**
   * Pauses all sounds held by the manager
   */
  public void pauseAll() {
    this.soundMap.forEach((id, sound) -> sound.pause());
  }
}
