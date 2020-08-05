
// try to use light and touch sensor to determine if it is a movable object

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

public class IsMovable {
    private NeuralNetwork light;
    private NeuralNetwork touch;
    public NeuralNetwork ann;
    private double[][] traindata;

    /**double[][] data = {
     * //sample data, first input is nShade, second is capacitance for left whisker, third is for right whisker
     * //last is result (movable object?)
            {0.12, 149, 298, 0.2},
            {-0.95, 700, 967, 0.79},
            {0.11, 122, 56, 0.12},
            {-0.23, 579, 793, 0.68},
            {0.33, 0, 0, 0} //no object touched

     // light sensor determine if there is an obstacle in front, if nshade>0, the shadow is on right, output{1,0},
     // so, that the right whisker is more significant in that case, so the light sensor can be a categorical variable.
     // touch sensor determine if the robot touches anything in front.
    };**/

    IsMovable(NeuralNetwork l, NeuralNetwork t, double[][] traindata){
        this.light = l;
        this.touch = t;
        this.traindata = traindata;
        this.ann= this.build();
    }

    private NeuralNetwork build() {
        // create multi layer perceptron
        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID,  5, 4, 8, 16, 2);
        // enable batch if using MomentumBackpropagation
        if (myMlPerceptron.getLearningRule() instanceof MomentumBackpropagation) {
            ((MomentumBackpropagation) myMlPerceptron.getLearningRule()).setBatchMode(true);
            ((MomentumBackpropagation) myMlPerceptron.getLearningRule()).setMaxIterations(2000);
            myMlPerceptron.randomizeWeights();
            myMlPerceptron.connectInputsToOutputs();
        }
        //myMlPerceptron.connectInputsToOutputs();
        return myMlPerceptron;
    }

    public DataSet CreateSet(double[][] data) {
        //create training set
        DataSet Set = new DataSet(5, 2);
        double[][] inputData = this.getInputData(data);
        double[][] outputData = this.getOutputData(data);
        for (int i = 0; i < data.length; i++) {
            Set.addRow(new DataSetRow(inputData[i], outputData[i]));
        }
        return  Set;
    }

    public void train(){
        DataSet trainingSet = CreateSet(traindata);
        ann.learn(trainingSet);
    }

    private double[][] getInputData(double[][] data) {
        double[][] inputData = new double[data.length][5];
        for (int i = 0; i < data.length; i++) {
            this.light.setInput(data[i][0]);// calculate result for the first input(nShade)
            this.light.calculate();
            double[] light_output = this.test_result(light.getOutput(), 0.1);

            this.touch.setInput(data[i][1], data[i][2]); // calculate result for touch sensor
            this.touch.calculate();
            double[] touch_output = touch.getOutput();

            if (data[0].length - 1 >= 0)
                System.arraycopy(light_output, 0, inputData[i], 0, 2);
                System.arraycopy(touch_output, 0, inputData[i], 2, 3);
        }
        return inputData;
    }

    private double[][] getOutputData(double[][] data) {
        double[][] outputData = new double[data.length][2];
        for (int i = 0; i < data.length; i++) {
            double[] row;
            if (data[i][data[i].length - 1] >= 0.4) {
                row = new double[]{1, 0}; // non-movable
            }
            else {
                row = new double[]{0, 1}; // movable
            }
            outputData[i] = row;
        }
        return outputData;
    }

    public double testnn(DataSet dataSet, String SetName) { //trainingset
        int count = 0;
        double accuracy;
        for (DataSetRow row : dataSet.getRows()) {
            ann.setInput(row.getInput());
            ann.calculate();
            double[] networkOutput = ann.getOutput();
            if (isOutputSame(networkOutput, row.getDesiredOutput())) {
                count++;
            }
        }
        accuracy = count / (double) dataSet.size();
        System.out.println(SetName + " success rate: " + accuracy);
        return accuracy;
    }

    private static boolean isOutputSame(double[] netOutput, double[] desiredOutput) {
        boolean same = true;
        for (int i = 0; i < netOutput.length; i++) {
            if (Math.abs(netOutput[i] - desiredOutput[i]) >= 0.2) {
                same = false;
            }
        }
        return same;
    }

    private double[] test_result(double[]output, double threshold){
        double[] result = new double[output.length];

            if (Math.abs(output[0]-1)<threshold){
                result[0]=1;
                result[1] = 0;
            }
            else if (Math.abs(output[1]-1)<threshold){
                result[1] = 1;
                result [0] = 0;
            }
            else {
                result[0] = result[1] = 0;
            }

            return result;
    }




}
