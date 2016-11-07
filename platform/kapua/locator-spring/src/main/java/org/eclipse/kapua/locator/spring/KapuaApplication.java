/*******************************************************************************
 * Copyright (c) 2011, 2016 Red Hat and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.kapua.locator.spring;

import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Bootstraps Spring Boot application capable of loading modules available in a classpath.
 */
@SpringBootApplication(scanBasePackages = "org.eclipse.kapua")
public class KapuaApplication {

    private static final Logger LOG = getLogger(KapuaApplication.class);

    // Execution points

    public ConfigurableApplicationContext run(String... args) {
        return new SpringApplicationBuilder(KapuaApplication.class).run(args);
    }

    public static void main(String... args) {
        new KapuaApplication().run(args);
    }

}