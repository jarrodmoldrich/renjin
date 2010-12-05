/*
 * R : A Computer Language for Statistical Data Analysis
 * Copyright (C) 1995, 1996  Robert Gentleman and Ross Ihaka
 * Copyright (C) 1997--2008  The R Development Core Team
 * Copyright (C) 2003, 2004  The R Foundation
 * Copyright (C) 2010 bedatadriven
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package r.lang;

/**
 * A visitor for iterating through an expression tree
 */
public class SexpVisitor<R> {


  protected void unhandled(SEXP exp) {

  }

  public void visit(CharExp charExp) {
    unhandled(charExp);
  }

  public void visit(BuiltinFunction builtinSexp) {
    unhandled(builtinSexp);
  }

  public void visit(ComplexVector complexExp) {
    unhandled(complexExp);
  }

  public void visit(EnvExp envExp) {
    unhandled(envExp);
  }

  public void visit(ExpressionVector expSexp) {
    unhandled(expSexp);
  }

  public void visit(IntVector intExp) {
    unhandled(intExp);
  }

  public void visit(LangExp langExp) {
    unhandled(langExp);
  }

  public void visit(PairListExp listExp) {
    unhandled(listExp);
  }

  public void visit(LogicalVector logicalExp) {
    unhandled(logicalExp);
  }

  public void visit(NullExp nilExp) {
    unhandled(nilExp);
  }

  public void visit(PrimitiveFunction primitiveSexp) {
    unhandled(primitiveSexp);
  }

  public void visit(PromiseExp promExp) {
    unhandled(promExp);
  }

  public void visit(DoubleVector realExp) {
    unhandled(realExp);
  }

  public void visit(StringVector stringExp) {
    unhandled(stringExp);
  }

  public void visit(SymbolExp symbolExp) {
    unhandled(symbolExp);
  }

  public void visit(Closure closureExp) {
    unhandled(closureExp);
  }

  public R getResult() {
    throw new UnsupportedOperationException();
  }

  public void visitSpecial(SpecialFunction specialExp) {
    unhandled(specialExp);
  }

  public void visit(ListVector listExp) {
    unhandled(listExp);
  }
}
