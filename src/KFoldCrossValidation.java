import org.apache.commons.lang3.SerializationUtils;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;


public class KFoldCrossValidation {
    int K;
    NeuralNetwork nn;
    DataSet dataSet;
    double[][] resultList;
    double resultmean;
    double threshold;


    public KFoldCrossValidation(NeuralNetwork nn, DataSet dataSet, int K, double threshold) {
        this.dataSet = dataSet;
        this.K = K;
        this.nn = nn;
        this.threshold = threshold;
    }

    public ArrayList<DataSet> split() {
        ArrayList<DataSet> result = new ArrayList<>(K);
        DataSet oldSet = SerializationUtils.clone(this.dataSet);
        oldSet.shuffle();
        int foldSize = (int) Math.floor(oldSet.size() / this.K);
        for (int i = 0; i < this.K-1; i++) {
            DataSet set = new DataSet(oldSet.getInputSize(), oldSet.getOutputSize());
            for (int j = 0; j < foldSize; j++) {
                set.addRow(oldSet.getRowAt(j));
                oldSet.removeRowAt(j);
            }
            result.add(set);
        }
        result.add(oldSet);
        return result;
    }

    public ArrayList<DataSet> split(DataSet dataSet) {
        ArrayList<DataSet> result = new ArrayList<>(K);
        dataSet.shuffle();
        int foldSize = dataSet.size() / this.K;
        for (int i = 0; i < this.K; i++) {
            DataSet set = new DataSet(dataSet.getInputSize(), dataSet.getOutputSize());
            for (int j = 0; j < foldSize; j++) {
                set.addRow(dataSet.getRowAt(i));
            }
            result.add(set);
        }
        return result;
    }

    public double testnn(NeuralNetwork ann, DataSet dataSet, String SetName) { //trainingset
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

    private boolean isOutputSame(double[] netOutput, double[] desiredOutput) {
        boolean same = true;
        for (int i = 0; i < netOutput.length; i++) {
            if (Math.abs(netOutput[i] - desiredOutput[i]) >= this.threshold) {
                same = false;
            }
        }
        return same;
    }



    public void run() {
        this.resultList = new double[K][2];
        ArrayList<DataSet> Sets = this.split();
        double sum = 0;
        for (int j = 0; j < K; j++) { //K times crossvalidation
            NeuralNetwork neuralNet = SerializationUtils.clone(this.nn);
            DataSet trainSet = new DataSet(this.dataSet.getInputSize(), this.dataSet.getOutputSize());
            for (int i = 0; i < K; i++) { // each time merge all sbsets != j, as trainset
                if (i != j) {
                    for(DataSetRow row: Sets.get(i).getRows()){
                        trainSet.addRow(row);
                    }
                }
            }
            //System.out.println(Sets.get(j).toString()+ "end");
            neuralNet.learn(trainSet); // learn
            //System.out.println(trainSet.toString());
            double accuracy = testnn(neuralNet, Sets.get(j), "Set " + j); // test nn
            resultList[j][0] = j;
            resultList[j][1] = accuracy;
            sum += accuracy;
        }
        resultmean = sum/K;

    }

    public void printAllResult(){
        System.out.println(Arrays.deepToString(resultList));
        System.out.println(resultmean);
    }

    public double getMean(){
        return resultmean;
    }



}
