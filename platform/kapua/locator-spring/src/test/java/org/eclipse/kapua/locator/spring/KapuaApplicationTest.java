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

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@Configuration
public class KapuaApplicationTest {

    // Fixtures

    @Bean
    String kapuaBean() {
        return "kapuaBean";
    }

    // Tests

    @Test
    public void shouldLoadBeanFromKapuaClasspath() {
        String fooBean = new KapuaApplication().run().getBean("kapuaBean", String.class);
        assertThat(fooBean).isEqualTo("kapuaBean");
    }

}