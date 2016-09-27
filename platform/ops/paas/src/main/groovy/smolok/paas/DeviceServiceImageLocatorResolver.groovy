package smolok.paas

import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties

class DeviceServiceImageLocatorResolver implements ImageLocatorResolver {

    @Override
    boolean canResolveImage(String imageLocator) {
        imageLocator == 'device'
    }

    @Override
    String resolveImage(String imageLocator) {
        def smolokVersion = artifactVersionFromDependenciesProperties('net.smolok', 'smolok-paas').get()
        "smolok/service-device:${smolokVersion}"
    }

}
