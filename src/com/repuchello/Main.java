package com.repuchello;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String inDirPath = "/Users/user/Downloads/logs";
        String outPath = "/Users/user/Downloads/totalLog.txt";

        try {
            TestLogGenerator.createLogs(inDirPath, 200, 1000);
        } catch (IOException e) {
            System.err.println(e);
            return;
        }

        LogParser.Filter filter = new LogParser.Filter();
        //filter.username = "Pit";
        //filter.messagePattern = Pattern.compile(".*-4298.*");
        //filter.since = LocalDateTime.parse("2016-12-09T18:03:35.000");
        //filter.until = LocalDateTime.parse("2016-12-09T18:25:45.000");

        LogParser.GroupBy groupBy = new LogParser.GroupBy();
        groupBy.username = true;
        groupBy.unit = "hour";
        LogParser parser = new LogParser(inDirPath, outPath, filter, groupBy, null);
        parser.parse();
    }
}
