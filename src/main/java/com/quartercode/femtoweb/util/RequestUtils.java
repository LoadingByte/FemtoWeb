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
     * Returns the {@link HttpServletRequest#getRequestURI() URI} of the given {@link HttpServletRequest} without the {@link HttpServletRequest#getContextPath() context path}.
     * Nevertheless, the returned URI still always starts with {@code /}.
     *
     * @param request The request whose shortened URI should be returned.
     * @return The URI of the given request. It always starts with {@code /}.
     */
    public static String getRequestUri(HttpServletRequest request) {

        return request.getRequestURI().substring(request.getContextPath().length());
    }

    private RequestUtils() {

    }

}
