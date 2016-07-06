package smolok.lib.scanner

import groovy.transform.ToString

@ToString
public class Device {

    public static final String DEVICE_RASPBERRY_PI_2 = 'RaspberryPi2'
    		
    private final InetAddress address

    private final String type

    public Device(InetAddress address, String type) {
        this.address = address
        this.type = type
    }

    InetAddress address() {
        address
    }

    String type() {
        type
    }

}
