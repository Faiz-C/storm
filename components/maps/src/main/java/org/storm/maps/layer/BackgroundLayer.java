package org.storm.maps.layer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.storm.core.ui.Resolution;
import org.storm.core.utils.ImageUtils;
import org.storm.maps.exception.MapLayerException;
import org.storm.physics.entity.Entity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Setter
public class BackgroundLayer extends Layer {

  private final Image background;

  public BackgroundLayer(Image background, Resolution resolution) {
    super(false, resolution);
    this.background = background;
  }

  public BackgroundLayer(String imagePath, Resolution resolution) throws FileNotFoundException {
    this(new Image(new FileInputStream(imagePath)), resolution);
  }

  @Override
  public void addEntity(Entity entity) {
    throw new MapLayerException("cannot add entities to the background layer");
  }

  @Override
  public void removeEntity(Entity entity) {
    throw new MapLayerException("cannot remove entities to the background layer");
  }

  @Override
  public void render(GraphicsContext graphicsContext, double x, double y) {
    Image croppedBackground = ImageUtils.crop(this.background, (int)x, (int)y,
      (int)(x + this.resolution.getWidth()), (int)(y + this.resolution.getHeight()));
    graphicsContext.drawImage(croppedBackground, 0, 0);
  }

  @Override
  public void update(double time, double elapsedTime) {
    // Default behaviour involves no updating but something like Parallax might involve the need to update
  }
}
