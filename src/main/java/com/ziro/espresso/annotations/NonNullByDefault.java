package com.ziro.espresso.annotations;

import jakarta.annotation.Nonnull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * Indicates that all elements in the annotated scope should be considered {@code @Nonnull} by default.
 * This annotation can be applied at various levels to enforce non-null constraints without explicitly
 * marking each element with {@code @Nonnull}.
 *
 * <p>When this annotation is present, it applies to all:</p>
 * <ul>
 *   <li>Annotations</li>
 *   <li>Constructors</li>
 *   <li>Fields</li>
 *   <li>Methods</li>
 *   <li>Packages</li>
 *   <li>Parameters</li>
 *   <li>Types</li>
 * </ul>
 *
 * <p>This annotation is retained at runtime and is documented in the generated JavaDoc.</p>
 *
 * @see Nonnull
 * @see TypeQualifierDefault
 */
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
