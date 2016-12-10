package com.repuchello;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Pavel on 10/12/2016.
 */
public class LogParser {
    private String inDirPath;
    private String outPath;
    private Filter filter;
    private GroupBy groupBy;
    private Integer threadsMaxCount = 1;

    public LogParser(
            String inDirPath,
            String outPath,
            Filter filter,
            GroupBy groupBy,
            Integer threadsMaxCount) {
        this.inDirPath = inDirPath;
        this.outPath = outPath;
        this.filter = filter;
        this.groupBy = groupBy;
        if (threadsMaxCount != null) {
            this.threadsMaxCount = threadsMaxCount;
        }
    }

    private static Stream<String> lines(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            System.err.println(e);
        }

        return Stream.of(new String[0]);
    }

    public void parse() {
        try {
            PrintWriter out = new PrintWriter(outPath);
            Files.walk(Paths.get(inDirPath), 1)
                    .parallel()
                    .filter(Files::isRegularFile)
                    .flatMap(LogParser::lines)
                    .map(line -> new Message(line))
                    .filter(filter::match)
                    .peek(out::println)
                    .collect(
                            Collectors.groupingBy(groupBy::getMessageGroupByKey, Collectors.counting())
                    ).forEach((key, count) -> System.out.printf("key:%s\tcount:%d\n", key, count)
            );
            out.flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    static class Filter {
        public String username;
        public Pattern messagePattern;
        public LocalDateTime since;
        public LocalDateTime until;

        public boolean match(Message m) {
            if (since != null && until != null && (since.isAfter(m.dateTime) || until.isBefore(m.dateTime))) {
                return false;
            }

            if (username != null && !username.equals(m.username)) {
                return false;
            }

            if (messagePattern != null && !messagePattern.matcher(m.text).matches()) {
                return false;
            }

            return true;
        }
    }

    static class GroupBy {
        public boolean username = false;
        public String unit; // hour, day, month

        public String getMessageGroupByKey(Message m) {
            int unitValue = -1;
            switch ((unit == null) ? "" : unit.toLowerCase()) {
                case "hour": unitValue = m.dateTime.getHour(); break;
                case "day": unitValue = m.dateTime.getDayOfMonth(); break;
                case "month": unitValue = m.dateTime.getMonth().getValue(); break;
            }


            if (username && unitValue != -1) {
                return String.format("%s-%s-%d", m.username, unit, unitValue);
            }

            if (username) {
                return m.username;
            }

            if (unitValue != -1) {
                return String.format("%s-%d", unit, unitValue);
            }

            return "no-grouping-key";
        }
    }
}
