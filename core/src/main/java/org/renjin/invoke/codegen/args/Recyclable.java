/*
 * Renjin : JVM-based interpreter for the R language for the statistical analysis
 * Copyright © 2010-2019 BeDataDriven Groep B.V. and contributors
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
package org.renjin.invoke.codegen.args;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import org.renjin.invoke.codegen.WrapperRuntime;
import org.renjin.invoke.codegen.scalars.ScalarType;
import org.renjin.invoke.codegen.scalars.ScalarTypes;
import org.renjin.invoke.model.JvmMethod.Argument;


public class Recyclable extends ArgConverterStrategy {

  private ScalarType scalarType;
  
  public Recyclable(Argument formal) {
    super(formal);
    this.scalarType = ScalarTypes.get(formal.getClazz());
  }

  public static boolean accept(Argument formal) {
    return formal.isRecycle();
  }

  @Override
  public JExpression getTestExpr(JCodeModel codeModel, JVar sexp) {
    return scalarType.testExpr(codeModel, sexp, this.formal);
  }

  @Override
  public JExpression convertArgument(JCodeModel codeModel, JVar contextVar, JVar environmentRho, JExpression sexp) {
    return codeModel.ref(WrapperRuntime.class).staticInvoke("convertToVector").arg(sexp);
  }

}
