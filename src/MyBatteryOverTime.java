import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.core.transfer.Tanh;

import java.util.Arrays;
import java.util.Random;

import org.neuroph.core.Layer;
public class MyBatteryOverTime {

    public NeuralNetwork ann;
    public double[][] traindata;

    public MyBatteryOverTime(double[][] traindata) {
        this.traindata = traindata;
        ann = this.build();
    }

    private NeuralNetwork build() {

        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.TRAPEZOID, 4, 8, 16, 8, 3);
        if (myMlPerceptron.getLearningRule() instanceof MomentumBackpropagation){
            ((MomentumBackpropagation) myMlPerceptron.getLearningRule()).setMaxIterations(1000);
            ((MomentumBackpropagation) myMlPerceptron.getLearningRule()).setBatchMode(true);
        }
        return myMlPerceptron;
    }

    public DataSet CreateSet(double[][] data) {
        //create training set
        DataSet Set = new DataSet(4, 3);
        double[][] inputData = this.getInputData(data);
        double[][] outputData = this.getOutputData(data);
        for (int i = 0; i < data.length; i++) {
            Set.addRow(new DataSetRow(inputData[i], outputData[i]));
        }
        return  Set;
    }

    public void train() {
        //create training set
        DataSet trainingSet = CreateSet(traindata);
        /*double[] weights = new double[27];
        for(int i = 0; i < 27; i++){
            weights[i] = 0.1;
        }
        ann.setWeights(weights);*/
        ann.learn(trainingSet);
    }

    private double[][] getInputData(double[][] data) {
        double[][] inputData = new double[data.length][4];
        for (int i = 0; i < data.length; i++) {
            double[] row = new double[4];
            if (data[0].length - 1 >= 0) {
                inputData[i][0] = data[i][0];
                if(data[i][1] == 100){
                    System.arraycopy(new double[]{1,0,0}, 0, inputData[i], 1, 3);
                }
                else if(data[i][1] == 50){
                    System.arraycopy(new double[]{0,1,0}, 0, inputData[i], 1, 3);
                }
                else if(data[i][1] == 10){
                    System.arraycopy(new double[]{0,0,1}, 0, inputData[i], 1, 3);
                }
                else {
                    System.arraycopy(new double[]{0,0,0}, 0, inputData[i], 1, 3);
                }
            }
        }
        //System.out.println(Arrays.deepToString(inputData));
        return inputData;
    }

    private double[][] getOutputData(double[][] data) {
        double[][] outputData = new double[data.length][3];
        for (int i = 0; i < data.length; i++) {
            double[] row = new double[]{0, 0, 0} ;
            if (data[i][data[i].length - 1] <= 0.1) {
                row = new double[]{1, 0, 0};
            } else if ((data[i][data[i].length - 1] >=0.3) && (data[i][data[i].length - 1] <=0.6)  ) {
                row = new double[]{0, 1, 0};
            } else if (data[i][data[i].length - 1] >= 0.6){
                row = new double[]{0, 0, 1};
            }
            outputData[i] = row;
        }
        return outputData;
    }

    public double testnn(DataSet dataSet, String SetName) { //trainingset
        int count = 0;
        double accuracy = 0;
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
            if (Math.abs(netOutput[i] - desiredOutput[i]) >= 0.001) {
                same = false;
            }
        }
        return same;
    }
}
