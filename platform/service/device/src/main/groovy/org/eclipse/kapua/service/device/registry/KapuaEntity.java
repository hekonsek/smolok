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

public interface KapuaEntity
{
    public KapuaId getId();

    public String getType();

    public KapuaId getScopeId();

    public Date getCreatedOn();

    public KapuaId getCreatedBy();
}
