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
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.quartercode.femtoweb.api.Action;
import com.quartercode.femtoweb.api.Context;
import com.quartercode.femtoweb.util.RequestUtils;

/**
 * An {@link Action} which sends a set {@link Context#getDynamicAssetPath() dynamic resource} to the user and executes no further actions.
 * Internally, this action forwards the request to that resource (e.g. a JSP page).
 * Therefore, all {@link ServletRequest#setAttribute(String, Object) request attributes}, {@link HttpSession#setAttribute(String, Object) session attributes} etc.
 * are also forwarded to the resource.
 * If the forwarding target is a JSP page, that page can then access those attributes (e.g. through the expression language).
 *
 * @see Action
 */
public class View implements Action {

    private static final Logger           LOGGER = LoggerFactory.getLogger(View.class);

    // Either "path" or "dir"&"action" is set
    private final String                  path;

    private final Class<? extends Action> dir;
    private final String                  name;

    /**
     * Creates a new view action which displays the dynamic resource located under the given path.
     * Note that the path is relative to the {@link Context#getDynamicAssetPath() dynamic asset path}.
     *
     * @param path The path to the resource which should be displayed to the user.
     */
    public View(String path) {

        Validate.notBlank(path, "Cannot forward to blank path");

        this.path = path;

        dir = null;
        name = null;
    }

    /**
     * Creates a new view action which displays the dynamic resource located under the path provided by the given components.
     * This method takes the {@link Context#getUri(Class) URI of the given action class} and replaces its last entry (the class name of the action) with the given name.
     * That means that the final path uses the URI "directory" of the given action and the given resource file name.
     * For example, calling this method with the {@link Action} class {@code test.package.SomeTestAction} and the file name {@code testForm.jsp} would result in the
     * dynamic resource path {@code /test/package/testForm.jsp}.<br>
     * <br>
     * Note that the final path is relative to the {@link Context#getDynamicAssetPath() dynamic asset path}.
     *
     * @param dir The action class whose URI "directory" should be used as the directory of the final file path.
     * @param name The name of the viewed file in the directory defined by the given action class.
     */
    public View(Class<? extends Action> dir, String name) {

        Validate.notNull(dir, "Cannot use null action as \"directory\" for the forwarding path");
        Validate.notBlank(name, "Cannot forward to a file with blank name");

        path = null;

        this.dir = dir;
        this.name = StringUtils.stripStart(name, "/");
    }

    @Override
    public Action execute(HttpServletRequest request, HttpServletResponse response, Context context) throws IOException, ServletException {

        // Generate the actual target path (depending on the invoked constructor, use the set path or combine the "directory" URI of the set action's URI with the set name)
        String actualPath;
        if (path != null) {
            actualPath = path;
        } else {
            String dirUri = StringUtils.substringBeforeLast(context.getUri(dir), "/");
            actualPath = dirUri + "/" + name;
        }

        // Add the path prefix to the directory containing dynamic content
        actualPath = context.getDynamicAssetPath() + "/" + StringUtils.stripStart(actualPath, "/");

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Forwarding request to '{}' to '{}'", RequestUtils.getRequestUri(request), actualPath);
        }

        // Actually forward the request
        request.getRequestDispatcher(actualPath).forward(request, response);

        return null;
    }

}
