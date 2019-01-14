import java.util.function.Function;
import java.util.Arrays;

public enum Stringifier {
    OBJECT((a) -> {
        
        if (a == null) {
            return "null";
        }

        return a.toString();
    }), ARRAY((arr) -> {

        if (arr == null) {
            return "null";
        }

        if (!arr.getClass().isArray()) {
            return arr.toString();
        }

        Object[] a = (Object[]) arr;

        return Arrays.toString(a);
    }), DEEP_ARRAY((arr) -> {
        if (arr == null) {
            return "null";
        }

        if (!arr.getClass().isArray()) {
            return arr.toString();
        }

        Object[] a = (Object[]) arr;        

        return Arrays.deepToString(a);
    });

    private Function<Object, String> converter;

    Stringifier(Function<Object, String> converter) {
        this.converter = converter;
    }

    public String convert(Object a) {
        return converter.apply(a);
    }
}