package org.storm.engine.request.types;

import lombok.AllArgsConstructor;
import org.storm.engine.StormEngine;
import org.storm.engine.request.Request;
import org.storm.physics.PhysicsData;
import org.storm.physics.entity.Entity;

import java.util.Map;

/**
 * A Request to change the entity data the PhysicsEngine is using.
 */
@AllArgsConstructor
public class EntityDataAlterationRequest implements Request {

  private final Map<Entity, PhysicsData> entityData;

  @Override
  public void execute(StormEngine stormEngine) {
    stormEngine.getPhysicsEngine().setEntityData(this.entityData);
  }
}
