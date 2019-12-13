package algorithm;


import java.util.ArrayList;

public class DataStream extends ArrayList<DataItem> {

    private double min;
    private double max;
    private double mean;
    private double variance;


    public DataStream() {
        super();
    }

    public DataStream(ArrayList signal) {
        super(signal);
    }

    public double getMin() {
        return min;
    }


    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public void statistic() {
        // Find minimum and maximum value and sum
        int dataSize = size();
        double total = 0;

        if (dataSize > 0) {
            DataItem entry = get(0);
            double temp = entry.getVal();
            min = max = temp;
        }

        for (int i = 1; i < dataSize; i++) {
            DataItem entry = get(i);
            double temp = entry.getVal();
            total = total + temp;
            if (temp > max && temp < 20000) max = temp;
            if (temp < min) min = temp;
        }

        // Calculate mean
        mean = total / dataSize;

        // Calculate variance
        variance = 0;
        for (int i = 0; i < dataSize; i++) {
            DataItem entry = get(i);
            double temp = entry.getVal();
            double var = temp - mean;
            variance = variance + Math.pow(var, 2);
        }
        variance = variance / dataSize;
        variance = Math.sqrt(variance);
    }


    public void reduceSize(int newSize) {
        //DataItem[] temp_values = new DataItem[newSize];
        removeRange(newSize, size());
    }


    void write_to_File(String path) {

    }

}
