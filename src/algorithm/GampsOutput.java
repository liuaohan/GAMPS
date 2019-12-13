package algorithm;

import java.util.ArrayList;

public class GampsOutput {

    private MultiDataStream mds;
    private double eps;

    private final int DOUBLE_SIZE = 4;
    private final int INT_SIZE = 4;
    // Compress data is the combination of base and ratio signals
    private ArrayList<GAMPSEntry>[] listResultBaseSignal;
    private ArrayList<GAMPSEntry>[] listResultRatioSignal;

    // Contains correspondence between base and ratio signals
    int TgoodSize;
    int[] Tgood;


    public double getEps() {
        return eps;
    }

    public void setEps(double eps) {
        this.eps = eps;
    }

    public GampsOutput() {
    }

    public int getTgoodSize() {
        return TgoodSize;
    }

    public void setTgoodSize(int tgoodSize) {
        TgoodSize = tgoodSize;
    }

    public int[] getTgood() {
        return Tgood;
    }

    public void setTgood(int[] tgood) {
        Tgood = tgood;
    }

    public MultiDataStream getMds() {
        return mds;
    }

    public void setMds(MultiDataStream mds) {
        this.mds = mds;
    }

    public ArrayList<GAMPSEntry>[] getListResultBaseSignal() {
        return listResultBaseSignal;
    }

    public void setListResultBaseSignal(ArrayList<GAMPSEntry>[] listResultBaseSignal) {
        this.listResultBaseSignal = listResultBaseSignal;
    }

    public ArrayList<GAMPSEntry>[] getListResultRatioSignal() {
        return listResultRatioSignal;
    }

    public void setListResultRatioSignal(ArrayList<GAMPSEntry>[] listResultRatioSignal) {
        this.listResultRatioSignal = listResultRatioSignal;
    }

    public double getCompressionRatio() {
        // Calculate total memory input
        double returnValue = 0;
        int numOfStream = mds.getNumOfStream();
        int signalLength = mds.getSignals()[0].size();
        int inputElementSize = DOUBLE_SIZE;
        int inputMemory = numOfStream * signalLength * inputElementSize; // all signal

        // Calculate total memory output
        int baseSignalOutputMemory = 0;
        int ratioSignalOutputMemory = 0;
        int baseSignalCount = 0;
        int ratioSignalCount = 0;
        int outputMemory = 0;
        int gampsEntrySize = DOUBLE_SIZE + INT_SIZE;
        // Do not care the Tgood size
        for (int i = 0; i < numOfStream; i++) {
            if (Tgood[i] == i) {//

                //System.out.println("基信号：" + i);
                baseSignalOutputMemory = baseSignalOutputMemory + listResultBaseSignal[baseSignalCount].size() * gampsEntrySize;
                baseSignalCount++;
            } else {
                ratioSignalOutputMemory = ratioSignalOutputMemory + listResultRatioSignal[ratioSignalCount].size() * gampsEntrySize;
                ratioSignalCount++;
            }
        }
        System.out.println("eps:"+eps);
        System.out.println(baseSignalCount + "/" + numOfStream + "个基信号");
        outputMemory = baseSignalOutputMemory + ratioSignalOutputMemory;
        System.out.println("baseSignalOutputMemory:" + baseSignalOutputMemory);
        System.out.println("ratioSignalOutputMemory:" + ratioSignalOutputMemory);
        System.out.println("outputMemory:" + outputMemory);
        System.out.println("inputMemory:" + inputMemory);

        returnValue = (double) outputMemory / (double) inputMemory;
        return returnValue;
    }
}
