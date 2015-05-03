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

/**
 * This exception is thrown by {@link Context#getAction(String)} if no {@link Action} is mapped to a certain URI.
 */
public class ActionNotFoundException extends Exception {

    private static final long serialVersionUID = 3067738731599265496L;

    private final String      uri;
    private final String      actionFQCN;

    /**
     * Creates a new action not found exception.
     * 
     * @param cause The exception which caused the error in the first place.
     * @param uri The URI no action is mapped to.
     * @param actionFQCN The fully qualified class name the algorithm expected the {@link Action} class for the given URI to be located under.
     */
    public ActionNotFoundException(Throwable cause, String uri, String actionFQCN) {

        super("Cannot find action '" + actionFQCN + "' for request to '" + uri + "'", cause);

        this.uri = uri;
        this.actionFQCN = actionFQCN;
    }

    /**
     * Returns the URI no action is mapped to.
     * 
     * @return The unmapped URI.
     */
    public String getUri() {

        return uri;
    }

    /**
     * Returns the fully qualified class name the algorithm expected the {@link Action} class for the set {@link #getUri() URI} to be located under.
     * For example, this might be {@code test.package.SomeTestAction} if the URI is {@code /test/package/someTest}
     * 
     * @return The expected (but not existent) action class for the set URI.
     */
    public String getActionFQCN() {

        return actionFQCN;
    }

}
