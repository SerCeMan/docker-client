package ru.serce.api.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import ru.serce.api.docker.entity.Cmd;
import ru.serce.api.docker.entity.Container;
import ru.serce.api.docker.entity.ContainerProcess;
import ru.serce.api.docker.entity.Image;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by Sergey.Tselovalnikov on 3/9/15.
 */
//@ApplicationComponentImplementation
public class ContainerServiceImpl implements ContainerService {

  private final DefaultDockerClient docker;

  public ContainerServiceImpl() {
    try {
      docker = DefaultDockerClient.fromEnv().build();
    } catch (DockerCertificateException e) {
      throw new ContainerServiceException("Error building docker client", e);
    }
  }

  @Override
  public List<Image> listImages() {
    try {
      return docker.listImages()
          .stream()
          .map(image -> new Image(image.id(), image.repoTags()))
          .collect(Collectors.toList());
    } catch (DockerException | InterruptedException e) {
      throw new ContainerServiceException("Unable to get images", e);
    }
  }

  @Override
  public Image getImage(String repotag) {
    return listImages()
        .stream()
        .filter(image -> image.getRepotags().contains(repotag))
        .findFirst()
        .orElseThrow(() -> new ContainerServiceException("Unable to find image with repotag " + repotag));
  }

  @Override
  public void pullImage(String repotag) {
    try {
      docker.pull(repotag);
    } catch (DockerException | InterruptedException e) {
      throw new ContainerServiceException("Unable to pull image", e);
    }
  }

  @Override
  public List<Container> listContainers() {
    try {
      return docker.listContainers()
          .stream()
          .map(container -> new Container(container.id()))
          .collect(Collectors.toList());
    } catch (DockerException | InterruptedException e) {
      throw new ContainerServiceException("Unable get list of containers", e);
    }
  }

  @Override
  public Container createContainer(Image image) {
    ContainerConfig config = ContainerConfig.builder()
        .image(image.getId())
        .cmd("sh", "-c", "while :; do sleep 1; done")
        .build();
    try {
      ContainerCreation creation = docker.createContainer(config);
      String id = creation.id();
      return new Container(id);
    } catch (DockerException | InterruptedException e) {
      throw new ContainerServiceException("Unable to create container", e);
    }
  }

  @Override
  public void startContainer(Container container, String mountPoint) {
    HostConfig hostConfig = HostConfig.builder()
        .binds(mountPoint + ":/tmp:rw")
        .build();
    try {
      docker.startContainer(container.getId(), hostConfig);
    } catch (InterruptedException | DockerException e) {
      throw new ContainerServiceException("Unable to start container.", e);
    }
  }

  @Override
  public void stopContainer(Container container) {
    try {
      docker.killContainer(container.getId());
    } catch (DockerException | InterruptedException e) {
      throw new ContainerServiceException("Unable to stop container", e);
    }
  }

  @Override
  public void removeContainer(Container container) {
    try {
      docker.removeContainer(container.getId());
    } catch (DockerException | InterruptedException e) {
      throw new ContainerServiceException("Unable to stop container", e);
    }
  }

    @Override
    public ContainerProcess exec(Container container, Cmd cmd) {
        return null;
    }

  public String execCmd(Container container, String cmd) {
    String[] command = {"sh", "-c", cmd};
    try {
      String execId = docker.execCreate(container.getId(), command, DockerClient.ExecParameter.STDOUT, DockerClient.ExecParameter.STDERR);
      LogStream output = docker.execStart(execId);
      return output.readFully();
    } catch (InterruptedException | DockerException e) {
      throw new ContainerServiceException("Error during exec cmd='" + cmd + "'", e);
    }
  }
}
