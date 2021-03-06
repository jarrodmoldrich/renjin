#
# Renjin : JVM-based interpreter for the R language for the statistical analysis
# Copyright © 2010-2019 BeDataDriven Groep B.V. and contributors
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, a copy is available at
# https://www.gnu.org/licenses/gpl-2.0.txt
#

library(hamcrest)

test.gsub0 <- function() {

    assertThat(gsub( '^0+', '', '123'), identicalTo('123'))
    assertThat(gsub( '^0*', '', '123'), identicalTo('123'))
    assertThat(gsub( '0*', '', '12303405678'), identicalTo('123345678'))
}

test.gsub.backref <- function() {
    assertThat(gsub(x = "az", pattern = "a([a-z])", replacement = "b\\1"), identicalTo("bz"))
}


test.gsub1 <- function() {
    x <- c("[boo]")
    chars <- c("*", ".", "?", "^", "+", "$", "|", "(", ")", "[", "]", "{", "}", "\\")
    y <- gsub(paste0("([\\", paste0(collapse = "\\", chars), "])"), "\\\\\\1", x, perl = TRUE)

    assertThat(y, identicalTo("\\[boo\\]"))
}