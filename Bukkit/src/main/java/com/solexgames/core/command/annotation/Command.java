package com.solexgames.core.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    String label();

    String[] aliases() default {};
    String permission() default "";

    boolean async() default false;
    boolean consoleOnly() default false;
    boolean hidden() default true;

}
