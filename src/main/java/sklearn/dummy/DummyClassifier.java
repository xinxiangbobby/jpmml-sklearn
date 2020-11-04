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
package sklearn.dummy;

import java.util.Collections;
import java.util.List;

import com.google.common.primitives.Doubles;
import org.dmg.pmml.DataType;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.ScoreDistribution;
import org.dmg.pmml.True;
import org.dmg.pmml.tree.ClassifierNode;
import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.jpmml.converter.CategoricalLabel;
import org.jpmml.converter.ModelUtil;
import org.jpmml.converter.Schema;
import org.jpmml.converter.ValueUtil;
import org.jpmml.python.ClassDictUtil;
import sklearn.Classifier;
import sklearn.HasPriorProbability;

public class DummyClassifier extends Classifier implements HasPriorProbability {

	public DummyClassifier(String module, String name){
		super(module, name);
	}

	@Override
	public Number getPriorProbability(int index){
		List<?> classes = getClasses();
		List<? extends Number> classPrior = getClassPrior();
		String strategy = getStrategy();

		ClassDictUtil.checkSize(classes, classPrior);

		switch(strategy){
			case "prior":
				{
					return classPrior.get(index);
				}
			default:
				throw new IllegalArgumentException(strategy);
		}
	}

	@Override
	public TreeModel encodeModel(Schema schema){
		List<?> classes = getClasses();
		List<? extends Number> classPrior = getClassPrior();
		Object constant = getConstant();
		String strategy = getStrategy();

		ClassDictUtil.checkSize(classes, classPrior);

		CategoricalLabel categoricalLabel = (CategoricalLabel)schema.getLabel();

		int index;

		double[] probabilities;

		switch(strategy){
			case "constant":
				{
					index = classes.indexOf(constant);

					probabilities = new double[classes.size()];
					probabilities[index] = 1d;
				}
				break;
			case "most_frequent":
				{
					index = classPrior.indexOf(Collections.max((List)classPrior));

					probabilities = new double[classes.size()];
					probabilities[index] = 1d;
				}
				break;
			case "prior":
				{
					index = classPrior.indexOf(Collections.max((List)classPrior));

					probabilities = Doubles.toArray(classPrior);
				}
				break;
			default:
				throw new IllegalArgumentException(strategy);
		}

		Node root = new ClassifierNode(ValueUtil.asString(classes.get(index)), True.INSTANCE);

		List<ScoreDistribution> scoreDistributions = root.getScoreDistributions();

		for(int i = 0; i < classes.size(); i++){
			ScoreDistribution scoreDistribution = new ScoreDistribution(ValueUtil.asString(classes.get(i)), probabilities[i]);

			scoreDistributions.add(scoreDistribution);
		}

		TreeModel treeModel = new TreeModel(MiningFunction.CLASSIFICATION, ModelUtil.createMiningSchema(categoricalLabel), root)
			.setOutput(ModelUtil.createProbabilityOutput(DataType.DOUBLE, categoricalLabel));

		return treeModel;
	}

	public List<? extends Number> getClassPrior(){
		return getNumberArray("class_prior_");
	}

	public Object getConstant(){
		return getOptionalScalar("constant");
	}

	public String getStrategy(){
		return getString("strategy");
	}
}