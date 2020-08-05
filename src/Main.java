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

    public static double[][] TouchData(int N){
        double[][] data = new double[N][4];
        for (int i =0; i<N; i++){
            Random rand  = new Random();
            int random1 = rand.nextInt(850)+150;
            int random2 = rand.nextInt(850)+150;
            data[i][0] = random1;
            data[i][1] = random2;
            if(random1>=280){
                //right wisker
                data[i][2] = Math.random() * 5;
            }
            else if(random2>=280){
                //left wisker
                data[i][3] = Math.random() * 5;
            }
            else if(random1>=280&&random2>=280){
                data[i][2] = Math.random() * 5;
                data[i][3] = Math.random() * 5;
            }
            else {
                data[i][2] = Math.random() * 10+5;
                data[i][3] = Math.random() * 10+5;
            }
        }
        return data;

    }

    public static double[][] LightData(int N){
        double[][] data = new double[N][2];
        for (int i =0; i<N; i++){
            double random  = Math.random()*2-1;
            data[i][0] = random;
            if(random>0){
                data[i][1] = 1; //right shadow
            }
            else if (random<0){
                data[i][1] = 2; //right shadow
            }
            else data[i][1] = 0; //no shadow
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
        /**double[][] traindata = BatteryData(1039);
        MyBatterySensor battery = new MyBatterySensor(traindata);
        battery.train();
        DataSet trainset = battery.CreateSet(traindata);
        System.out.println("test begins");
        battery.testnn(trainset, "Train Set");
        KFoldCrossValidation crossvalidation = new KFoldCrossValidation(battery.ann, trainset, 5, 0.01);
        crossvalidation.run();
        crossvalidation.printAllResult();
        double[][] testdata = BatteryData(579);

        DataSet testset = battery.CreateSet(testdata);
        battery.testnn(testset, "Test Set");

        //BtteryOverTime
        System.out.println("BatteryTime begins");

        double[][] traindata2 = BatteryTimeData(1347);
        MyBatteryOverTime Batterytime  = new MyBatteryOverTime(traindata2);
        Batterytime.train();
        DataSet trainset2 = Batterytime.CreateSet(traindata2);
        System.out.println("BatteryTime training done");
        Batterytime.testnn(trainset2, "Train Set");
        double[][] testdata2 = BatteryTimeData(529);
        DataSet testset2 = Batterytime.CreateSet(testdata2);
        Batterytime.testnn(testset2, "Test Set");
        KFoldCrossValidation crossvalidation2 = new KFoldCrossValidation(Batterytime.ann, trainset2, 5, 0.01);
        crossvalidation2.run();
        crossvalidation2.printAllResult();

        //touch sensor
        System.out.println("TouchSensor begins");
        double[][] touchdata = TouchData(1539);
        TouchSensor touch = new TouchSensor(touchdata);
        touch.train();;
        DataSet touchtrain = touch.CreateSet(touchdata);
        touch.testnn(touchtrain, "train data for touch sensor");
        double[][] touchdata2 = TouchData(577);
        DataSet touchtest = touch.CreateSet(touchdata2);
        touch.testnn(touchtest, "test data for touch sensor");
        KFoldCrossValidation touchvalidation =  new KFoldCrossValidation(touch.ann, touchtrain, 5, 0.01);
        touchvalidation.run();
        touchvalidation.printAllResult();

        // light sensor
        System.out.println("LightSensor begins");
        double[][] lightdata = LightData(1637);
        LightSensor light = new LightSensor(lightdata);
        light.train();;
        DataSet lighttrain = light.CreateSet(lightdata);
        light.testnn(lighttrain, "train data for touch sensor");
        double[][] lightdata2 = LightData(537);
        DataSet lighttest = light.CreateSet(lightdata2);
        light.testnn(lighttest, "test data for touch sensor");
        KFoldCrossValidation lightvalidation =  new KFoldCrossValidation(light.ann, lighttrain, 5, 0.01);
        lightvalidation.run();
        lightvalidation.printAllResult();
         **/
        AllTest test = new AllTest("IsMovable");
        test.plot(10);
    }


}
