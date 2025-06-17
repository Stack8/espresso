package com.ziro.espresso.javax.annotation.extensions;

import jakarta.annotation.Nonnull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifierDefault;

@Documented
@Nonnull
@TypeQualifierDefault({
    ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR,
    ElementType.FIELD,
    ElementType.METHOD,
    ElementType.PACKAGE,
    ElementType.PARAMETER,
    ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface NonNullByDefault {}
