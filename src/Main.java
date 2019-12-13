import algorithm.GAMPS;
import algorithm.MultiDataStream;
import input.InputDataset;

import java.time.Instant;
import java.util.concurrent.TimeUnit;


public class Main {


    public static void main(String[] arg) throws Exception {
        InputDataset input = new InputDataset();
        MultiDataStream mds = new MultiDataStream();
        mds = input.getDataSet(mds, "CO2");

        float[] epss = {0.01f, 0.05f, 0.1f};
        //Instant start = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
        GAMPS gamps = new GAMPS(mds);

        for (float eps : epss) {
            Instant start = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
            gamps.setEps(eps);
            gamps.Compute();
            Instant end = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
            System.out.println(end.toEpochMilli() - start.toEpochMilli());
        }
        //Instant end = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));

        //GAMPS.Compute(input.signals, 0.1f);
        //APCA();
        System.out.println("??");
    }
}


