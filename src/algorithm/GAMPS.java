package algorithm;


import java.lang.reflect.Array;
import java.util.ArrayList;

public class GAMPS {

    private MultiDataStream mds;

    private double c1;

    private double c2;

    private float eps;

    public float getEps() {
        return eps;
    }

    public void setEps(float eps) {
        this.eps = eps;
    }

    public GAMPS(MultiDataStream mds) {
        this.mds = mds;
    }

    public double getC1() {
        return c1;
    }

    public void setC1(double c1) {
        this.c1 = c1;
    }

    public double getC2() {
        return c2;
    }

    public void setC2(double c2) {
        this.c2 = c2;
    }

    public int Compute() {
        // init
        int numOfStream = mds.getNumOfStream();
        ArrayList<GAMPSEntry>[] listBucket = (ArrayList<GAMPSEntry>[]) Array.newInstance(ArrayList.class, numOfStream);//new ArrayList<GAMPSEntry>[numOfStream];
        ArrayList<GAMPSEntry>[] listRatioSignalBucket = (ArrayList<GAMPSEntry>[]) Array.newInstance(ArrayList.class, numOfStream * numOfStream);//new GAMPSEntry[numOfStream * numOfStream][];
        //GAMPSEntry[][] listRatioSignalBucket = new GAMPSEntry[numOfStream * numOfStream][];
        double eps = this.getEps();
        double eps1 = 0;
        double eps2 = 0;

        // Apply APCA
        for (int j = 0; j < numOfStream; j++) {
            // choose one signal => base signal, compress it and push into base signal bucket list
            DataStream baseSignal = mds.getSignals()[j];

            // calculate % eps
            baseSignal.statistic();

            /*int errorType = 1;

            if (errorType == 0) {
                eps = 0.05;
            } else {
                eps = 0.1 * (baseSignal.getMax() - baseSignal.getMin());
            }*/
            eps = this.getEps() * (baseSignal.getMax() - baseSignal.getMin());
            eps1 = 0.4 * eps;

            ArrayList<GAMPSEntry> listBaseSignalBucket = compress_APCA(baseSignal, eps1);
            listBucket[j] = listBaseSignalBucket;

            for (int i = 0; i < numOfStream; i++) {
			/*
			foreach signal:
				+ calculate ratio with base signal
				+ apply APCA and push it into ratio bucket list
			*/
                DataStream ratioSignal = mds.getSignals()[i];
                ArrayList<GAMPSEntry> listComputeRatioSignal = computeRatioSignal(ratioSignal, baseSignal);

                eps2 = computeEps2(eps1);
                ArrayList<GAMPSEntry> listRatioBucket = compress_APCA(listComputeRatioSignal, eps2);
                int pos = j * numOfStream + i;
                listRatioSignalBucket[pos] = listRatioBucket;
            }
        }

        /*************** facility location *************/

        int bSize = numOfStream;
        int[] baseBucketCost = new int[numOfStream];
        ArrayList<Integer>[] ratioSignalCost = (ArrayList<Integer>[]) Array.newInstance(ArrayList.class, numOfStream);

        // calculate each base signal bucket cost
        for (int i = 0; i < bSize; i++) {
            int oneBSize = listBucket[i].size();
            baseBucketCost[i] = oneBSize;
        }

        // put ratio signal cost into 2 dimension array array[baseSignal][ratioSignal]

        for (int i = 0; i < bSize; i++) {
            ArrayList<Integer> tempArray = new ArrayList<>();
            for (int j = 0; j < bSize; j++) {
                if (i != j) {
                    int temp = 0;
                    int pos = i * bSize + j;
                    int oneSSize = listRatioSignalBucket[pos].size();
                    tempArray.add(oneSSize);
                } else {
                    int bucketSize = baseBucketCost[i];
                    tempArray.add(bucketSize);
                }
            }
            ratioSignalCost[i] = tempArray;
        }

        FacilityLocation facilityLocation = new FacilityLocation(numOfStream);
        facilityLocation.setArrBaseCost(baseBucketCost);
        facilityLocation.setRatioCost(ratioSignalCost);

        // find optimal solution for faciliting location
        int totalCost = facilityLocation.findOptimalSolution();

        GampsOutput output = new GampsOutput();
        output.setEps(this.eps);

        output.setTgood(facilityLocation.getTgood());
        output.setTgoodSize(numOfStream);

        output = computeOutput(output, listBucket, listRatioSignalBucket);

        //output.setListResultBaseSignal(listBucket);
        //output.setListResultRatioSignal(listRatioSignalBucket);
        output.setMds(mds);
        System.out.println(output.getCompressionRatio());


        return totalCost;

    }

