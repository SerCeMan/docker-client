package ru.serce.api.docker;

/**
 * Indicates what error happened during work with docker client
 *
 * Created by Sergey.Tselovalnikov on 3/9/15.
 */
public class ContainerServiceException extends RuntimeException {
  public ContainerServiceException(String message) {
    super(message);
  }

  public ContainerServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
