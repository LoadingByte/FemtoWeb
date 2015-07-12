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

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import com.quartercode.femtoweb.api.Action;
import com.quartercode.femtoweb.api.ActionNotFoundException;
import com.quartercode.femtoweb.api.Context;

/**
 * The default implementation of the {@link Context} interface.
 * This class is used by the {@link FemtoWebFilter}.
 */
public class DefaultContext implements Context {

    private static final String                        DEFAULT_STATIC_ASSET_PATH  = "/static";
    private static final String                        DEFAULT_DYNAMIC_ASSET_PATH = "/WEB-INF/dynamic";
    private static final String                        DEFAULT_INDEX_URI          = "/index";

    private final String                               actionBasePackage;
    private final String                               staticAssetPath;
    private final String                               dynamicAssetPath;
    private final String                               indexUri;

    private final Map<Class<? extends Action>, String> actionsToUris              = new HashMap<>();
    private final Map<String, Class<? extends Action>> urisToActions              = new HashMap<>();

    /**
     * Creates a new default context implementation with the given framework settings.
     *
     * @param actionBasePackage The package which contains all {@link Action}s and action subpackages.
     *        Note that this also functions as a package prefix which is removed from all action packages before they are mapped to URIs.
     *        Thereby, package names like {@code com.quartercode.femtowebtest.actions} are not included in URIs.
     * @param staticAssetPath The {@link #getStaticAssetPath() static asset path}.
     *        If this is blank, a default value is used.
     * @param dynamicAssetPath The {@link #getDynamicAssetPath() dynamic asset path}.
     *        If this is blank, a default value is used.
     * @param indexUri The {@link #getIndexUri() index URI}.
     *        If this is blank, a default value is used.
     */
    public DefaultContext(String actionBasePackage, String staticAssetPath, String dynamicAssetPath, String indexUri) {

        Validate.notBlank(actionBasePackage, "Action base package cannot be blank");
        Validate.isTrue(!isClassExistent(actionBasePackage), "Provided action base package name ('%s') is a class and not a package", actionBasePackage);

        this.actionBasePackage = actionBasePackage;
        this.staticAssetPath = !StringUtils.isBlank(staticAssetPath) ? preparePath(staticAssetPath) : DEFAULT_STATIC_ASSET_PATH;
        this.dynamicAssetPath = !StringUtils.isBlank(dynamicAssetPath) ? preparePath(dynamicAssetPath) : DEFAULT_DYNAMIC_ASSET_PATH;
        this.indexUri = !StringUtils.isBlank(indexUri) ? preparePath(indexUri) : DEFAULT_INDEX_URI;
    }

    private boolean isClassExistent(String className) {

        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private String preparePath(String path) {

        return "/" + StringUtils.strip(path, "/");
    }

    @Override
    public String getStaticAssetPath() {

        return staticAssetPath;
    }

    @Override
    public String getDynamicAssetPath() {

        return dynamicAssetPath;
    }

    @Override
    public String getIndexUri() {

        return indexUri;
    }

    @Override
    public String getUri(Class<? extends Action> action) {

        if (!actionsToUris.containsKey(action)) {
            insertActionUriPair(action, ActionUriResolver.getUri(actionBasePackage, action));
        }

        return actionsToUris.get(action);
    }

    @Override
    public Class<? extends Action> getAction(String uri) throws ActionNotFoundException {

        // By doing this, you are no longer able to flood the cache by requesting the same action with different amounts of repeated "/" at the end
        String effectiveUri = "/" + StringUtils.strip(uri, "/");

        if (!urisToActions.containsKey(effectiveUri)) {
            insertActionUriPair(ActionUriResolver.getAction(actionBasePackage, effectiveUri), effectiveUri);
        }

        return urisToActions.get(effectiveUri);
    }

    private void insertActionUriPair(Class<? extends Action> action, String uri) {

        actionsToUris.put(action, uri);
        urisToActions.put(uri, action);
    }

}
