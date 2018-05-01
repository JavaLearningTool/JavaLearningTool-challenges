import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@interface TestedMember {
    String name() default "";

    EqualityTester equality() default EqualityTester.NONE;

    Stringifier stringConverter() default Stringifier.OBJECT;

    int[] paramIsClass() default {};

    boolean returnIsClass() default false;
}