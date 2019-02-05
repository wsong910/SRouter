package chuxin.shimo.shimowendang.smrouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface Router {
    /**
     * 1.如果定义目标路由名称为"/main"，那么只接受"/main"或"/main?id=xx&userinfo=xx"，不可接受多一级，如"/main/extra"或"/main/extra?id=xx&useinfo=xx"
     * 2.如果定义目标路由名称为"/main/:userid",那么可接受""
     * @return
     */
    String[] value();

    String flag() default "-1";

    Class<?>[] interceptors() default {};

    String[] stringParams() default "";

    String[] intParams() default "";

    String[] longParams() default "";

    String[] booleanParams() default "";

    String[] shortParams() default "";

    String[] floatParams() default "";

    String[] doubleParams() default "";

    String[] byteParams() default "";

    String[] charParams() default "";
}
