package algorithm;

import java.util.ArrayList;

public class FacilityLocation {

    private int NumOfStream;
    private int arrBaseCost[];
    private ArrayList<Integer>[] RatioCost;
    private int Tgood[];

    public FacilityLocation(int numOfStream) {
        this.NumOfStream = numOfStream;
        this.Tgood = new int[NumOfStream];
    }

    public int[] getTgood() {
        return this.Tgood;
    }

    public void setArrBaseCost(int[] arrBaseCost) {
        this.arrBaseCost = arrBaseCost;
    }


    public void setRatioCost(ArrayList<Integer>[] RatioCost) {
        this.RatioCost = RatioCost;
    }

    // Purpose	: find facility location solution to minimize memory cost problem
    // Return	: (int) allocated memory cost
    public int findOptimalSolution() {
        // init Tgood and Tgoodold and run array for caculating reduce cost
        int totalCost = 0;
        int[] selectedSignal = new int[NumOfStream];

        for (int i = 0; i < NumOfStream; i++) {
            Tgood[i] = i;
            totalCost += arrBaseCost[i];
            selectedSignal[i] = 0;
        }

        // Repeat until no changes between previous and current solution
        while (true) {
            int maxReduceCostPos = -1;
            int maxReduceCost = 0;

            // calculate reduce cost of signal ith
            for (int i = 0; (i < NumOfStream) && (selectedSignal[i] == 0); i++) {
                int reduceCost = 0;
                for (int j = 0; j < NumOfStream && (selectedSignal[j] == 0); j++) {
                    int delta = arrBaseCost[j] - RatioCost[i].get(j);
                    if (delta > 0) {
                        reduceCost = reduceCost + delta;
                    }
                }

                if (reduceCost > 0 && reduceCost > maxReduceCost) {
                    maxReduceCostPos = i;
                    maxReduceCost = reduceCost;
                }
            }

            // Choose the signal, which has maximum reduce cost, to be base signal
            // And the remaining signals, which have reduce cost > 0, become ratio signals
            if (maxReduceCostPos != -1) {
                // update m_pTgood
                selectedSignal[maxReduceCostPos] = 1;
                Tgood[maxReduceCostPos] = maxReduceCostPos;

                for (int j = 0; j < NumOfStream && (selectedSignal[j] == 0); j++) {
                    int delta = arrBaseCost[j] - RatioCost[maxReduceCostPos].get(j);
                    if (delta > 0) {
                        Tgood[j] = maxReduceCostPos;
                        selectedSignal[j] = 1;
                    }
                }
            } else // Otherwise, all remaining signals become base signals
            {
                for (int j = 0; j < NumOfStream && (selectedSignal[j] == 0); j++) {
                    Tgood[j] = j;
                }
                break;
            }
        }

        totalCost = 0;
        for (int i = 0; i < NumOfStream; i++) {
            totalCost += RatioCost[Tgood[i]].get(i);
        }

        return totalCost;
    }

}
