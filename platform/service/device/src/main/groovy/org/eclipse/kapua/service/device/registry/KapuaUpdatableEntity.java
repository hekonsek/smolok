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

import java.util.Date;
import java.util.Properties;

public interface KapuaUpdatableEntity extends KapuaEntity
{
    public Date getModifiedOn();

    public KapuaId getModifiedBy();

    public int getOptlock();

    public void setOptlock(int optlock);

    public Properties getEntityAttributes()
        throws KapuaException;

    public void setEntityAttributes(Properties props)
        throws KapuaException;

    public Properties getEntityProperties()
        throws KapuaException;

    public void setEntityProperties(Properties props)
        throws KapuaException;
}
