package com.noname.server;

import java.lang.annotation.*;

@Target(value= ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface Mapped {
    String uri();
    Methods method();
}
