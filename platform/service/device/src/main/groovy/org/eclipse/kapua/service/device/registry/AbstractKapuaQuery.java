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

public abstract class AbstractKapuaQuery<E extends KapuaEntity> implements KapuaQuery<E>
{
    private KapuaId      scopeId;

    private KapuaPredicate    predicate;
    private KapuaSortCriteria sortCriteria;
    private KapuaFetchStyle   fetchStyle;

    private Integer      offset;
    private Integer      limit;

    public AbstractKapuaQuery()
    {
    }

    public void setScopeId(KapuaId scopeId)
    {
        this.scopeId = scopeId;
    }

    public KapuaId getScopeId()
    {
        return scopeId;
    }

    public void setPredicate(KapuaPredicate queryPredicate)
    {
        this.predicate = queryPredicate;
    }

    public KapuaPredicate getPredicate()
    {
        return this.predicate;
    }

    // sort
    public void setSortCriteria(KapuaSortCriteria sortCriteria)
    {
        this.sortCriteria = sortCriteria;
    }

    public KapuaSortCriteria getSortCriteria()
    {
        return this.sortCriteria;
    }

    public KapuaFetchStyle getFetchStyle()
    {
        return fetchStyle;
    }

    public void setFetchStyle(KapuaFetchStyle fetchStyle)
    {
        this.fetchStyle = fetchStyle;
    }

    public Integer getOffset()
    {
        return offset;
    }

    public Integer getLimit()
    {
        return limit;
    }

    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }

    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
}
