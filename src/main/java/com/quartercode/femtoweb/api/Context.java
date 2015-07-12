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
 * A {@link Context} object is provided to {@link Action}s and provides some framework settings and methods which depend on those settings.
 */
public interface Context {

    /**
     * Returns the path under which static resources (e.g. stylesheets or images) are located.
     * Note that the path is relative to the root directory of the webapp.
     * By default, this framework setting is {@code /static}.<br>
     * <br>
     * Note that this path always starts with {@code /} and never ands with {@code /}.
     *
     * @return The static resource path.
     */
    public String getStaticAssetPath();

    /**
     * Returns the path under which dynamic resources (e.g. JSP pages) are located.
     * Note that the path is relative to the root directory of the webapp.
     * By default, this framework setting is {@code /WEB-INF/dynamic}.<br>
     * <br>
     * Note that this path always starts with {@code /} and never ands with {@code /}.
     *
     * @return The dynamic resource path.
     */
    public String getDynamicAssetPath();

    /**
     * Returns the URI which serves as the homepage/welcome page of the web application.
     * When a user makes a request to the root of the webapp (the domain followed by the context path), he is redirected to this URI.
     * By default, this framework setting is {@code /index}.<br>
     * <br>
     * Note that this URI always starts with {@code /} and never ands with {@code /}.
     *
     * @return The URI to the homepage.
     */
    public String getIndexUri();

    /**
     * Returns the URI the given {@link Action} class is mapped to.
     * That is possible because each action is automatically mapped to a certain URI (e.g {@code test.package.SomeTestAction -> /test/package/someTest}).
     * See the {@link Action} javadoc for more details on the algorithm behind the mapping.<br>
     * <br>
     * Note that the returned URI always starts with {@code /} and never ands with {@code /}.
     *
     * @param action The action class whose URI should be returned.
     * @return The URI the given action class is mapped to.
     */
    public String getUri(Class<? extends Action> action);

    /**
     * Returns the {@link Action} class which is mapped to the given URI.
     * That is possible because each action is automatically mapped to a certain URI (e.g {@code test.package.SomeTestAction -> /test/package/someTest}).
     * See the {@link Action} javadoc for more details on the algorithm behind the mapping.
     *
     * @param uri The URI whose mapped action class should be returned.
     * @return The action class which is mapped to the given URI.
     * @throws ActionNotFoundException If no action class is mapped to the given URI.
     */
    public Class<? extends Action> getAction(String uri) throws ActionNotFoundException;

}
