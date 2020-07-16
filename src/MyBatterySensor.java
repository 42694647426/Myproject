import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class MyBatterySensor {

    public NeuralNetwork ann;
    double[][] traindata;

    public MyBatterySensor(double[][] traindata) {
        this.traindata = traindata;
        ann = this.build();
    }

    private NeuralNetwork build() {
        // create multi layer perceptron
        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID,  1, 4, 3);
        // enable batch if using MomentumBackpropagation
        if (myMlPerceptron.getLearningRule() instanceof MomentumBackpropagation) {
            ((MomentumBackpropagation) myMlPerceptron.getLearningRule()).setBatchMode(true);
        }
        //myMlPerceptron.connectInputsToOutputs();
        return myMlPerceptron;
    }

    public DataSet CreateSet(double[][] data) {
        //create training set
        DataSet Set = new DataSet(1, 3);
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
        double[][] inputData = new double[data.length][data[0].length - 1];
        for (int i = 0; i < data.length; i++) {
            double[] row = new double[data[0].length - 1];
            if (data[0].length - 1 >= 0)
                System.arraycopy(data[i], 0, row, 0, data[0].length - 1);
            inputData[i] = row;
        }
        return inputData;
    }

    private double[][] getOutputData(double[][] data) {
        double[][] outputData = new double[data.length][3];
        for (int i = 0; i < data.length; i++) {
            double[] row;
            if (data[i][data[i].length - 1] >= 3.80) {
                row = new double[]{1, 0, 0};
            } else if ((data[i][data[i].length - 1] >=3.70) && (data[i][data[i].length - 1] <=3.80)  ) {
                row = new double[]{0, 1, 0};
            } else {
                row = new double[]{0, 0, 1};
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
            if (Math.abs(netOutput[i] - desiredOutput[i]) >= 0.01) {
                same = false;
            }
        }
        return same;
    }

}
