package ru.serce.api.docker;


import ru.serce.api.docker.entity.Cmd;
import ru.serce.api.docker.entity.Container;
import ru.serce.api.docker.entity.ContainerProcess;
import ru.serce.api.docker.entity.Image;

import java.util.List;

/**
 * Created by serce on 3/9/15.
 */
//@ApplicationComponentInterface
public interface ContainerService {

    List<Image> listImages();

    Image getImage(String repotag);

    void pullImage(String repotag);


    List<Container> listContainers();

    Container createContainer(Image image);

    // mounting API does not clear at this moment
    void startContainer(Container container, String mountPoint);

    void stopContainer(Container container);

    void removeContainer(Container container);

    ContainerProcess exec(Container container, Cmd cmd);
}
