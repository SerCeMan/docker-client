import com.spotify.docker.client.*;
import com.spotify.docker.client.messages.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by serce on 3/2/15.
 */
public class Main {
    public static void main(String[] args) {
        DockerClient docker = null;
        String id = null;
        try {
            docker = DefaultDockerClient.fromEnv().build();

            // Pull image
            docker.pull("busybox");

            // Create container with exposed ports
//            final String[] ports = {"22", "80"};
            String[] ports = {};
            final ContainerConfig config = ContainerConfig.builder()
                    .image("busybox")
                    .exposedPorts(ports)
                    .volumes("/tmp")
                    .cmd("sh", "-c", "while :; do sleep 1; done")
                    .build();

            // bind container ports to host ports
            final Map<String, List<PortBinding>> portBindings = new HashMap<>();
            for (String port : ports) {
                List<PortBinding> hostPorts = new ArrayList<>();
                hostPorts.add(PortBinding.of("0.0.0.0", getPort(port)));
                portBindings.put(port, hostPorts);
            }
            final HostConfig hostConfig = HostConfig.builder()
                    .portBindings(portBindings)
                    .binds("/tmp:/tmp:rw")
                    .build();

            final ContainerCreation creation = docker.createContainer(config);
            id = creation.id();

            // Inspect container
            final ContainerInfo info = docker.inspectContainer(id);
            System.out.println(info.toString());

// Start container
            docker.startContainer(id, hostConfig);

            System.out.println(docker.listContainers());
// Exec command inside running container with attached STDOUT and STDERR
            final String[] command = {"sh", "-c", "ls /tmp && echo 1 > /tmp/a && cat /tmp/a"};
            final String execId = docker.execCreate(id, command, DockerClient.ExecParameter.STDOUT, DockerClient.ExecParameter.STDERR);
            final LogStream output = docker.execStart(execId);
            final String execOutput = output.readFully();
            System.out.println("OUT: " + execOutput);

        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            if(e.getCause() != null)
//            {
//                System.out.println(e.getCause().getMessage());
//            }
            e.printStackTrace();
        } finally {
            try {
                docker.killContainer(id);
                docker.removeContainer(id);
            } catch (DockerException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getPort(String port) {
        return "80".equals(port) ? "14235" : "12458";
    }
}