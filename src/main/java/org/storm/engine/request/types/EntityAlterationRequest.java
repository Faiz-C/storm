package org.storm.engine.request.types;

import lombok.AllArgsConstructor;
import org.storm.engine.StormEngine;
import org.storm.engine.request.Request;
import org.storm.physics.entity.Entity;

import java.util.List;

/**
 * A Request to change the list of entities that the PhysicsEngine is using.
 */
@AllArgsConstructor
public class EntityAlterationRequest implements Request {

  private final List<Entity> newEntities;

  @Override
  public void execute(StormEngine stormEngine) {
    stormEngine.getPhysicsEngine().setEntities(this.newEntities);
  }

}
