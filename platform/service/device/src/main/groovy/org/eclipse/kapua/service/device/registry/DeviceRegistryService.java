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

public interface DeviceRegistryService {

    public Device create(DeviceCreator deviceCreator) throws KapuaException;

    public Device update(Device device) throws KapuaException;

    public Device find(KapuaId scopeId, KapuaId entityId)
            throws KapuaException;

    public DeviceListResult query(KapuaQuery<Device> query)
            throws KapuaException;

    public long count(KapuaQuery<Device> query)
            throws KapuaException;

    public void delete(KapuaId scopeId, KapuaId deviceId)
            throws KapuaException;

    public Device findByClientId(KapuaId scopeId, String clientId)
            throws KapuaException;

}
