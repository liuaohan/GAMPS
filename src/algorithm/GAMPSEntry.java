package algorithm;

public class GAMPSEntry {

    private double value;
    private int endingTimestamp;

    public GAMPSEntry(double v, int e) {
        value = v;
        endingTimestamp = e;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getEndingTimestamp() {
        return endingTimestamp;
    }

    public void setEndingTimestamp(int endingTimestamp) {
        this.endingTimestamp = endingTimestamp;
    }
}
