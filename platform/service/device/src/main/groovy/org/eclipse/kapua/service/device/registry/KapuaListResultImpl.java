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

import java.util.ArrayList;

public class KapuaListResultImpl<E extends KapuaEntity> extends ArrayList<E> implements KapuaListResult<E>
{
    private static final long serialVersionUID = 6296843946293431564L;

    private boolean           limitExceeded;

    public KapuaListResultImpl()
    {
        this.limitExceeded = false;
    }

    public boolean isLimitExceeded()
    {
        return limitExceeded;
    }

    public void setLimitExceeded(boolean limitExceeded)
    {
        this.limitExceeded = limitExceeded;
    }
}
