package com.dlut.mnist.scriptrecognizer.DAO;


import android.util.Log;

import com.blankj.utilcode.util.TimeUtils;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataManager {
    private static final DataManager ourInstance = new DataManager();
    private List<DataBean> dataSheet;
    private String TAG = this.getClass().getSimpleName();
    private CSVParser parser;
    private List headerList = null;
    private int maxId;

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
        this.dataSheet = new ArrayList<>();
    }

    public int getDataCount() {

        return dataSheet.size();
    }

    public void readCsv(String filename) throws IOException {


        parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(true)
                .build();
        final CSVReader reader =
                new CSVReaderBuilder(new InputStreamReader(new FileInputStream(filename), Charset.forName("UTF-8")))
                        .withSkipLines(0)
                        .withCSVParser(parser)
                        .build();

        List<String[]> allRecords = reader.readAll();
        List<String> list = Arrays.asList(allRecords.remove(0));
        headerList = new ArrayList<>(list);
        List<String> dateList = headerList.subList(3, headerList.size());
        // dateList.addAll(Arrays.asList(headers));
        // dateList.remove(0);
        // dateList.remove(0);
        // dateList.remove(0);
        for (String[] records : allRecords) {
            DataBean dataBean = new DataBean();
            // List<String> recordList = Arrays.asList(records);
            // dataBean.setId(Integer.valueOf(recordList.remove(0)));
            // dataBean.setName(recordList.remove(0));
            // dataBean.setStunum(recordList.remove(0));
            // for (String record : recordList) {
            //     dataBean.addScore(headers[i], Integer.valueOf(record));
            //
            // }
            dataBean.setId(Integer.valueOf(records[0]));
            maxId = Math.max(maxId, Integer.valueOf(records[0]));
            dataBean.setName(records[1]);
            dataBean.setStunum(records[2]);
           for (int i = 0;i<dateList.size();i++)
           {
               dataBean.addScore(dateList.get(i),records[i+3]);
           }

            // for (int i = 3; i < records.length; i++) {
            //     dataBean.addScore(headers[i], records[i]);
            // }
            dataSheet.add(dataBean);
        }
        Log.d(TAG, "readCsv: read CSV file success!");
        reader.close();
    }

    public void writeCsv(String filename) throws IOException {
        if (headerList == null) {
            headerList = new ArrayList<>();
            headerList.add("Id");
            headerList.add("姓名");
            headerList.add("学号");
        }

        Writer writer = new FileWriter(filename);
        ICSVWriter csvWriter = new CSVWriterBuilder(writer)
                .withParser(parser)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build();
        String[] headers = (String[]) headerList.toArray(new String[0]);

        csvWriter.writeNext(headers);
        for (DataBean dataBean : dataSheet) {
            String[] dataLine = new String[headers.length];
            List<String> list = new ArrayList<>();
            list.add(dataBean.getIdString());
            list.add(dataBean.getName());
            list.add(dataBean.getStunum());
            List<String> dateList = headerList.subList(3, headerList.size());
            for (String date : dateList) {
                list.add(dataBean.getScoreByDate(date));
            }
            list.toArray(dataLine);

            csvWriter.writeNext(dataLine);
        }

        csvWriter.close();


    }

    public List<DataBean> getDataSheet() {
        return dataSheet;
    }

    public DataBean getDataByOrder(int i) {

        return dataSheet.get(i);
    }

    public void changeName(int positon, String string) {
        dataSheet.get(positon).setName(string);
    }

    public void changeStunum(int positon, String string) {
        dataSheet.get(positon).setStunum(string);
    }

    public void addNewByName(String name) {
        DataBean data = new DataBean();
        data.setName(name);
        data.setId(++maxId);
        dataSheet.add(data);

    }

    public void addNewByStunum(String stunum) {

        DataBean data = new DataBean();
        data.setStunum(stunum);
        data.setId(++maxId);

        dataSheet.add(data);
    }

    public void addScore(int positon, String string) {
        DataBean data = dataSheet.get(positon);
        String timeStamp = TimeUtils.getNowString(new SimpleDateFormat("yyyyMMdd"));
        Log.d(TAG, "addScore: "+timeStamp);
        data.addScore(timeStamp, string);
        if (!headerList.contains(timeStamp)) {
            headerList.add(timeStamp);
        }
    }

    public String getScore(int i) {
        DataBean data = dataSheet.get(i);
        String timeStamp = TimeUtils.getNowString(new SimpleDateFormat("yyyyMMdd"));
        Log.d(TAG, "addScore: "+timeStamp);

        return data.getScoreByDate(timeStamp);

    }
}
