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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.ArrayUtils;
import com.quartercode.femtoweb.api.resolutions.View;

/**
 * {@link Action}s can extend this class instead of directly implementing the action interface in order to remove boilerplate code and make the code more readable.
 * Therefore, this class provides the following mechanisms:
 *
 * <ul>
 * <li>You override the method {@link #doExecute()} instead of {@code execute(...)}. That method has no parameters you would need to handle.</li>
 * <li>The parameters are still available as {@code protected} fields ({@link #request}, {@link #response}, {@link #context}). They can be used everywhere in the class.</li>
 * <li>There are some utility methods available that directly interact with the {@code protected} fields and make the code more understandable.</li>
 * </ul>
 *
 * By using the utility methods, any action code is written in a new scheme that avoids direct access to the {@link #request} and {@link #response} objects.
 * First of all, parameters can be quickly retrieved with the {@link #getParam(String)} method.
 * In order to add an object to the {@link HttpServletRequest#setAttribute(String, Object) attributes} so JSPs can refer to it,
 * the {@link #push(String, Object)} method can be used.<br>
 * <br>
 * See {@link Action} for more general information on what actions are.
 */
public abstract class ActionSupport implements Action {

    /**
     * The {@link HttpServletRequest} object representing the currently processed request.
     */
    protected HttpServletRequest  request;

    /**
     * The {@link HttpServletResponse} object representing the response to the {@link #request currently processed request}.
     */
    protected HttpServletResponse response;

    /**
     * A {@link Context} object which provides some framework settings, as well as some methods which depend on those settings.
     */
    protected Context             context;

    @Override
    public final Action execute(HttpServletRequest request, HttpServletResponse response, Context context) throws Exception {

        this.request = request;
        this.response = response;
        this.context = context;

        return doExecute();
    }

    /**
     * Fulfills the current HTTP request that is defined by the three {@code protected} fields {@link #request}, {@link #response} and {@link #context}.
     * The returned {@link Action} will be executed directly after this method finished in order to continue the fulfillment of the request.
     * In the most common case, a {@link View} action is returned in order to show a JSP page to the user.
     *
     * @return The next action which should be executed directly after this method finished.
     * @throws Exception If any exception occurred while this action executed its activities.
     */
    protected abstract Action doExecute() throws Exception;

    // ----- Utilities -----

    /**
     * Retrieves the {@link #request} parameter that has the given name.
     * For example, say that the current request URI is {@code .../search?query=test}.
     * A call to this method with the parameter name {@code query} would yield the parameter value {@code test}.
     * Of course, this method also supports access to {@code POST} parameters.<br>
     * <br>
     * Internally, this method just redirects all calls to {@link HttpServletRequest#getParameter(String)}.
     * See that method for further documentation.
     *
     * @param name The name of the request parameter whose value should be returned.
     * @return The single value of the retrieved request parameter.
     */
    protected final String getParam(String name) {

        return request.getParameter(name);
    }

    /**
     * Retrieves <b>all</b> the {@link #request} parameters that have the given name.
     * For example, say that the current request URI is {@code .../search?filter=red&filter=green} (e.g. because multiple checkboxes have the same name).
     * A call to this method with the parameter name {@code filter} would yield the parameter value list {@code ["red", "green"]}.
     * Of course, this method also supports access to {@code POST} parameters.<br>
     * Note that this method returns an empty list instead of {@code null} if the retrieved parameter doesn't actually exist.
     * That change has been made for additional convenience.<br>
     * <br>
     * Internally, this method just redirects all calls to {@link HttpServletRequest#getParameterValues(String)}.
     * See that method for further documentation.
     *
     * @param name The name of the request parameter whose values should be returned.
     * @return All the values of the retrieved request parameter.
     */
    protected final List<String> getParams(String name) {

        String[] params = request.getParameterValues(name);
        // Sadly, this rather memory intensive implementation (we always need to create a new array list) is necessary since
        // Collections.emptyList() and Arrays.asList() return private classes which are not properly supported by EL
        return ArrayUtils.isEmpty(params) ? new ArrayList<String>() : new ArrayList<>(Arrays.asList(params));
    }

