/**
 * Renjin : JVM-based interpreter for the R language for the statistical analysis
 * Copyright © 2010-2016 BeDataDriven Groep B.V. and contributors
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, a copy is available at
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.renjin.parser;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class StringLiteralsTest {

    @Test
    public void unicodeEscapes() {
        assertThat(StringLiterals.format("\u0001", "NA"), equalTo("\"\\u0001\""));
        assertThat(StringLiterals.format("\u00a1", "NA"), equalTo("\"\\u00a1\""));
        assertThat(StringLiterals.format("\u01a1", "NA"), equalTo("\"\\u01a1\""));
        assertThat(StringLiterals.format("\u1fa1", "NA"), equalTo("\"\\u1fa1\""));
    }
}