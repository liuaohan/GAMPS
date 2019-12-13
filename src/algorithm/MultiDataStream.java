package algorithm;


public class MultiDataStream {
    private DataStream[] signals;

    private int numOfStream;

    private int count;


    public MultiDataStream(int numOfStream) {
        this.numOfStream = numOfStream;
        signals = new DataStream[numOfStream];
        count = 0;

    }

    public MultiDataStream() {

    }

    public DataStream[] getSignals() {
        return signals;
    }

    public void setSignals(DataStream[] signals) {
        this.signals = signals;
    }

    public int getNumOfStream() {
        return numOfStream;
    }

    public void setNumOfStream(int numOfStream) {
        this.numOfStream = numOfStream;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addSingleStream(DataStream ds) {
        signals[count++] = ds;
        //ds.statistic();

        // Normalize all streams' size to be equal
        if (count > 1 && (signals[count - 2].size() != ds.size())) {
            int minLength = signals[count - 2].size();
            if (ds.size() < minLength)
                minLength = ds.size();
            for (int i = 0; i < count; i++) {
                if (minLength < signals[i].size()) {
                    signals[i].reduceSize(minLength);
                    signals[i].statistic();
                }
            }
        }
    }

}
