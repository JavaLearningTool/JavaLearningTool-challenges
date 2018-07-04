import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class TestUtils {

    private TestUtils() {
    }

    public static Class<?> classExists(String className) {
        Class<?> ret = null;
        try {
            ret = Class.forName(className);
        } catch (ClassNotFoundException e) {
        }

        return ret;
    }

    public static boolean classIsStatic(Class<?> cls) {
        return Modifier.isStatic(cls.getModifiers());
    }

    public static boolean classIsAbstract(Class<?> cls) {
        return Modifier.isAbstract(cls.getModifiers());
    }

    public static boolean classIsFinal(Class<?> cls) {
        return Modifier.isFinal(cls.getModifiers());
    }

    public static boolean classIsPrivate(Class<?> cls) {
        return Modifier.isPrivate(cls.getModifiers());
    }

    public static boolean classIsProtected(Class<?> cls) {
        return Modifier.isProtected(cls.getModifiers());
    }

    public static boolean classIsPublic(Class<?> cls) {
        return Modifier.isPublic(cls.getModifiers());
    }

    public static boolean isSubClass(Class<?> superClass, Class<?> subClass) {
        return superClass.isAssignableFrom(subClass);
    }

    public static boolean classIsEnum(Class<?> cls) {
        return cls.isEnum();
    }

    public static boolean classIsInterface(Class<?> cls) {
        return cls.isInterface();
    }

    public static boolean classIsLocalClass(Class<?> cls) {
        return cls.isLocalClass();
    }

    public static boolean classIsMemberClass(Class<?> cls) {
        return cls.isMemberClass();
    }

    public static boolean constructorEquals(Constructor c1, Constructor c2) {

        if (c1 == null || c2 == null) {
            return false;
        }

        if (c1.getModifiers() != c2.getModifiers()) {
            return false;
        }

        if (!Arrays.equals(c1.getParameterTypes(), c2.getParameterTypes())) {
            return false;
        }

        return true;
    }

    public static Constructor<?> hasAccessibleConstructor(Class<?> cls, Class<?>[] paramTypes) {
        Constructor<?> ret = null;
        try {
            ret = cls.getConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
        }

        return ret;
    }

    public static Constructor<?> hasDeclaredConstructor(Class<?> cls, Class<?>[] paramTypes) {
        Constructor<?> ret = null;
        try {
            ret = cls.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
        }

        return ret;
    }

    // Not sure about this name
    public static Method accessibleDeclaredMethodExists(Class<?> cls, String methodName, Class<?>[] paramTypes) {
        Method met = null;
        try {
            met = cls.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
        }

        return met;
    }

    public static String constructorToString(int modifier, String name, Class<?>[] params) {
        return String.format("%s %s(%s)", Modifier.toString(modifier), name, paramToString(params));
    }

    public static String constructorToString(Constructor constructor) {
        return constructorToString(constructor.getModifiers(), constructor.getDeclaringClass().getSimpleName(),
                constructor.getParameterTypes());
    }

    public static Method declaredMethodExists(Class<?> cls, String methodName, Class<?>[] paramTypes) {
        Method met = null;
        try {
            met = cls.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
        }

        return met;
    }

    public static String methodToString(int modifier, Class<?> ret, String name, Class<?>[] params) {
        return String.format("%s %s %s(%s)", Modifier.toString(modifier), ret.getSimpleName(), name,
                paramToString(params));
    }

    public static String paramToString(Class<?>[] params) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            if (i == params.length - 1) {
                ret.append(params[i].getSimpleName());
            } else {
                ret.append(params[i].getSimpleName() + ", ");
            }
        }

        return ret.toString();
    }

    public static boolean methodEquals(Method m1, Method m2) {

        if (m1 == null || m2 == null) {
            return false;
        }

        if (!m1.getName().equals(m2.getName())) {
            return false;
        }

        if (m1.getModifiers() != m2.getModifiers()) {
            return false;
        }

        // Only worry about whether or not is return something
        if (methodHasReturnType(m1, Void.TYPE) != methodHasReturnType(m2, Void.TYPE)) {
            return false;
        }

        if (!Arrays.equals(m1.getParameterTypes(), m2.getParameterTypes())) {
            return false;
        }

        return true;
    }

    public static boolean methodEquals(Method expectedMethod, Method actualMethod, boolean returnIsClass,
            int[] paramIsClass, Class<?> actual) {

        if (expectedMethod == null || actualMethod == null) {
            return false;
        }

        if (!expectedMethod.getName().equals(actualMethod.getName())) {
            return false;
        }

        if (expectedMethod.getModifiers() != actualMethod.getModifiers()) {
            return false;
        }

        if (returnIsClass) {
            if (expectedMethod.getReturnType().isArray() != actualMethod.getReturnType().isArray()) {
                return false;
            } else if (expectedMethod.getReturnType().isArray() && actualMethod.getReturnType().isArray()) {
                int expectedDimensionCount = 0;
                Class<?> expectedArrayClass = expectedMethod.getReturnType();
                while (expectedArrayClass.isArray()) {
                    expectedDimensionCount++;
                    expectedArrayClass = expectedArrayClass.getComponentType();
                }
                int actualDimensionCount = 0;
                Class<?> actualArrayClass = actualMethod.getReturnType();
                while (actualArrayClass.isArray()) {
                    actualDimensionCount++;
                    actualArrayClass = actualArrayClass.getComponentType();
                }

                // Check array dimensions
                if (expectedDimensionCount != actualDimensionCount) {
                    return false;
                }

                // check array type
                if (!actualArrayClass.equals(actual)) {
                    return false;
                }

            } else if (!actualMethod.getReturnType().equals(actual)) {
                return false;
            }
        } else if (!expectedMethod.getReturnType().equals(actualMethod.getReturnType())) {
            return false;
        }

        Class<?>[] expectedParams = expectedMethod.getParameterTypes();
        Class<?>[] actualParams = actualMethod.getParameterTypes();

        if (expectedParams.length != actualParams.length) {
            return false;
        }

        for (int i = 0; i < expectedParams.length; i++) {
            final int ii = i;
            if (IntStream.of(paramIsClass).anyMatch(x -> x == ii)) {
                int expectedDimensionCount = 0;
                Class<?> expectedArrayClass = expectedParams[i];
                while (expectedArrayClass.isArray()) {
                    expectedDimensionCount++;
                    expectedArrayClass = expectedArrayClass.getComponentType();
                }
                int actualDimensionCount = 0;
                Class<?> actualArrayClass = actualParams[i];
                while (actualArrayClass.isArray()) {
                    actualDimensionCount++;
                    actualArrayClass = actualArrayClass.getComponentType();
                }

                // Check array dimensions
                if (expectedDimensionCount != actualDimensionCount) {
                    return false;
                }

                // check array type
                if (!actualArrayClass.equals(actual)) {
                    return false;
                }
            } else if (!expectedParams[i].equals(actualParams[i])) {
                return false;
            }

        }

        return true;
    }

    public static boolean methodHasReturnType(Method method, Class<?> type) {
        return method.getReturnType().equals(type);
    }

    public static boolean methodIsDefault(Method method) {
        return method.isDefault();
    }

    public static boolean methodThrowsExceptions(Method method, Class<? extends Throwable>... exceptions) {
        return Arrays.equals(method.getExceptionTypes(), exceptions);
    }

    public static boolean methodHasModifiers(Method method, int mods) {
        return method.getModifiers() == mods;
    }

    public static boolean methodIsStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    public static boolean methodIsAbstract(Method method) {
        return Modifier.isAbstract(method.getModifiers());
    }

    public static boolean methodIsFinal(Method method) {
        return Modifier.isFinal(method.getModifiers());
    }

    public static boolean methodIsPrivate(Method method) {
        return Modifier.isPrivate(method.getModifiers());
    }

    public static boolean methodIsProtected(Method method) {
        return Modifier.isProtected(method.getModifiers());
    }

    public static boolean methodIsPublic(Method method) {
        return Modifier.isPublic(method.getModifiers());
    }

    public static boolean methodHasVarArgs(Method method) {
        return method.isVarArgs();
    }

    /**
     * Gets all of the fields in all of the super classes.
     * 
     * @return a list of all fields in all super classes.
     */
    public static List<Field> getAllSuperFields(Class<?> cls) {
        cls = cls.getSuperclass();
        List<Field> ret = new ArrayList<>();

        while (cls != null) {
            ret.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }

        return ret;
    }

    // Not sure about this name
    public static Field accessibleDeclaredFieldExists(Class<?> cls, String fieldName, Class<?> fieldType) {
        Field field = null;
        try {
            field = cls.getField(fieldName);
            if (!field.getType().equals(fieldType)) {
                field = null;
            }
        } catch (NoSuchFieldException e) {
        }

        return field;
    }

    public static Field declaredFieldExists(Class<?> cls, String fieldName, Class<?> fieldType) {
        Field field = null;
        try {
            field = cls.getDeclaredField(fieldName);
            if (!field.getType().equals(fieldType)) {
                field = null;
            }
        } catch (NoSuchFieldException e) {
        }

        return field;
    }

    /**
     * Returns if fields are equal
     * 
     * @param f1 a field to test for equality
     * @param f2 another field to test for equality
     */
    public static boolean fieldEquals(Field f1, Field f2) {

        if (f1 == null || f2 == null) {
            return false;
        }

        if (!f1.getName().equals(f2.getName())) {
            return false;
        }

        /*
         * Test if the fields aren't from the same class. This has a potential to go
         * wrong if the tested class has the same name as another class
         */
        if (!f1.getDeclaringClass().getSimpleName().equals(f2.getDeclaringClass().getSimpleName())) {
            return false;
        }

        if (f1.getModifiers() != f2.getModifiers()) {
            return false;
        }

        if (!typeEqual(f1.getGenericType(), f2.getGenericType())) {
            return false;
        }

        return true;
    }

    /**
     * Returns if types are equal. Pass in generic types if applicable
     * 
     * @param t1 a type to test for equality
     * @param t2 another type to test for equality
     */
    public static boolean typeEqual(Type t1, Type t2) {

        // Possible early exit
        if (!t1.equals(t2)) {
            return false;
        }

        if ((t1 instanceof ParameterizedType && !(t2 instanceof ParameterizedType))
                || (t2 instanceof ParameterizedType && !(t1 instanceof ParameterizedType))) {
            return false;
        }

        if (t1 instanceof ParameterizedType) {
            ParameterizedType paramType1 = (ParameterizedType) t1;
            Type[] params1 = paramType1.getActualTypeArguments();

            ParameterizedType paramType2 = (ParameterizedType) t2;
            Type[] params2 = paramType2.getActualTypeArguments();

            if (params1.length != params2.length) {
                return false;
            }

            for (int i = 0; i < params1.length; i++) {
                if (!typeEqual(params1[i], params2[i])) {
                    return false;
                }
            }
        }

        // Everything was equal
        return true;
    }

    /**
     * Convert the field to a String
     * 
     * @param modifier the fields modifiers
     * @param type     the type of the field (Use the generic type if applicable)
     * @param name     The name of the field
     * @return String version of field
     */
    public static String fieldToString(int modifier, Type type, String name) {
        return String.format("%s %s %s", Modifier.toString(modifier), typeToString(type), name);
    }

    /**
     * Converts a Type to a String. Handles the case when the type is generic
     * 
     * @param type the type to convert to a String
     * @return String version of type
     */
    public static String typeToString(Type type) {

        String parameterizedTypes = "";

        if (type instanceof ParameterizedType) {
            parameterizedTypes += "<";
            ParameterizedType paramType = (ParameterizedType) type;

            Type[] params = paramType.getActualTypeArguments();
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Class<?>) {
                    parameterizedTypes += typeToString(params[i]);
                    if (i < params.length - 1) {
                        parameterizedTypes += ", ";
                    }
                }
            }
            parameterizedTypes += ">";

            if (type instanceof Class<?>) {
                return ((Class) type).getSimpleName() + parameterizedTypes;
            } else {
                try {
                    Class cls = Class.forName(paramType.getRawType().getTypeName());
                    return cls.getSimpleName() + parameterizedTypes;
                } catch (ClassNotFoundException e) {
                    return type.getTypeName();
                }
            }

        } else {
            try {
                Class cls = Class.forName(type.getTypeName());
                return cls.getSimpleName();
            } catch (ClassNotFoundException e) {
                return type.getTypeName();
            }
        }
    }

    public static boolean fieldHasModifiers(Field method, int mods) {
        return method.getModifiers() == mods;
    }

    public static boolean fieldIsStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    public static boolean fieldIsAbstract(Field field) {
        return Modifier.isAbstract(field.getModifiers());
    }

    public static boolean fieldIsFinal(Field field) {
        return Modifier.isFinal(field.getModifiers());
    }

    public static boolean fieldIsPrivate(Field field) {
        return Modifier.isPrivate(field.getModifiers());
    }

    public static boolean fieldIsProtected(Field field) {
        return Modifier.isProtected(field.getModifiers());
    }

    public static boolean fieldIsPublic(Field field) {
        return Modifier.isPublic(field.getModifiers());
    }
}