import org.neuroph.core.data.DataSet;
import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class Main {
    public static double[][] BatteryData(int N){
        double[][] data = new double[N][2];
        for (int i =0; i<N; i++){
            double random  = Math.random();
            data[i][0] = random;
            if(random>=0.85){
                double upper = 4.0;
                double lower = 3.8;
                data[i][1] = Math.random() * (upper - lower) + lower;
            }
            else if(random>=0.5&&random<0.85){
                double upper = 3.8;
                double lower = 3.7;
                data[i][1] = Math.random() * (upper - lower) + lower;
            }
            else {
                double upper = 3.7;
                double lower = 0;
                data[i][1] = Math.random() * (upper - lower) + lower;
            }
        }
        return data;

    }

    public static double[][] LightData(int N){
        double[][] data = new double[N][2];
        for (int i =0; i<N; i++){
            double random  = Math.random();
            data[i][0] = random;
            if(random>=0.85){
                double upper = 4.0;
                double lower = 3.8;
                data[i][1] = Math.random() * (upper - lower) + lower;
            }
            else if(random>=0.5&&random<0.85){
                double upper = 3.8;
                double lower = 3.7;
                data[i][1] = Math.random() * (upper - lower) + lower;
            }
            else {
                double upper = 3.7;
                double lower = 0;
                data[i][1] = Math.random() * (upper - lower) + lower;
            }
        }
        return data;

    }

    public static double[][] BatteryTimeData(int N){
        double[][] data = new double[N][3];
        int[] intArray = {100, 50, 10, 1};
        for (int i =0; i<N; i++){
            double random1  = Math.random() * (9 - 5) + 5;
            data[i][0] = random1;
            int idx = new Random().nextInt(intArray.length);
            double random2 = intArray[idx];
            data[i][1] = random2;
            if(random2==100){
                if(random1>=7){
                    double upper = 0.1;
                    double lower = 0;
                    data[i][2] = Math.random() * (upper - lower) + lower;
                }
                else data[i][2] = Math.random() * (0.2 - 0.1) + 0.1;
            }
            else if(random2==50){
                if(random1>=8){
                    double upper = 0.1;
                    double lower = 0;
                    data[i][2] = Math.random() * (upper - lower) + lower;
                }
                else data[i][2] = Math.random() * (0.3 - 0.1) + 0.1;
            }
            else if (random2 == 10){
                if(random1>=9){
                    double upper = 0.1;
                    double lower = 0;
                    data[i][2] = Math.random() * (upper - lower) + lower;
                }
                else if(random1<9&&random1>8){
                    data[i][2] = Math.random() * (0.3 - 0.1) + 0.1;
                }
                else data[i][2] = Math.random() * (0.8 - 0.3) + 0.3;
            }
            else{
                if(random1>=9.5){
                    double upper = 0.1;
                    double lower = 0;
                    data[i][2] = Math.random() * (upper - lower) + lower;
                }
                else if(random1<9.5&&random1>8.5){
                    data[i][2] = Math.random() * (0.3 - 0.1) + 0.1;
                }
                else data[i][2] = Math.random() * (1 - 0.3) + 0.3;

            }
        }
        return data;

    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // first value is the state of charge in percentage
        // second is the output voltage
        double[][] traindata = BatteryData(200);
        MyBatterySensor battery = new MyBatterySensor(traindata);
        battery.train();
        DataSet trainset = battery.CreateSet(traindata);
        System.out.println("test begins");
        battery.testnn(trainset, "Train Set");
        KFoldCrossValidation crossvalidation = new KFoldCrossValidation(battery.ann, trainset, 3, 0.01);
        crossvalidation.run();
        crossvalidation.printAllResult();
        double[][] testdata = BatteryData(150);

        DataSet testset = battery.CreateSet(testdata);
        battery.testnn(testset, "Test Set");

        //BtteryOverTime
        System.out.println("BatteryTime begins");

        double[][] traindata2 = BatteryTimeData(150);
        MyBatteryOverTime Batterytime  = new MyBatteryOverTime(traindata2);
        Batterytime.train();
        DataSet trainset2 = Batterytime.CreateSet(traindata2);
        System.out.println("BatteryTime training done");
        Batterytime.testnn(trainset2, "Train Set");
        double[][] testdata2 = BatteryTimeData(150);
        DataSet testset2 = Batterytime.CreateSet(testdata2);
        Batterytime.testnn(testset2, "Test Set");
        KFoldCrossValidation crossvalidation2 = new KFoldCrossValidation(Batterytime.ann, trainset2, 3, 0.001);
        crossvalidation2.run();
        crossvalidation2.printAllResult();
    }


}
