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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringJoiner;

public class KapuaException extends Exception
{
    private static final long   serialVersionUID      = -2911004777156433206L;

    private static final String KAPUA_ERROR_MESSAGES  = "kapua-service-error-messages";
    private static final String KAPUA_GENERIC_MESSAGE = "Error: {0}";

    private static final Logger s_logger              = LoggerFactory.getLogger(KapuaException.class);

    protected KapuaErrorCode    code;
    protected Object[]          args;

    @SuppressWarnings("unused")
    private KapuaException()
    {
        super();
    }

    @SuppressWarnings("unused")
    private KapuaException(String message)
    {
        super(message);
    }

    @SuppressWarnings("unused")
    private KapuaException(String message, Throwable cause)
    {
        super(message, cause);
    }

    @SuppressWarnings("unused")
    private KapuaException(Throwable t)
    {
        super(t);
    }

    public KapuaException(KapuaErrorCode code)
    {
        this.code = code;
    }

    public KapuaException(KapuaErrorCode code, Object... arguments)
    {
        this.code = code;
        this.args = arguments;
    }

    public KapuaException(KapuaErrorCode code, Throwable cause, Object... arguments)
    {
        super(cause);
        this.code = code;
        this.args = arguments;
    }

    public static KapuaException internalError(Throwable cause, String message)
    {
        return new KapuaException(KapuaErrorCodes.INTERNAL_ERROR, cause, message);
    }

    public static KapuaException internalError(Throwable cause)
    {
        String arg = cause.getMessage();
        if (arg == null) {
            arg = cause.getClass().getName();
        }
        return new KapuaException(KapuaErrorCodes.INTERNAL_ERROR, cause, arg);
    }

    public static KapuaException internalError(String message)
    {
        return new KapuaException(KapuaErrorCodes.INTERNAL_ERROR, null, message);
    }

    public KapuaErrorCode getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return getLocalizedMessage(Locale.US);
    }

    public String getLocalizedMessage()
    {
        return getLocalizedMessage(Locale.getDefault());
    }

    protected String getKapuaErrorMessagesBundle()
    {
        return KAPUA_ERROR_MESSAGES;
    }

    protected String getLocalizedMessage(Locale locale)
    {
        String pattern = getMessagePattern(locale, code);
        if (pattern != null) {
            return MessageFormat.format(pattern, args);
        }
        else {
            // use the generic message by concatenating all args in one string
            StringJoiner joiner = new StringJoiner(",");
            if (args != null && args.length > 0) {
                for (Object arg : args)
                    joiner.add(arg.toString());
            }
            return MessageFormat.format(KAPUA_GENERIC_MESSAGE, joiner.toString());
        }
    }

    protected String getMessagePattern(Locale locale, KapuaErrorCode code)
    {
        //
        // Load the message pattern from the bundle
        String messagePattern = null;
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle(getKapuaErrorMessagesBundle(), locale);
            messagePattern = resourceBundle.getString(code.name());
        }
        catch (MissingResourceException mre) {
            // log the failure to load a message bundle
            s_logger.warn("Could not load Exception Messages Bundle for Locale {}", locale);
        }

        return messagePattern;
    }
}
