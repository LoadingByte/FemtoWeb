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

package com.quartercode.femtoweb.impl;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.quartercode.femtoweb.api.Action;
import com.quartercode.femtoweb.api.ActionNotFoundException;
import com.quartercode.femtoweb.api.Context;
import com.quartercode.femtoweb.util.RequestUtils;

/**
 * The internal {@link Filter} which calls the appropriate {@link Action}s for all dynamic requests.
 */
public class FemtoWebFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FemtoWebFilter.class);

    private Context             context;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        String actionBasePackage = filterConfig.getInitParameter("actionBasePackage");
        String staticAssetPath = filterConfig.getInitParameter("staticAssetPath");
        String dynamicAssetPath = filterConfig.getInitParameter("dynamicAssetPath");
        String indexUri = filterConfig.getInitParameter("indexUri");

        context = new DefaultContext(actionBasePackage, staticAssetPath, dynamicAssetPath, indexUri);
    }

    @Override
    public void destroy() {

        context = null;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Retrieve the requested URI
        String uri = RequestUtils.getRequestUri(request);

        // If no specific action is specified (empty URI, e.g. "/"), redirect to the index URI (by default "/index")
        if (StringUtils.remove(uri, '/').isEmpty()) {
            response.sendRedirect(request.getContextPath() + context.getIndexUri());
            return;
        }

        // If the static content is requested, directly forward to that static content
        if (uri.startsWith(context.getStaticAssetPath())) {
            LOGGER.trace("Allowing direct access to static content '{}'", uri);
            chain.doFilter(request, response);
            return;
        }

        // If the request is no special case, retrieve and execute the action responsible for the requested URI
        processAction(request, response, uri);
    }

    private void processAction(HttpServletRequest request, HttpServletResponse response, String uri) throws IOException, ServletException {

        // Get the action which is responsible for the requested URI
        Class<? extends Action> actionClass;
        try {
            actionClass = context.getAction(uri);
        } catch (ActionNotFoundException e) {
            LOGGER.trace("Cannot find requested action '{}' for request to '{}'", e.getActionFQCN(), uri);
            response.sendError(404);
            return;
        }

        // Create a new instance of the responsible action
        Action action;
        try {
            action = actionClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServletException("Unable to construct new instance of requested action '" + actionClass.getName() + "'  for request to '" + uri + "')", e);
        }

        LOGGER.trace("Executing action '{}' for request to '{}'", actionClass.getName(), uri);

        // Execute the action and recursively execute any returned action (and their actions as well ...)
        Action currentAction = action;
        while (currentAction != null) {
            try {
                currentAction = currentAction.execute(request, response, context);
            } catch (Exception e) {
                throw new ServletException("Error while calling action '" + currentAction.getClass().getName() + "; first action was '" + actionClass.getName()
                        + ", request URI is '" + uri + "')", e);
            }
        }
    }

}
