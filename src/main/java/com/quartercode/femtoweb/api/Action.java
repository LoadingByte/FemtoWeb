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

package com.quartercode.femtoweb.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.quartercode.femtoweb.api.resolutions.View;

/**
 * An action instance takes a certain HTTP request and executes certain activities to fulfill that request.
 * When its finished, it returns another action that will be executed next in order to continue the fulfillment of the request.
 * In the most common case, a {@link View} action is returned in order to show a JSP page to the user.
 * Internally, that action just calls some servlet API methods like any other action; there's no special handling involved.<br>
 * <br>
 * Actions are mapped to URIs using their fully qualified class name.
 * For example, an action with the FQCN {@code test.package.SomeTestAction} is mapped to the URI {@code /test/package/someTest}.
 * Of course, any package prefix (e.g. {@code com.quartercode.myapp}) can be removed. See the official documentation for more details on that.<br>
 * <br>
 * Note that a new action instance is created for each request.
 * Therefore, the action is allowed to carry state information in variables outside the {@link #execute(HttpServletRequest, HttpServletResponse, Context)} method.
 * Also note that all action implementations <b>must</b> fulfill the following requirements:
 * 
 * <ul>
 * <li>All actions must provide a default no-arg constructor.</li>
 * <li>All actions must end with {@code Action} (example: {@code SomeTestAction} is allowed, while {@code SomeTest} is disallowed).</li>
 * <li>Actions are not allowed to be inner classes.</li>
 * <li>Actions are not allowed to be just called {@code Action} (example: {@code SomeTestAction} is allowed, while {@code Action} is disallowed).</li>
 * </ul>
 */
public interface Action {

    /**
     * Fulfills the given HTTP request.
     * The returned action will be executed directly after this method finished in order to continue the fulfillment of the request.
     * In the most common case, a {@link View} action is returned in order to show a JSP page to the user.
     * 
     * @param request The {@link HttpServletRequest} object representing the processed request.
     * @param response The {@link HttpServletResponse} object representing the response to the processed request.
     * @param context A {@link Context} object which provides some framework settings and methods which depend on those settings.
     * @return The next action which should be executed directly after this method finished.
     * @throws Exception If any exception occurred while the action executed its activities.
     */
    public Action execute(HttpServletRequest request, HttpServletResponse response, Context context) throws Exception;

}
