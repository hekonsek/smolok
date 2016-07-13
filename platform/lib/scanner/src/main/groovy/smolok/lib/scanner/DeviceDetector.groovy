package smolok.lib.scanner

interface DeviceDetector {

    List<Device> detectDevices(int sshPort)

    List<Device> detectDevices()

    void close()

}