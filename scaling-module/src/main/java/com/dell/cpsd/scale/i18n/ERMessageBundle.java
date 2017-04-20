/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.scale.i18n;

import java.util.ListResourceBundle;

/**
 * Endpoint registration resource bundle.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class ERMessageBundle extends ListResourceBundle
{
    private static final Object[][] CONTENTS = {{"ER1005E", "ER1005E The filter for message with correlation id {0} is null"},
            {"ER1006E", "ER1006E The filter type for message with correlation id {0} is null"},
            {"ER1007E", "ER1007E The Endpoint Registration service was null while attempting to process message with correlation id {0}"},
            {"ER1008E", "ER1008E An exception has occurs while attempting to process message with correlation id {0}.  Detail: {1}"},
            {"ER1009E", "ER1009E There were no endpoints found for service type {0} while processing message with correlation id {1}"}};

    public ERMessageBundle()
    {
        super();
    }

    @Override
    protected Object[][] getContents()
    {
        return CONTENTS;
    }
}