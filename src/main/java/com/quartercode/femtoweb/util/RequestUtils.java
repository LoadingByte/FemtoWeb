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

package com.quartercode.femtoweb.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * A class which contains some utilities concerning {@link ServletRequest}s and {@link HttpServletRequest}s.
 */
public class RequestUtils {

    /**
     * Returns the {@link HttpServletRequest#getContextPath() context path} of the given {@link HttpServletRequest}.
     * In contrast to the regular method, this method returns {@code "/"} instead of {@code ""} if the context path is the root of the server.
     * That means that the returned path <b>always</b> starts with {@code /} (and never ends with {@code /}).
     * 
     * @param request The request whose context path should be returned.
     * @return The context path of the given request.
     *         It always starts with {@code /} and never ends with {@code /}.
     */
    public static String getContextPath(HttpServletRequest request) {

        return request.getContextPath().isEmpty() ? "/" : request.getContextPath();
    }

    /**
     * Returns the {@link HttpServletRequest#getRequestURI() URI} of the given {@link HttpServletRequest} without the {@link HttpServletRequest#getContextPath() context path}.
     * Nevertheless, the returned URI still always starts with {@code /}.
     * 
     * @param request The request whose shortened URI should be returned.
     * @return The URI of the given request. It always starts with {@code /}.
     */
    public static String getRequestUri(HttpServletRequest request) {

        return request.getRequestURI().substring(getContextPath(request).length());
    }

    private RequestUtils() {

    }

}
