#
# Renjin : JVM-based interpreter for the R language for the statistical analysis
# Copyright © 2010-2018 BeDataDriven Groep B.V. and contributors
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

# Generated by gen-unary-tests.R using GNU R version 3.5.3 (2019-03-11)
library(hamcrest)
Sys.setlocale('LC_COLLATE', 'C')
as.name.foo <- function(...) 41L
as.vector.foo <- function(...) 99
as.vector.bar <- function(...) 98
Math.bar <- function(...) 44
Summary.bar <- function(...) 45
Ops.bar <- function(...) 46
test.as.name.1 <- function() assertThat(as.name(NULL), throwsError())
test.as.name.2 <- function() assertThat(as.name(logical(0)), throwsError())
test.as.name.3 <- function() assertThat(as.name(c(TRUE, TRUE, FALSE, FALSE, TRUE)), identicalTo(as.name("TRUE")))
test.as.name.4 <- function() assertThat(as.name(structure(c(TRUE, FALSE), .Names = c("a", ""))), identicalTo(as.name("TRUE")))
test.as.name.5 <- function() assertThat(as.name(c(TRUE, FALSE, NA)), identicalTo(as.name("TRUE")))
test.as.name.6 <- function() assertThat(as.name(integer(0)), throwsError())
test.as.name.7 <- function() assertThat(as.name(structure(integer(0), .Names = character(0))), throwsError())
test.as.name.8 <- function() assertThat(as.name(1:3), identicalTo(as.name("1")))
test.as.name.9 <- function() assertThat(as.name(c(1L, NA, 4L, NA, 999L)), identicalTo(as.name("1")))
test.as.name.10 <- function() assertThat(as.name(c(1L, 2L, 1073741824L, 1073741824L)), identicalTo(as.name("1")))
test.as.name.11 <- function() assertThat(as.name(numeric(0)), throwsError())
test.as.name.12 <- function() assertThat(as.name(c(3.14159, 6.28319, 9.42478, 12.5664, 15.708)), identicalTo(as.name("3.14159")))
test.as.name.13 <- function() assertThat(as.name(c(-3.14159, -6.28319, -9.42478, -12.5664, -15.708)), identicalTo(as.name("-3.14159")))
test.as.name.14 <- function() assertThat(as.name(structure(1:2, .Names = c("a", "b"))), identicalTo(as.name("1")))
test.as.name.15 <- function() assertThat(as.name(structure(c(1.5, 2.5), .Names = c("a", "b"))), identicalTo(as.name("1.5")))
test.as.name.16 <- function() assertThat(as.name(c(1.5, 1.51, 0, 1.49, -30)), identicalTo(as.name("1.5")))
test.as.name.17 <- function() assertThat(as.name(c(1.5, 1.51, 0, 1.49, -30, NA)), identicalTo(as.name("1.5")))
test.as.name.18 <- function() assertThat(as.name(c(1.5, 1.51, 0, 1.49, -30, NaN)), identicalTo(as.name("1.5")))
test.as.name.19 <- function() assertThat(as.name(c(1.5, 1.51, 0, 1.49, -30, Inf)), identicalTo(as.name("1.5")))
test.as.name.20 <- function() assertThat(as.name(c(1.5, 1.51, 0, 1.49, -30, -Inf)), identicalTo(as.name("1.5")))
test.as.name.21 <- function() assertThat(as.name(character(0)), throwsError())
test.as.name.22 <- function() assertThat(as.name(c("4.1", "blahh", "99.9", "-413", NA)), identicalTo(as.name("4.1")))
test.as.name.23 <- function() assertThat(as.name(complex(0)), throwsError())
test.as.name.24 <- function() assertThat(as.name(list(1, 2, 3)), throwsError())
test.as.name.25 <- function() assertThat(as.name(list(1, 2, NULL)), throwsError())
test.as.name.26 <- function() assertThat(as.name(list(1L, 2L, 3L)), throwsError())
test.as.name.27 <- function() assertThat(as.name(list(1L, 2L, NULL)), throwsError())
test.as.name.28 <- function() assertThat(as.name(list(1, 2, list(3, 4))), throwsError())
test.as.name.29 <- function() assertThat(as.name(list(1, 2, numeric(0))), throwsError())
test.as.name.30 <- function() assertThat(as.name(list(3, "a", structure(list("b", list(TRUE, "c")), .Names = c("", "z")))), throwsError())
test.as.name.31 <- function() assertThat(as.name(structure(list(1, 2, 3), .Names = c(NA, "", "b"))), throwsError())
test.as.name.32 <- function() assertThat(as.name(pairlist(41, "a", 21L)), throwsError())
test.as.name.33 <- function() assertThat(as.name(structure(pairlist(a = 41, 42))), throwsError())
test.as.name.34 <- function() assertThat(as.name(structure(pairlist(a = 41, NULL))), throwsError())
test.as.name.35 <- function() assertThat(as.name(structure(1:12, .Dim = 3:4)), identicalTo(as.name("1")))
test.as.name.36 <- function() assertThat(as.name(structure(1:12, .Dim = 3:4, .Dimnames = structure(list(    c("a", "b", "c"), c("d", "e", "f", "g")), .Names = c("x", "y")))), identicalTo(as.name("1")))
test.as.name.37 <- function() assertThat(as.name(structure(1:3, rando.attrib = 941L)), identicalTo(as.name("1")))
test.as.name.38 <- function() assertThat(as.name(structure(1:3, .Dim = 3L, .Dimnames = list(c("a", "b", "c")))), identicalTo(as.name("1")))
test.as.name.39 <- function() assertThat(as.name(structure(1:3, .Dim = 3L, .Dimnames = structure(list(    c("a", "b", "c")), .Names = "z"))), identicalTo(as.name("1")))
test.as.name.40 <- function() assertThat(as.name(structure(list("foo"), class = "foo")), identicalTo(99))
test.as.name.41 <- function() assertThat(as.name(structure(list("bar"), class = "foo")), identicalTo(99))
test.as.name.42 <- function() assertThat(as.name(quote(xyz)), identicalTo(as.name("xyz")))
test.as.name.43 <- function() assertThat(as.name(quote(sin(3.14))), throwsError())
test.as.name.44 <- function() assertThat(as.name("NaN"), identicalTo(as.name("NaN")))
test.as.name.45 <- function() assertThat(as.name("NABOOM!"), identicalTo(as.name("NABOOM!")))
test.as.name.46 <- function() assertThat(as.name("NaNaNabooboo"), identicalTo(as.name("NaNaNabooboo")))
test.as.name.47 <- function() assertThat(as.name("-Inf"), identicalTo(as.name("-Inf")))
test.as.name.48 <- function() assertThat(as.name("+Infinity"), identicalTo(as.name("+Infinity")))
test.as.name.49 <- function() assertThat(as.name("Infinity and beyond!"), identicalTo(as.name("Infinity and beyond!")))
test.as.name.50 <- function() assertThat(as.name("Infi"), identicalTo(as.name("Infi")))
test.as.name.51 <- function() assertThat(as.name("0.03f"), identicalTo(as.name("0.03f")))
test.as.name.52 <- function() assertThat(as.name("  0.0330   "), identicalTo(as.name("  0.0330   ")))
test.as.name.53 <- function() assertThat(as.name(structure("foo", class = "foo")), identicalTo(99))
test.as.name.54 <- function() assertThat(as.name(structure(list(1L, "bar"), class = "bar")), identicalTo(98))
