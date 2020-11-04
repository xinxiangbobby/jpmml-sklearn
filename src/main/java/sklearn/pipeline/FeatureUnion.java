/*
 * Copyright (c) 2017 Villu Ruusmann
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
package sklearn.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.jpmml.converter.Feature;
import org.jpmml.python.TupleUtil;
import org.jpmml.sklearn.SkLearnEncoder;
import sklearn.MultiTransformer;
import sklearn.Transformer;

public class FeatureUnion extends MultiTransformer {

	public FeatureUnion(String module, String name){
		super(module, name);
	}

	@Override
	public List<Feature> encodeFeatures(List<Feature> features, SkLearnEncoder encoder){
		List<? extends Transformer> transformers = getTransformers();

		List<Feature> result = new ArrayList<>();

		for(Transformer transformer : transformers){
			List<Feature> transformerFeatures = new ArrayList<>(features);

			transformerFeatures = transformer.encode(transformerFeatures, encoder);

			result.addAll(transformerFeatures);
		}

		return result;
	}

	public List<? extends Transformer> getTransformers(){
		List<Object[]> transformerList = getTransformerList();

		return TupleUtil.extractElementList(transformerList, 1, Transformer.class);
	}

	public List<Object[]> getTransformerList(){
		return getTupleList("transformer_list");
	}
}