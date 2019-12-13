package input;

import algorithm.DataItem;
import algorithm.DataStream;
import algorithm.MultiDataStream;

import java.io.*;
import java.util.*;

public class InputDataset {
    static String basedir = "E:\\WorkSpace\\GAMPS\\src\\dataset\\";


    public MultiDataStream getDataSet(MultiDataStream mds, String type) {
        String path = basedir + Type.getFileName(type);
        basedir = path;
        File file = new File(path);

        if (file.isDirectory()) {
            String[] list = file.list();
            System.out.printf("%s目录有%d个文件\n", type, list.length);
            mds = new MultiDataStream(list.length);
            int t=0;
            for (int j = 0; j < list.length; j++) {
                final double d = Math.random();
                final int i = (int)(d*100);
                //if(i<50){
                    readAndInsert(mds, list[j]);
                    t++;
                //}
            }
            mds.setNumOfStream(t);
        }
        return mds;
    }

    private void readAndInsert(MultiDataStream mds, String path) {
        try {
            DataStream t = readCsv(path);
            mds.addSingleStream(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DataStream readCsv(String path) throws Exception {
        System.out.println("读入" + path);
        path = basedir + path;
        File csv = new File(path); // CSV文件路径
        csv.setReadable(true);//设置可读
        csv.setWritable(true);//设置可写
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        String[] lineItems;
        ArrayList<DataItem> res = new ArrayList<>();
        int times = 1;
        // try {
        while ((line = br.readLine()) != null) // 读取到的内容给line变量
        {
            if (line.startsWith("#")) continue;
            lineItems = line.split(",");
            for (int i = 0; i < lineItems.length; i++) {
                if (lineItems[i].length() > 0 && lineItems[i].matches("^[+-]?\\d+(\\.\\d+)?$")) {
                    double td = Double.parseDouble(lineItems[i].trim());
                    DataItem t = new DataItem(td, times);
                    res.add(t);
                    times++;
                }
            }
        }
        System.out.println(path + "表格中所有行数：" + res.size());
        /*} catch (IOException e) {
            System.out.println(path + "有问题！");
            e.printStackTrace();
        }*/

        return new DataStream(res);
    }
}
