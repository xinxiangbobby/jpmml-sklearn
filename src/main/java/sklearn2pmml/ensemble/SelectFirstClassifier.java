/*
 * Copyright (c) 2020 Villu Ruusmann
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
package sklearn2pmml.ensemble;

import java.util.List;

import org.dmg.pmml.mining.MiningModel;
import org.jpmml.converter.Schema;
import org.jpmml.python.TupleUtil;
import sklearn.Classifier;

public class SelectFirstClassifier extends Classifier {

	public SelectFirstClassifier(String module, String name){
		super(module, name);
	}

	@Override
	public List<?> getClasses(){
		List<Object[]> steps = getSteps();

		if(steps.size() < 1){
			throw new IllegalArgumentException();
		}

		Object[] step = steps.get(0);

		Classifier classifier = TupleUtil.extractElement(step, 1, Classifier.class);

		return classifier.getClasses();
	}

	@Override
	public MiningModel encodeModel(Schema schema){
		List<Object[]> steps = getSteps();

		return SelectFirstUtil.encodeClassifier(steps, schema);
	}

	public List<Object[]> getSteps(){
		return getTupleList("steps");
	}
}