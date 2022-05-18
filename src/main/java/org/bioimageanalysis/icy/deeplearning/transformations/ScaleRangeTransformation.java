package org.bioimageanalysis.icy.deeplearning.transformations;


import java.util.stream.IntStream;

import org.bioimageanalysis.icy.deeplearning.tensor.Tensor;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class ScaleRangeTransformation extends DefaultImageTransformation {
	public static final String name = "scale_range";
	/**
	 * Compulsory parameter. Maximum percentile for the image
	 */
	private Number maxPercentile;
	/**
	 * Compulsory parameter. Minimum percentile
	 */
	private Number minPercentile;
	/**
	 * Compulsory, although it can be replaced by the INDArray input
	 */
	private Tensor inputTensor;
	/**
	 * Optional. Axes along which the transformation is calculated
	 */
	private String axes;
	/**
	 * Compulsory. Whether the method is applied per sample or per dataset
	 */
	private String mode;
	/** TODO find out which is this for
	 * Optional. REference tensor
	 */
	private String referenceTensor;

	/**
	 * Constructor for the transformation using a Tensor for the processing
	 * @param input
	 * 	input in the form of Tensor
	 */
	public ScaleRangeTransformation(Tensor input) {
		inputTensor = input;
	}
	
	/**
	 * Set the maximum percentile
	 * @param maxPercentile
	 * 	maximum percentile
	 */
	public void setMaxPercentile(Number maxPercentile) {
		this.maxPercentile = maxPercentile;
	}

	public void setMinPercentile(Number minPercentile) {
		this.minPercentile = minPercentile;
	}
	
	public void setAxes(String axes){
		this.axes = axes;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public void setReferenceTensor(String referenceTensor) {
		this.referenceTensor = referenceTensor;
	}

	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 */
	public Tensor apply() {
		if (minPercentile == null || maxPercentile == null) {
			throw new IllegalArgumentException("Error defining the processing '"
					+ name + "'. It should at least be provided with the "
					+ "arguments 'min_percentile' and 'max_percetile' in the"
					+ " yaml file.");
		}
		String axesOrder = inputTensor.getAxesOrderString();
		int[] percentileAxes = IntStream.range(0, axesOrder.length()).toArray();
		if (axes != null) {
			percentileAxes = IntStream.range(0, axesOrder.length()).toArray();
		}
		INDArray array = inputTensor.getDataAsNDArray();
		INDArray minPercVals = array.percentile(minPercentile, percentileAxes);
		INDArray maxPercVals = array.percentile(maxPercentile, percentileAxes);
		array = array.sub(minPercVals).div(array.sub(maxPercVals));
		inputTensor.convertToDataType(DataType.FLOAT);
		return inputTensor;
	}
	
	public static void main(String[] args) {
		double c = 1.0 / (1024.0 * 1024.0);
		double aa = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) *c;
		INDArray arr = Nd4j.rand(new int[] {1,3,128,128});
		double bb = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) *c;
		double dd = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) *c;
		Tensor tt = Tensor.build("example", "bcyx", arr);
		ScaleRangeTransformation preproc = new ScaleRangeTransformation(tt);
		preproc.setMinPercentile(10);
		preproc.setMaxPercentile(90);
		preproc.apply();
		double cc = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) *c;
		System.out.println();
	}
}
