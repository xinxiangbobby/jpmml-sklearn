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
package sklearn.ensemble.bagging;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Ints;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.Model;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segmentation;
import org.jpmml.converter.ModelUtil;
import org.jpmml.converter.Schema;
import org.jpmml.converter.mining.MiningModelUtil;
import sklearn.Estimator;

public class BaggingUtil {

	private BaggingUtil(){
	}

	static
	public <E extends Estimator> MiningModel encodeBagging(List<E> estimators, List<List<Integer>> estimatorsFeatures, Segmentation.MultipleModelMethod multipleModelMethod, MiningFunction miningFunction, Schema schema){
		Schema segmentSchema = schema.toAnonymousSchema();

		List<Model> models = new ArrayList<>();

		for(int i = 0; i < estimators.size(); i++){
			E estimator = estimators.get(i);
			List<Integer> estimatorFeatures = estimatorsFeatures.get(i);

			Schema estimatorSchema = segmentSchema.toSubSchema(Ints.toArray(estimatorFeatures));

			Model model = estimator.encode(estimatorSchema);

			models.add(model);
		}

		MiningModel miningModel = new MiningModel(miningFunction, ModelUtil.createMiningSchema(schema.getLabel()))
			.setSegmentation(MiningModelUtil.createSegmentation(multipleModelMethod, models));

		return miningModel;
	}
}