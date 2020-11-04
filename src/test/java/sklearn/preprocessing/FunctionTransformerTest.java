/*
 * Copyright (c) 2016 Villu Ruusmann
 *
 * This file is part of JPMML-SkLearn
 *
 * JPMML-SkLearn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-SkLearn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-SkLearn.  If not, see <http://www.gnu.org/licenses/>.
 */
package sklearn.preprocessing;

import java.util.Collections;

import numpy.core.UFunc;
import numpy.core.UFuncUtil;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.jpmml.evaluator.EvaluationContext;
import org.jpmml.evaluator.ExpressionUtil;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.FieldValueUtil;
import org.jpmml.evaluator.VirtualEvaluationContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FunctionTransformerTest {

	@Test
	public void evaluateUFunc(){
		assertEquals(3d, evaluate("absolute", -3d));

		assertEquals(-2, evaluate("ceil", -2.75d));
		assertEquals(3, evaluate("ceil", 2.75d));

		assertEquals(-3, evaluate("floor", -2.75d));
		assertEquals(2, evaluate("floor", 2.75d));

		assertEquals(-3, evaluate("negative", 3));
		assertEquals(-3f, evaluate("negative", 3f));
		assertEquals(-3d, evaluate("negative", 3d));

		assertEquals(1f / 3f, (Float)evaluate("reciprocal", 3f), 1e-5);
		assertEquals(1d / 3d, (Double)evaluate("reciprocal", 3d), 1e-8);

		assertEquals(-1, evaluate("sign", -3d));
		assertEquals(0, evaluate("sign", 0d));
		assertEquals(+1, evaluate("sign", +3d));
	}

	static
	private Object evaluate(String function, Object value){
		UFunc ufunc = new UFunc("numpy.core", "_ufunc_reconstruct");
		ufunc.__init__(new String[]{"numpy", function});

		FieldName name = FieldName.create("x");

		DataType dataType;

		if(value instanceof Integer){
			dataType = DataType.INTEGER;
		} else

		if(value instanceof Float){
			dataType = DataType.FLOAT;
		} else

		{
			dataType = DataType.DOUBLE;
		}

		EvaluationContext context = new VirtualEvaluationContext();
		context.declare(name, FieldValueUtil.create(dataType, OpType.CONTINUOUS, value));

		Expression expression = UFuncUtil.encodeUFunc(ufunc, Collections.singletonList(new FieldRef(name)));

		FieldValue result = ExpressionUtil.evaluate(expression, context);

		return FieldValueUtil.getValue(result);
	}
}