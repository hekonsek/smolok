package smolok.paas

interface ImageLocatorResolver {

    boolean canResolveImage(String imageLocator)

    String resolveImage(String imageLocator)

}