/*
 * Copyright (c) 2015 Villu Ruusmann
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
package sklearn.ensemble.gradient_boosting;

import java.util.Arrays;
import java.util.List;

import org.jpmml.converter.SigmoidTransformation;
import sklearn.HasPriorProbability;

public class BinomialDeviance extends LossFunction {

	public BinomialDeviance(String module, String name){
		super(module, name);
	}

	@Override
	public List<? extends Number> computeInitialPredictions(HasPriorProbability init){
		Number probaPosClass = init.getPriorProbability(1);

		double result = Math.log(probaPosClass.doubleValue() / (1d - probaPosClass.doubleValue()));

		return Arrays.asList(1d - result, result);
	}

	@Override
	public SigmoidTransformation createTransformation(){
		return new SigmoidTransformation(-1d);
	}
}