    /**
     * Returns whether the {@link #request} parameter with the given name exists.
     * For example, say that the current request URI is {@code .../search?query=test}.
     * A call to this method with the parameter name {@code query} would yield {@code true}.
     * However, a call with the parameter name {@code user} would return {@code false} because there's no such parameter in the request.
     * Of course, this method also supports {@code POST} parameters.<br>
     * <br>
     * Internally, this method just redirects all calls to {@link HttpServletRequest#getParameter(String)} and checks whether the result is {@code null}.
     * See that method for further documentation.
     *
     * @param name The name of the request parameter whose existence should be checked.
     * @return Whether the request parameter with the given name is part of the request.
     */
    protected final boolean isParamSet(String name) {

        return request.getParameter(name) != null;
    }

    /**
     * Pushes the given key-value-pair to the {@link #request} attributes so JSPs that follow this action can use it.
     * For example, say that we push the integer attribute {@code price} with the value {@code 50}.
     * Any JSP following this very action could then access that price attribute using a variety of different ways
     * (the easiest one being the EL expression <code>${price}</code>).<br>
     * <br>
     * Internally, this method just redirects all calls to {@link HttpServletRequest#setAttribute(String, Object)}.
     * See that method for further documentation.
     *
     * @param name The name of the attribute that should be set.
     * @param value The value the attribute should be set to.
     */
    protected final void push(String name, Object value) {

        request.setAttribute(name, value);
    }

    /**
     * {@link #push(String, Object) Pushes} the given {@link #getParam(String) single parameter value} if it {@link #isParamSet(String) is set}.
     * For example, say that we have a form which looks like this:
     *
     * <pre>
     * &lt;form&gt; &lt;!-- The form sends its data to itself when you click submit --&gt;
     *     &lt;input type="text" name="query" /&gt;
     *     &lt;!-- Submit button ... --&gt;
     * &lt;/form&gt;
     * </pre>
     *
     * If you click the submit button, the action that backs the JSP could retrieve and process the query parameter.
     * However, when the JSP is displayed again (e.g. because form validation failed), the input field depicted above would no longer contain the text input by the user.
     * In order to fix that, the action could always repush the parameter {@code query} so that the value can be reused by the input:
     *
     * <pre>
     * &lt;form&gt;
     *     &lt;input type="text" name="query" <b>value="${query}"</b> /&gt;
     *     &lt;!-- Submit button ... --&gt;
     * &lt;/form&gt;
     * </pre>
     *
     * @param name The name of the parameter whose value should be retrieved and then immediately pushed again.
     */
    protected final void repushSingle(String name) {

        String param = getParam(name);

        if (param != null) {
            push(name, param);
        }
    }

    /**
     * {@link #push(String, Object) Pushes} {@link #getParams(String) all values of the given parameter} as a list if it {@link #isParamSet(String) is set}.
     * If you wonder what this could be useful for, take a look at {@link #repushSingle(String)}.
     * The JavaDoc of that method discusses repushing a single parameter value.
     * Of course, the JSP code for processing of a multiple parameter value is slightly more complicated.
     * Still, it could be used for preserving the state of checkboxes or other multiple choice inputs.
     *
     * @param name The name of the parameter whose multiple values should be retrieved and then immediately pushed again as a list.
     */
    protected final void repushMultiple(String name) {

        List<String> params = getParams(name);

        if (!params.isEmpty()) {
            push(name, params);
        }
    }

    /**
     * Returns the object which is assigned to the given name in the {@link #request currently processed request's} {@link HttpSession session}.
     * This method might return {@code null} if no object is assigned to the name, or if no session has yet been created in the first place.
     *
     * @param name The name of the session object which should be returned.
     * @return The session object with the given name.
     */
    protected final Object getSessionAttr(String name) {

        HttpSession session = request.getSession(false);
        return session == null ? null : session.getAttribute(name);
    }

    /**
     * Assigns the given object to the given name in the {@link #request currently processed request's} {@link HttpSession session}.
     * If another object is already assigned to the same name, the old object is replaced with the given new one.
     * That also means that a session attribute can be {@link HttpSession#removeAttribute(String) removed} by just passing a {@code null} value into this method.
     *
     * @param name The name the given object should be assigned to.
     * @param value The object which should be assigned to the given name.
     */
    protected final void setSessionAttr(String name, Object value) {

        request.getSession().setAttribute(name, value);
    }

}
