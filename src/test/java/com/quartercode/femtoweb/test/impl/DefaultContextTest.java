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

package com.quartercode.femtoweb.test.impl;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.femtoweb.api.ActionNotFoundException;
import com.quartercode.femtoweb.impl.DefaultContext;
import com.quartercode.femtoweb.test.testactions.SomeRootTestAction;
import com.quartercode.femtoweb.test.testactions.sub1.sub2.SomeTestAction;

public class DefaultContextTest {

    private DefaultContext context;

    @Before
    public void setUp() throws Exception {

        context = new DefaultContext("com.quartercode.femtoweb.test.testactions", null, null, null);
    }

    @Test
    public void testGetUri() {

        // Test twice for testing the cache
        assertEquals("Returned URI for test action", "/sub1/sub2/someTest", context.getUri(SomeTestAction.class));
        assertEquals("Returned URI for test action", "/sub1/sub2/someTest", context.getUri(SomeTestAction.class));

        assertEquals("Returned URI for test action", "/someRootTest", context.getUri(SomeRootTestAction.class));
    }

    @Test
    public void testGetAction() throws ActionNotFoundException {

        // Test twice for testing the cache
        assertEquals("Returned action for test URI", SomeTestAction.class, context.getAction("/sub1/sub2/someTest"));
        assertEquals("Returned action for test URI", SomeTestAction.class, context.getAction("/sub1/sub2/someTest"));

        assertEquals("Returned action for test URI", SomeRootTestAction.class, context.getAction("/someRootTest"));
    }

}
