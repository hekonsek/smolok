package smolok.lib.docker

import com.google.common.collect.ImmutableList

/**
 * Represents CommandLineDocker container to be created.
 */
class Container {

    private final String image

    private final String name

    private final String net

    private final Map volumes

    private final String[] arguments

    Container(String image, String name, String net, Map volumes, String[] arguments) {
        this.image = image
        this.name = name
        this.net = net
        this.volumes = volumes
        this.arguments = arguments
    }

    static Container container(String image, String name) {
        new Container(image, name, null, [:])
    }

    static Container container(String image) {
        new Container(image, null, null, [:])
    }

    // Getters

    String image() {
        image
    }

    String name() {
        name
    }

    String net() {
        net
    }

    Map volumes() {
        volumes
    }

    List<String> arguments() {
        ImmutableList.copyOf(arguments)
    }

}
