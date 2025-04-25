package com.ziro.espresso.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Inherited
@Target(TYPE)
@Retention(RUNTIME)
@GroupSequence({Default.class, CustomValidation.class})
@interface CustomValidator {}
