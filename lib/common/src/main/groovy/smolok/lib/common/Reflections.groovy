package smolok.lib.common

class Reflections {

    private static final wrapperClasses = [:]
    static {
        wrapperClasses[int.class] = Integer.class
        wrapperClasses[long.class] = Long.class
        wrapperClasses[short.class] = Short.class
        wrapperClasses[byte.class] = Byte.class
        wrapperClasses[char.class] = Character.class
        wrapperClasses[float.class] = Float.class
        wrapperClasses[double.class] = Double.class
    }

    private static final wrappersOf = [:]
    static {
        wrappersOf[Integer.class] = int.class
        wrappersOf[Long.class] = long.class
        wrappersOf[Short.class] = short.class
        wrappersOf[Byte.class] = byte.class
        wrappersOf[Character.class] = char.class
        wrappersOf[Float.class] = float.class
        wrappersOf[Double.class] = double.class
    }

    static boolean wrappableAwareInstanceOf(Class<?> type, Class<?> instanceOf) {
        if (instanceOf.isAssignableFrom(type)) {
            return true;
        } else {
            Class<?> resolvedType = wrapperClasses.get(type)
            if(resolvedType == null) {
                resolvedType = wrappersOf[type]
            }
            instanceOf.isAssignableFrom(resolvedType)
        }
    }

    static boolean isNumber(Class<?> type) {
        wrapperClasses.containsKey(type) || wrapperClasses.containsValue(type)
    }

    static boolean isJavaLibraryType(Class<?> type) {
        isNumber(type) || type == String.class || type == Date.class;
    }

    static boolean isContainer(Class<?> type) {
        Map.isAssignableFrom(type) || Collection.isAssignableFrom(type)
    }

    static boolean isPojo(Class<?> type) {
        type != Object.class && !isJavaLibraryType(type) && !isContainer(type)
    }

}