    // Purpose	: apply APCA to compress original data with maximum error tolerance
    // Parameter:
    //            stream: data need to compress
    //            eps: maximum error tolerance - epsilon
    private ArrayList<GAMPSEntry> compress_APCA(DataStream computeRatioList, double esp) {
        ArrayList<GAMPSEntry> compressData = new ArrayList<>();
        double doubleEsp = 2 * esp;
        double currentMax = 0;
        double currentMin = 0;
        int inputCount = computeRatioList.size();


        if (inputCount <= 0) return compressData;
        DataItem item = computeRatioList.get(0);
        currentMax = item.getVal();
        currentMin = item.getVal();

        for (int i = 0; i < inputCount; i++) {
            double tempMax = currentMax;
            double tempMin = currentMin;
            item = computeRatioList.get(i);
            double newValue = item.getVal();

            if (currentMax < newValue)
                tempMax = newValue;

            if (currentMin > newValue)
                tempMin = newValue;

            if ((tempMax - tempMin) > doubleEsp) {
                GAMPSEntry compress = new GAMPSEntry((currentMax + currentMin) / 2, item.getTimestamp() - 1);
                currentMax = newValue;
                currentMin = newValue;
                compressData.add(compress);
            } else {
                currentMax = tempMax;
                currentMin = tempMin;
            }
        }

        //add the last point
        GAMPSEntry entry = new GAMPSEntry((currentMax + currentMin) / 2, inputCount);
        compressData.add(entry);

        return compressData;
    }

    // Purpose	: apply APCA to compress ratio signal with maximum error tolerance
// Parameter: 
//            computeRatioList: ratio signal to compress
//            eps: maximum error tolerance - epsilon       
    private ArrayList<GAMPSEntry> compress_APCA(ArrayList<GAMPSEntry> computeRatioList, double esp) {
        ArrayList<GAMPSEntry> compressData = new ArrayList<GAMPSEntry>();
        double doubleEsp = 2 * esp;
        double currentMax, currentMin = 0;

        int inputCount = computeRatioList.size();
        if (inputCount <= 0) return compressData;

        GAMPSEntry item = computeRatioList.get(0);
        currentMax = item.getValue();
        currentMin = item.getValue();

        for (int i = 0; i < inputCount; i++) {
            double tempMax = currentMax;
            double tempMin = currentMin;
            item = computeRatioList.get(i);
            double newValue = item.getValue();

            if (currentMax < newValue)
                tempMax = newValue;

            if (currentMin > newValue)
                tempMin = newValue;

            if ((tempMax - tempMin) > doubleEsp) {
                GAMPSEntry compress = new GAMPSEntry((currentMax + currentMin) / 2, item.getEndingTimestamp() - 1);
                //compress.value = (currentMax + currentMin) / 2;
                //compress.endingTimestamp = item.endingTimestamp - 1;
                currentMax = newValue;
                currentMin = newValue;
                compressData.add(compress);
            } else {
                currentMax = tempMax;
                currentMin = tempMin;
            }
        }

        //add the last point 
        GAMPSEntry entry = new GAMPSEntry((currentMax + currentMin) / 2, inputCount);
        //entry.value = (currentMax + currentMin) / 2;
        //entry.endingTimestamp = inputCount;
        compressData.add(entry);

        return compressData;
    }

