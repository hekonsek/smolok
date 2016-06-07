package smolok.lib.docker

class Container {

    private final String image

    private final String name

    private final String net

    Container(String image, String name, String net) {
        this.image = image
        this.name = name
        this.net = net
    }

    static Container container(String image, String name) {
        new Container(image, name, null)
    }

    static Container container(String image) {
        new Container(image, null, null)
    }

    String image() {
        image
    }

    String name() {
        name
    }

    String net() {
        net
    }

}
