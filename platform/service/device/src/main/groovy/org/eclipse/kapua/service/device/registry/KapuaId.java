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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Base64;

public interface KapuaId extends Serializable
{
    public BigInteger getId();

    default public String getShortId()
    {
        return Base64.getEncoder().withoutPadding().encodeToString(getId().toByteArray());
    }
}
