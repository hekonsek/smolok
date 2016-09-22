/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.service.device.registry;

public interface KapuaAttributePredicate<T> extends KapuaPredicate
{
    public enum Operator
    {
        EQUAL,
        NOT_EQUAL,

        IS_NULL,
        NOT_NULL,

        STARTS_WITH,
        LIKE,

        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL;
    }

    public String getAttributeName();

    public T getAttributeValue();

    public Operator getOperator();
}