    private ArrayList<GAMPSEntry> computeRatioSignal(DataStream computeSignal, DataStream baseSignal) {
        double maxC1 = 0, maxC2 = 0;
        ArrayList<GAMPSEntry> listRatioSignal = new ArrayList<GAMPSEntry>();
        int baseSignalSize = baseSignal.size();

        if (baseSignalSize > 0) {
            DataItem baseEntry = baseSignal.get(0);
            DataItem computeEntry = computeSignal.get(0);
            if (baseEntry.getVal() < 1 && baseEntry.getVal() > -1)
                baseEntry.setVal(1);

            double ratioValue = computeEntry.getVal() / baseEntry.getVal();
            GAMPSEntry ratioEntry = new GAMPSEntry(ratioValue, baseEntry.getTimestamp());
            listRatioSignal.add(ratioEntry);
            maxC1 = ratioValue;
            maxC2 = baseEntry.getVal();
        }

        for (int i = 1; i < baseSignalSize; i++) {
            DataItem baseEntry = baseSignal.get(i);
            DataItem computeEntry = computeSignal.get(i);

            // in case of baseEntry.value == 0
            if (baseEntry.getVal() < 1 && baseEntry.getVal() > -1)
                baseEntry.setVal(1);

            double ratioValue = computeEntry.getVal() / baseEntry.getVal();

            GAMPSEntry ratioEntry = new GAMPSEntry(ratioValue, baseEntry.getTimestamp());
            listRatioSignal.add(ratioEntry);

            if (ratioValue > maxC1)
                maxC1 = ratioValue;

            if (baseEntry.getVal() > maxC2)
                maxC2 = baseEntry.getVal();
        }

        setC1(maxC1);
        setC2(maxC2);

        return listRatioSignal;
    }

    private double computeEps2(double eps1) {
        double eps2 = this.eps - c1 * eps1;
        eps2 = eps2 / (c2 + eps1);
        return eps2;
    }

    /*public GAMPS getWndData(int from, int wSize) {
        MultiDataStream mds = new MultiDataStream(this.mds.getNumOfStream());
        DataStream temp = new DataStream();

        for (int i = 0; i < mds.getNumOfStream(); i++) {
            temp = mds.getSignals()[i];
            int limit = temp.size() > from + wSize ? from + wSize : temp.size();
            DataStream newStream = new DataStream();
            int tempCount = 0;

            for (int j = from; j < limit; j++) {
                GAMPSEntry entry = temp.get(j);
                //GAMPSEntry tempEntry = new GAMPSEntry(entry.getVal(), entry.getTimestamp());
                newStream.add(entry);//getSignal()[newStream.getSignal().length] = entry;
            }
            mds.addSingleStream(newStream);
        }

        GAMPS res = new GAMPS(mds);
        return res;
    }*/
    private GampsOutput computeOutput(GampsOutput output, ArrayList<GAMPSEntry>[] baseBucketList, ArrayList<GAMPSEntry>[] ratioBucketList) {
        int numOfStream = this.mds.getNumOfStream();
        int baseBucketCount = 0;
        int baseCount = 0;
        int ratioCount = 0;

        for (int i = 0; i < numOfStream; i++) {
            if (output.getTgood()[i] == i)
                baseBucketCount++;
        }

        ArrayList<GAMPSEntry>[] resultBaseSignal = (ArrayList<GAMPSEntry>[]) Array.newInstance(ArrayList.class, numOfStream);
        ArrayList<GAMPSEntry>[] resultRatioSignal = (ArrayList<GAMPSEntry>[]) Array.newInstance(ArrayList.class, numOfStream);

        if (baseBucketCount < numOfStream)
            resultRatioSignal = (ArrayList<GAMPSEntry>[]) Array.newInstance(ArrayList.class, numOfStream - baseBucketCount);


            for (int i = 0; i < numOfStream; i++) {
                if (output.getTgood()[i] == i) {
                    ArrayList<GAMPSEntry> temp = new ArrayList<>(baseBucketList[i]);
                    resultBaseSignal[baseCount++] = temp;
                } else {
                    int pos = output.getTgood()[i] * numOfStream + i;
                    ArrayList<GAMPSEntry> temp = new ArrayList<GAMPSEntry>(ratioBucketList[pos]);
                    resultRatioSignal[ratioCount++] = temp;
                }
            }

        output.setListResultBaseSignal(resultBaseSignal);
        output.setListResultRatioSignal(resultRatioSignal);
        return output;
    }

}
