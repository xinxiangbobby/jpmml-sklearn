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

public class RobustScaler extends Transformer {

	public RobustScaler(String module, String name){
		super(module, name);
	}

	@Override
	public int getNumberOfFeatures(){
		Boolean withCentering = getWithCentering();
		Boolean withScaling = getWithScaling();

		int[] shape;

		if(withCentering){
			shape = getCenterShape();
		} else

		if(withScaling){
			shape = getScaleShape();
		} else

		{
			return super.getNumberOfFeatures();
		}

		return shape[0];
	}

	@Override
	public List<Feature> encodeFeatures(List<Feature> features, SkLearnEncoder encoder){
		Boolean withCentering = getWithCentering();
		Boolean withScaling = getWithScaling();

		List<? extends Number> center = (withCentering ? getCenter() : null);
		List<? extends Number> scale = (withScaling ? getScale() : null);

		if(center == null && scale == null){
			return features;
		}

		ClassDictUtil.checkSize(features, center, scale);

		List<Feature> result = new ArrayList<>();

		for(int i = 0; i < features.size(); i++){
			Feature feature = features.get(i);

			Number centerValue = (withCentering ? center.get(i) : 0d);
			Number scaleValue = (withScaling ? scale.get(i) : 1d);

			if(ValueUtil.isZero(centerValue) && ValueUtil.isOne(scaleValue)){
				result.add(feature);

				continue;
			}

			ContinuousFeature continuousFeature = feature.toContinuousFeature();

			// "($name - center) / scale"
			Expression expression = continuousFeature.ref();

			if(!ValueUtil.isZero(centerValue)){
				expression = PMMLUtil.createApply(PMMLFunctions.SUBTRACT, expression, PMMLUtil.createConstant(centerValue));
			} // End if

			if(!ValueUtil.isOne(scaleValue)){
				expression = PMMLUtil.createApply(PMMLFunctions.DIVIDE, expression, PMMLUtil.createConstant(scaleValue));
			}

			DerivedField derivedField = encoder.createDerivedField(createFieldName("RobustScaler", continuousFeature), expression);

			result.add(new ContinuousFeature(encoder, derivedField));
		}

		return result;
	}

	public Boolean getWithCentering(){
		return getBoolean("with_centering");
	}

	public Boolean getWithScaling(){
		return getBoolean("with_scaling");
	}

	public List<? extends Number> getCenter(){
		return getNumberArray("center_");
	}

	public int[] getCenterShape(){
		return getArrayShape("center_", 1);
	}

	public List<? extends Number> getScale(){
		return getNumberArray("scale_");
	}

	public int[] getScaleShape(){
		return getArrayShape("scale_", 1);
	}
}