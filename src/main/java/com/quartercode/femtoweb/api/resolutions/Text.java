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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.quartercode.femtoweb.api.Action;
import com.quartercode.femtoweb.api.Context;
import com.quartercode.femtoweb.util.RequestUtils;

/**
 * An {@link Action} which sends a set {@link String text} to the user and executes no further actions.
 * Internally, this action just prints the specified string to the HTTP response output stream.
 *
 * @see Action
 */
public class Text implements Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(Text.class);

    private final String        text;

    /**
     * Creates a new text action which displays the given {@link String}.
     *
     * @param text The string which should be sent to the user.
     */
    public Text(String text) {

        Validate.notNull(text, "Cannot respond with null text");

        this.text = text;
    }

    @Override
    public Action execute(HttpServletRequest request, HttpServletResponse response, Context context) throws IOException, ServletException {

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Responding with plain text to request to '{}'", RequestUtils.getRequestUri(request));
        }

        // Actually print out the text
        response.getOutputStream().print(text);

        return null;
    }

}
