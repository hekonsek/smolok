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

/**
 * KapuaEid
 */
public class KapuaEid implements KapuaId, Serializable
{
    private static final long serialVersionUID = 8998805462408705432L;

    protected BigInteger      eid;

    public KapuaEid()
    {
    }

    public KapuaEid(BigInteger id)
    {
        this();
        this.eid = id;
    }

    public static KapuaEid parseShortId(String shortId)
    {
        byte[] bytes = Base64.getDecoder().decode(shortId);
        return new KapuaEid(new BigInteger(bytes));
    }

    public BigInteger getId()
    {
        return eid;
    }

    protected void setId(BigInteger eid)
    {
        this.eid = eid;
    }

    public String toString()
    {
        return eid.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((eid == null) ? 0 : eid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KapuaEid other = (KapuaEid) obj;
        if (eid == null) {
            if (other.eid != null)
                return false;
        }
        else if (!eid.equals(other.eid))
            return false;
        return true;
    }
}
