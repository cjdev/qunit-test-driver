package com.googlecode.qunitTestDriver.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

import org.junit.runner.Description


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QUnitResults {
}
