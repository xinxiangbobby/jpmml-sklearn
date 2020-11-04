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
package sklearn.preprocessing;

import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Expression;
import org.dmg.pmml.PMMLFunctions;
import org.jpmml.converter.ContinuousFeature;
import org.jpmml.converter.Feature;
import org.jpmml.converter.PMMLUtil;
import org.jpmml.converter.ValueUtil;
import org.jpmml.python.ClassDictUtil;
import org.jpmml.sklearn.SkLearnEncoder;
import sklearn.Transformer;

public class MinMaxScaler extends Transformer {

	public MinMaxScaler(String module, String name){
		super(module, name);
	}

	@Override
	public int getNumberOfFeatures(){
		int[] shape = getScaleShape();

		return shape[0];
	}

	@Override
	public List<Feature> encodeFeatures(List<Feature> features, SkLearnEncoder encoder){
		List<? extends Number> min = getMin();
		List<? extends Number> scale = getScale();

		ClassDictUtil.checkSize(features, min, scale);

		List<Feature> result = new ArrayList<>();

		for(int i = 0; i < features.size(); i++){
			Feature feature = features.get(i);

			Number minValue = min.get(i);
			Number scaleValue = scale.get(i);

			if(ValueUtil.isOne(scaleValue) && ValueUtil.isZero(minValue)){
				result.add(feature);

				continue;
			}

			ContinuousFeature continuousFeature = feature.toContinuousFeature();

			// "($name * scale) + min"
			Expression expression = continuousFeature.ref();

			if(!ValueUtil.isOne(scaleValue)){
				expression = PMMLUtil.createApply(PMMLFunctions.MULTIPLY, expression, PMMLUtil.createConstant(scaleValue));
			} // End if

			if(!ValueUtil.isZero(minValue)){
				expression = PMMLUtil.createApply(PMMLFunctions.ADD, expression, PMMLUtil.createConstant(minValue));
			}

			DerivedField derivedField = encoder.createDerivedField(createFieldName("minMaxScaler", continuousFeature), expression);

			result.add(new ContinuousFeature(encoder, derivedField));
		}

		return result;
	}

	public List<? extends Number> getMin(){
		return getNumberArray("min_");
	}

	public List<? extends Number> getScale(){
		return getNumberArray("scale_");
	}

	public int[] getScaleShape(){
		return getArrayShape("scale_", 1);
	}
}