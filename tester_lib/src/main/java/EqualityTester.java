import java.util.Arrays;
import java.util.function.BiPredicate;

public enum EqualityTester {
    NONE((a, b) -> {
        return true;
    }), OBJECT((a, b) -> {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }), ARRAY((a, b) -> {
        if (a == null || b == null) {
            return false;
        }

        if (!a.getClass().isArray() || !b.getClass().isArray()) {
            return false;
        }

        Object[] aa = (Object[]) a;
        Object[] bb = (Object[]) b;

        return Arrays.equals(aa, bb);
    }), FLOATING_POINT((a, b) -> {
        if (a == null || b == null) {
            return false;
        }

        if (!(a instanceof Number && b instanceof Number)) {
            return false;
        }

        double aa = ((Number) a).doubleValue();
        double bb = ((Number) b).doubleValue();

        final double FLOAT_ACCURACY = .001;

        return (aa - FLOAT_ACCURACY < bb && aa + FLOAT_ACCURACY > bb);
    });

    private BiPredicate<Object, Object> tester;

    EqualityTester(BiPredicate<Object, Object> tester) {
        this.tester = tester;
    }

    public boolean test(Object a, Object b) {
        return tester.test(a, b);
    }
}