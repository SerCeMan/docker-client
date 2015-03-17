import org.junit.BeforeClass;
import org.junit.Test;
import ru.serce.api.docker.ContainerService;
import ru.serce.api.docker.ContainerServiceImpl;
import ru.serce.api.docker.entity.Container;
import ru.serce.api.docker.entity.Image;


/**
 * Created by Sergey.Tselovalnikov on 3/9/15.
 */
public class ContainerServiceTest {

  private static ContainerService docker;

  @BeforeClass
  public static void init() {
    docker = new ContainerServiceImpl();
  }

  /**
   * Работает только c настроенным docker на машине
   */
  @Test
  public void test() {
    docker.pullImage("ubuntu:14.04");
    Image image = docker.getImage("ubuntu:14.04");
    Container container = docker.createContainer(image);
    try {
      docker.startContainer(container, "/tmp/");
      String out = docker.execCmd(container, "ls /tmp && echo 1 > /tmp/a && cat /tmp/a");
      System.out.println(out);
    } finally {
      try {
        docker.stopContainer(container);
        docker.removeContainer(container);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }
}