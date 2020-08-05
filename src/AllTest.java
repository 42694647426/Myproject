import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.neuroph.core.data.DataSet;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class AllTest {
    private String sensor;

    public AllTest(String sensor){
        this.sensor = sensor;
    }

    public static double[][] MovableData(int N){
        double[][] data = new double[N][4];
        for (int i =0; i<N; i++){
            data[i][0] = Math.random()*2-1;//light sensor input
            data[i][1] = new Random().nextInt(850)+150;
            data[i][2] = new Random().nextInt(850)+150;
            if(data[i][0] > 0&& data[i][1] >= 650){
                data[i][3] = Math.random()*0.6+0.4; // non-movable
            }
            else if(data[i][1] <0 && data[i][2] >=650){
                data[i][3] = Math.random()*0.6+0.4; // non-movable
            }
            else if(data[i][1] >=650&&data[i][2] >=650){
                data[i][3] = Math.random()*0.6+0.4;
            }
            else {
                data[i][3] = Math.random()*0.4;
            }
        }
        return data;
    }

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
            if(random1>=650){
                //right wisker
                data[i][2] =  Math.random() *6 +9;
            }
            else if(random2>=650){
                //left wisker
                data[i][3] =  Math.random() *6 +9;
            }
            else if(random1>=650&&random2>=650){
                data[i][2] =  Math.random() *6 +9;
                data[i][3] =  Math.random() *6 +9;
            }
            else {
                data[i][2] = Math.random() *9;
                data[i][3] = Math.random() *9;
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

    public void plot(int N){ // N is how many times it will test
        final XYSeries trainseries = new XYSeries("Accuracy of Neural Network training for " + sensor + "Sensor train dataset");
        final XYSeries testseries = new XYSeries("Accuracy of Neural Network training for " + sensor + "Sensor test dataset");
        if (sensor.equals("Battery")){
            for(int i =0; i<N; i++) {
                Random rand = new Random();
                int random = rand.nextInt(700)+800;
                double[][] traindata = BatteryData(random);
                MyBatterySensor battery = new MyBatterySensor(traindata);
                battery.train();
                DataSet trainset = battery.CreateSet(traindata);
                double train_accuracy = battery.testnn(trainset, "Train Set");
                trainseries.add(i+1, train_accuracy);

                random = rand.nextInt(500)+500;
                double[][] testdata = BatteryData(random);
                DataSet testset = battery.CreateSet(testdata);
                battery.testnn(testset, "Test Set");
                double test_accuracy = battery.testnn(testset, "Train Set");
                testseries.add(i+1, test_accuracy);
            }
        }
        else if (sensor.equals("BatteryOverTime")){
            for(int i =0; i<N; i++) {
                Random rand = new Random();
                int random = rand.nextInt(700) + 800;
                double[][] traindata = BatteryData(random);
                MyBatteryOverTime Batterytime = new MyBatteryOverTime(traindata);
                Batterytime.train();
                DataSet trainset = Batterytime.CreateSet(traindata);
                double train_accuracy = Batterytime.testnn(trainset, "Train Set");
                trainseries.add(i + 1, train_accuracy);

                random = rand.nextInt(500) + 500;
                double[][] testdata = BatteryData(random);
                DataSet testset = Batterytime.CreateSet(testdata);
                double test_accuracy = Batterytime.testnn(testset, "Test Set");
                testseries.add(i + 1, test_accuracy);
            }

        }
        else if (sensor.equals("Touch")){
            for(int i =0; i<N; i++) {
                Random rand = new Random();
                int random = rand.nextInt(700) + 800;
                double[][] touchdata = TouchData(random);
                TouchSensor touch = new TouchSensor(touchdata);
                touch.train();;
                DataSet touchtrain = touch.CreateSet(touchdata);
                double train_accuracy = touch.testnn(touchtrain, "train data for touch sensor");
                trainseries.add(i + 1, train_accuracy);

                random = rand.nextInt(500) + 500;
                double[][] touchdata2 = TouchData(random);
                DataSet touchtest = touch.CreateSet(touchdata2);
                double test_accuracy = touch.testnn(touchtest, "test data for touch sensor");
                testseries.add(i + 1, test_accuracy);
            }
        }
        else if (sensor.equals("Light")){
            for(int i =0; i<N; i++) {
                Random rand = new Random();
                int random = rand.nextInt(700) + 800;
                double[][] lightdata = LightData(random);
                LightSensor light = new LightSensor(lightdata);
                light.train();
                DataSet lighttrain = light.CreateSet(lightdata);
                double train_accuracy = light.testnn(lighttrain, "train data for touch sensor");
                trainseries.add(i + 1, train_accuracy);

                random = rand.nextInt(500) + 500;
                double[][] lightdata2 = LightData(random);
                DataSet lighttest = light.CreateSet(lightdata2);
                double test_accuracy = light.testnn(lighttest, "test data for touch sensor");
                testseries.add(i + 1, test_accuracy);
            }

        }
        else if (sensor.equals("IsMovable")){
            for(int i =0; i<N; i++) {
                Random rand = new Random();
                int random = rand.nextInt(700) + 800;
                double[][] movabledata = MovableData(random);
                double[][] lightdata = LightData(random);
                LightSensor light = new LightSensor(lightdata);
                light.train();
                double[][] touchdata = TouchData(random);
                TouchSensor touch = new TouchSensor(touchdata);
                touch.train();;
                IsMovable move = new IsMovable(light.ann,touch.ann , movabledata);

                move.train();
                DataSet movetrain = move.CreateSet(movabledata);
                double train_accuracy = move.testnn(movetrain, "train data for IsMovable");
                trainseries.add(i + 1, train_accuracy);

                random = rand.nextInt(500) + 500;
                double[][] movedata2 = MovableData(random);
                DataSet movetest = move.CreateSet(movedata2);
                double test_accuracy = move.testnn(movetest, "test data for IsMovable");
                testseries.add(i + 1, test_accuracy);
            }
            double[][] movabledata = MovableData(20);
            System.out.println(Arrays.deepToString(movabledata));

        }
        else{
            throw new IllegalArgumentException("Wrong Sensor input!");
        }
        final XYSeriesCollection traindata = new XYSeriesCollection(trainseries);
        final XYSeriesCollection testdata = new XYSeriesCollection(testseries);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Accuracy of Neural Network training for " + sensor + "Sensor train dataset",
                "times",
                "accuracy",
                traindata,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final JFreeChart chart2 = ChartFactory.createXYLineChart(
                "Accuracy of Neural Network training for " + sensor + "Sensor test dataset",
                "times",
                "accuracy",
                testdata,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        // plot settings
        XYPlot plot = chart.getXYPlot();
        var renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
        chart.getLegend().setFrame(BlockBorder.NONE);

        XYPlot plot2 = chart2.getXYPlot();
        var renderer2 = new XYLineAndShapeRenderer();
        renderer2.setSeriesPaint(0, Color.RED);
        renderer2.setSeriesStroke(0, new BasicStroke(2.0f));
        plot2.setRenderer(renderer);
        chart2.getLegend().setFrame(BlockBorder.NONE);

        JFrame frame = new JFrame("Line chart");
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 600));
        frame.add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JFrame frame2 = new JFrame("Line chart");
        final ChartPanel chartPanel2 = new ChartPanel(chart2);
        chartPanel2.setPreferredSize(new java.awt.Dimension(500, 600));
        frame2.add(chartPanel2);
        frame2.pack();
        frame2.setLocationRelativeTo(null);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setVisible(true);

    }

}
