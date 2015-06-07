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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import com.quartercode.femtoweb.api.Action;
import com.quartercode.femtoweb.api.ActionNotFoundException;
import com.quartercode.femtoweb.api.Context;

/**
 * An internal class used by {@link DefaultContext} for enforcing the mapping of {@link Action}s to URIs.
 */
class ActionUriResolver {

    /**
     * Returns the URI of the given {@link Action} which must be located in the given package.
     * See {@link Context#getUri(Class)} for more information.
     *
     * @param actionBasePackage The package name which will be removed from the start of the action class name.
     *        Thereby, package names like {@code com.quartercode.femtowebtest.actions} are not included in URIs.
     * @param action The action class whose URI should be returned.
     * @return The URI the given action class is mapped to.
     */
    public static String getUri(String actionBasePackage, Class<? extends Action> action) {

        String actionFQCN = action.getName();

        // Verify that the action class is
        // - not an inner class
        // - not called "Action"
        // - ends with action
        Validate.isTrue(!StringUtils.contains(actionFQCN, '$'), "Action classes are not allowed to be inner classes; '%s' is therefore invalid", actionFQCN);
        Validate.isTrue(!actionFQCN.endsWith(".Action"), "Actions classes which are just called 'Action' are disallowed");
        Validate.isTrue(actionFQCN.endsWith("Action"), "Actions classes must end with 'Action'; '%s' is therefore invalid", actionFQCN);

        // Verify that the action class is inside the base package
        Validate.isTrue(actionFQCN.startsWith(actionBasePackage), "Cannot retrieve URI of action class '%s' because it doesn't start with the set action base package '%s'",
                actionFQCN, actionBasePackage);

        // Retrieve the name of the action class without the base package
        String actionName = actionFQCN.substring(actionBasePackage.length() + 1);

        // Replace all "." with "/", add an "/" to the front, uncapitalize the last URI part, remove "Action" from the last URI part
        // Example: "path.to.SomeTestAction" -> "/path/to/someTest")
        String[] actionNameComponents = splitAtLastSeparator(actionName, ".");
        String uriDir = actionNameComponents[0].replace('.', '/');
        String uriName = StringUtils.uncapitalize(StringUtils.removeEnd(actionNameComponents[1], "Action"));

        return "/" + joinNonBlankItems("/", uriDir, uriName);
    }

    /**
     * Returns the {@link Action} which is mapped to the given URI and located in the given package or a subpackage.
     * See {@link Context#getAction(String)} for more information.
     *
     * @param actionBasePackage The name of the package the action class must be located in somehow (subpackages are allowed).
     *        For example, the URI {@code /some/test} and the package {@code com.quartercode.femtowebtest.actions} would result in the
     *        class name {@code com.quartercode.femtowebtest.actions.some.TestAction}.
     * @param uri The URI whose mapped action class should be returned.
     * @return The action class which is mapped to the given URI.
     * @throws ActionNotFoundException If no action class is mapped to the given URI.
     */
    @SuppressWarnings ("unchecked")
    public static Class<? extends Action> getAction(String actionBasePackage, String uri) throws ActionNotFoundException {

        // Remove any "/" from the start and the end of the URI
        String trimmedUri = StringUtils.strip(uri, "/");

        // Replace all "/" with ".", add the action base package to the front, capitalize the last URI part, append "Action" to the last URI part
        // Example: "/path/to/someTest" -> "path.to.SomeTest")
        String[] uriComponents = splitAtLastSeparator(trimmedUri, "/");
        String actionFQCNPackage = uriComponents[0].replace('/', '.');
        String actionFQCNName = StringUtils.capitalize(uriComponents[1]) + "Action";
        String actionFQCN = joinNonBlankItems(".", actionBasePackage, actionFQCNPackage, actionFQCNName);

        try {
            return (Class<? extends Action>) Class.forName(actionFQCN);
        } catch (ClassNotFoundException e) {
            throw new ActionNotFoundException(e, uri, actionFQCN);
        }
    }

    private static String[] splitAtLastSeparator(String string, String separator) {

        if (!string.contains(separator)) {
            return new String[] { "", string };
        } else {
            return new String[] { StringUtils.substringBeforeLast(string, separator), StringUtils.substringAfterLast(string, separator) };
        }
    }

    private static String joinNonBlankItems(String separator, String... items) {

        StringBuilder string = new StringBuilder();
        for (String item : items) {
            if (!StringUtils.isBlank(item)) {
                string.append(separator).append(item);
            }
        }

        return string.length() == 0 ? "" : string.substring(separator.length());
    }

    private ActionUriResolver() {

    }

}
