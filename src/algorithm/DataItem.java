package algorithm;


public class DataItem {
    private double val;
    private int timestamp;

    public DataItem(double val, int timestamp) {
        this.val = val;
        this.timestamp = timestamp;
    }

    public double getVal() {
        return val;
    }

    public void setVal(double val) {
        this.val = val;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
