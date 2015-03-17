package ru.serce.api.docker.entity;

/**
 * Created by Sergey.Tselovalnikov on 3/9/15.
 */
public class Container {
  private final String id;

  public Container(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
