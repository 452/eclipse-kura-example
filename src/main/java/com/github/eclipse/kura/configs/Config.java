package com.github.eclipse.kura.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(id = "com.github.eclipse.kura.components.I2CScannerToolComponent", name = "i2c Tool", description = "i2c tool config")
public @interface Config {

    @AttributeDefinition(name = "i2c bus", description = "This i2c bus for default")
    int i2cBus() default 1;

}