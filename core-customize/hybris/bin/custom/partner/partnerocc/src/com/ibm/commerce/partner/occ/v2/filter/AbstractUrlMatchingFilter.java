/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.occ.v2.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Abstract matching filter that helps parsing urls.
 * to be deprecated in August Release.
 */
@Deprecated(forRemoval = true)
public abstract class AbstractUrlMatchingFilter extends OncePerRequestFilter
{

    protected String getValue(final HttpServletRequest request, final String regexp)
    {
        final Matcher matcher = getMatcher(request, regexp);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return null;
    }

    protected Matcher getMatcher(final HttpServletRequest request, final String regexp)
    {
        final Pattern pattern = Pattern.compile(regexp);
        final String path = getPath(request);
        return pattern.matcher(path);
    }

    protected String getPath(final HttpServletRequest request)
    {
        return StringUtils.defaultString(request.getPathInfo());
    }
}