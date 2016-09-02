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

/**
 * KapuaErrorCodes holds the enumeration of common error codes for KapuaServices.
 * For each defined enum value, a corresponding message should be defined in the properties bundle named: KapuaExceptionMessagesBundle.properties.
 */
public enum KapuaErrorCodes implements KapuaErrorCode
{
    ENTITY_NOT_FOUND,
    DUPLICATE_NAME,
    ILLEGAL_ACCESS,
    ILLEGAL_ARGUMENT,
    ILLEGAL_NULL_ARGUMENT,
    ILLEGAL_STATE,
    OPTIMISTIC_LOCKING,
    UNAUTHENTICATED,
    INTERNAL_ERROR,
    OPERATION_NOT_SUPPORTED
}
