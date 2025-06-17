package com.ziro.espresso.javax.annotation.extensions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides additional context for deprecated elements.
 * <p>
 * This annotation should be used in conjunction with {@code @Deprecated} to provide
 * information about when the element was deprecated and when it's scheduled for removal.
 * </p>
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({
    ElementType.CONSTRUCTOR,
    ElementType.FIELD,
    ElementType.LOCAL_VARIABLE,
    ElementType.METHOD,
    ElementType.PACKAGE,
    ElementType.MODULE,
    ElementType.PARAMETER,
    ElementType.TYPE
})
public @interface DeprecatedInfo {

    /**
     * Reference to the story/issue where this element was marked as deprecated.
     * This field is mandatory.
     *
     * @return the story/issue reference where the element was deprecated
     */
    String deprecatedIn();

    /**
     * Reference to the story/issue where this element is scheduled for removal.
     * This field is mandatory.
     *
     * @return the story/issue reference for removal
     */
    String removingIn();
}
