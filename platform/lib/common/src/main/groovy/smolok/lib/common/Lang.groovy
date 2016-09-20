package smolok.lib.common

class Lang {

    private Lang() {
    }

    static <X, Y> Y nullOr(X object, Closure<Y> provider) {
        if(object == null) {
            null
        } else {
            provider(object)
        }
    }

    static <T> T doWith(T object, Closure closure) {
        closure(object)
        object
    }

}
