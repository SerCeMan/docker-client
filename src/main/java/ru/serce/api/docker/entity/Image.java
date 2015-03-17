package ru.serce.api.docker.entity;

import java.util.List;

/**
 * Created by Sergey.Tselovalnikov on 3/9/15.
 */
public class Image {
  private final String id;
  private final List<String> repotags;

  public List<String> getRepotags() {
    return repotags;
  }

  public Image(String id, List<String> repotags) {
    this.id = id;
    this.repotags = repotags;
  }

  public String getId() {
    return id;
  }
}
