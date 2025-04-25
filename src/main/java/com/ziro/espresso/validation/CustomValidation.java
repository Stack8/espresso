package com.ziro.espresso.validation;

/**
 * Used to mark a bean validation annotation as part of the custom validation group.
 *
 * <p>
 * This is to help control the order in which validations run.
 * For example, we typically want @NotNull validations to run first
 * so that custom validators do not have to include null checks.
 * </p>
 */
public interface CustomValidation {}
