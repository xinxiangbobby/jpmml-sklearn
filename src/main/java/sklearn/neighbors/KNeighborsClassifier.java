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
package sklearn.neighbors;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.dmg.pmml.DataType;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.nearest_neighbor.NearestNeighborModel;
import org.jpmml.converter.Schema;
import org.jpmml.converter.ValueUtil;
import sklearn.Classifier;

public class KNeighborsClassifier extends Classifier implements HasNeighbors, HasTrainingData {

	public KNeighborsClassifier(String module, String name){
		super(module, name);
	}

	@Override
	public int getNumberOfFeatures(){
		int[] shape = getFitXShape();

		return shape[1];
	}

	@Override
	public DataType getDataType(){
		return DataType.FLOAT;
	}

	@Override
	public NearestNeighborModel encodeModel(Schema schema){
		int[] shape = getFitXShape();

		int numberOfInstances = shape[0];
		int numberOfFeatures = shape[1];

		NearestNeighborModel nearestNeighborModel = KNeighborsUtil.encodeNeighbors(this, MiningFunction.CLASSIFICATION, numberOfInstances, numberOfFeatures, schema)
			.setCategoricalScoringMethod(NearestNeighborModel.CategoricalScoringMethod.MAJORITY_VOTE);

		return nearestNeighborModel;
	}

	@Override
	public String getMetric(){
		return getString("metric");
	}

	@Override
	public int getNumberOfNeighbors(){
		return getInteger("n_neighbors");
	}

	@Override
	public int getP(){
		return getInteger("p");
	}

	@Override
	public String getWeights(){
		return getString("weights");
	}

	@Override
	public List<? extends Number> getFitX(){
		return getNumberArray("_fit_X");
	}

	public int[] getFitXShape(){
		return getArrayShape("_fit_X", 2);
	}

	@Override
	public List<?> getY(){
		List<? extends Number> y = getNumberArray("_y");

		Function<Number, Object> function = new Function<Number, Object>(){

			private List<?> classes = getClasses();


			@Override
			public Object apply(Number number){
				int index = ValueUtil.asInt(number);

				return this.classes.get(index);
			}
		};

		return Lists.transform(y, function);
	}
}