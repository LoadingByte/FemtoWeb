/*
 * This file is part of FemtoWeb.
 * Copyright (c) 2015 QuarterCode <http://quartercode.com/>
 *
 * FemtoWeb is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * FemtoWeb is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with FemtoWeb. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.femtoweb.api.resolutions;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.quartercode.femtoweb.api.Action;
import com.quartercode.femtoweb.api.Context;
import com.quartercode.femtoweb.util.RequestUtils;

/**
 * An {@link Action} which redirects the user's browser to another URI using HTTP 302 and executes no further actions.
 * Therefore, all request attributes and parameters will be disposed.
 *
 * @see Action
 */
public class Redirect implements Action {

    private static final Logger           LOGGER = LoggerFactory.getLogger(Redirect.class);

    // Either "URL" or "action" is set
    private final String                  url;
    private final Class<? extends Action> action;

    private final String[]                parameters;

    /**
     * Creates a new redirect action which redirects the user to the given URL.
     * See {@link HttpServletResponse#sendRedirect(String)} for more details on the format the URL must have.
     * Actually, this method is only recommended for redirecting to another website (e.g. {@code http://quartercode.com}).
     * For redirecting to another action, you should use the {@link #Redirect(Class, String...)} constructor.
     *
     * @param url The URL the user should be redirected to.
     *        See {@link HttpServletResponse#sendRedirect(String)} for more details on the format.
     * @param parameters An alternating array of GET parameter names and values that should be appended to the URL.
     *        For example, the array {@code [param1, value1, param2, value2]} would result in the URL {@code ...?param1=value1&param2=value2}.
     *        If this array is empty, nothing (not even {@code ?}) is appended to the URL.
     */
    public Redirect(String url, String... parameters) {

        Validate.notBlank(url, "Cannot redirect to blank URL");
        Validate.noNullElements(parameters, "Cannot use a null parameter for redirect");
        Validate.isTrue(parameters.length % 2 == 0, "Invalid parameter format; parameter names and values must alternate in array");

        this.url = url;
        action = null;

        this.parameters = parameters;
    }

    /**
     * Creates a new redirect action which redirects the user to the given {@link Action}.
     * Internally, the user is redirected to the URI the action is mapped to.
     * See the {@link Action} javadoc for more details on that URI mapping.
     *
     * @param action The action the user should be redirected to.
     * @param parameters An alternating array of GET parameter names and values that should be appended to the URI of the action.
     *        For example, the array {@code [param1, value1, param2, value2]} would result in the URI {@code ...?param1=value1&param2=value2}.
     *        If this array is empty, nothing (not even {@code ?}) is appended to the URI.
     */
    public Redirect(Class<? extends Action> action, String... parameters) {

        Validate.notNull(action, "Cannot redirect to null action");
        Validate.noNullElements(parameters, "Cannot use a null parameter for redirect");
        Validate.isTrue(parameters.length % 2 == 0, "Invalid parameter format; parameter names and values must alternate in array");

        url = null;
        this.action = action;

        this.parameters = parameters.clone();
    }

    @Override
    public Action execute(HttpServletRequest request, HttpServletResponse response, Context context) throws IOException, ServletException {

        // Generate the actual target URL (depending on the invoked constructor, use the set URL or the URI of the set action)
        String actualUrl;
        if (url != null) {
            actualUrl = url;
        } else {
            actualUrl = RequestUtils.getContextPath(request) + context.getUri(action);
        }

        // Add GET parameters to the URL (if any)
        if (parameters.length != 0) {
            StringBuffer parameterString = new StringBuffer("?");
            for (int index = 0; index < parameters.length; index += 2) {
                if (index != 0) {
                    parameterString.append("&");
                }
                parameterString.append(parameters[index]).append("=").append(parameters[index + 1]);
            }
            actualUrl += parameterString;
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Redirecting request to '{}' to '{}'", RequestUtils.getRequestUri(request), actualUrl);
        }

        // Actually do the redirect
        response.sendRedirect(actualUrl);

        // No further actions
        return null;
    }

}
