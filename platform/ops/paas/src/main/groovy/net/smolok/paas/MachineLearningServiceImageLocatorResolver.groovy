package net.smolok.paas

import static smolok.lib.common.Mavens.artifactVersionFromDependenciesProperties

class MachineLearningServiceImageLocatorResolver implements ImageLocatorResolver {

    @Override
    boolean canResolveImage(String imageLocator) {
        imageLocator == 'machine-learning'
    }

    @Override
    List<ServiceConfiguration> resolveImage(String imageLocator) {
        def smolokVersion = artifactVersionFromDependenciesProperties('net.smolok', 'smolok-paas').get()
        [new ServiceConfiguration("smolok/service-machinelearning:${smolokVersion}", [SPARK_USER: 'root'])]
    }

}
