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

import java.util.List;

import com.google.common.collect.Iterables;
import org.jpmml.python.PythonObject;
import sklearn.HasPriorProbability;

public class LogOddsEstimator extends PythonObject implements HasPriorProbability {

	public LogOddsEstimator(String module, String name){
		super(module, name);
	}

	@Override
	public Number getPriorProbability(int index){
		List<? extends Number> prior = getPrior();

		if(prior.size() <= 1 && index <= 1){
			return Iterables.getOnlyElement(prior);
		}

		return Iterables.get(prior, index);
	}

	public List<? extends Number> getPrior(){
		return getNumberArray("prior");
	}
}