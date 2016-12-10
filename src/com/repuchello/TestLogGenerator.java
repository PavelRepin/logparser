package com.repuchello;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Pavel on 10/12/2016.
 */
public class TestLogGenerator {
    private static LocalDateTime logTime = LocalDateTime.now();

    public static void createLog(String logPath, int messagesCount) throws IOException {
        String[] names = {"Repa", "Pit", "Gash", "Yuri"};
        Random rng = new Random();

        PrintWriter out = new PrintWriter(logPath);
        for (int i = 0; i < messagesCount; i++) {
            logTime = logTime.plusSeconds(rng.nextInt(60));
            Message m = new Message(logTime, names[rng.nextInt(names.length)], UUID.randomUUID().toString());
            out.write(m.toLogLine());
        }
        out.flush();
    }

    public static void createLogs(String logDirPath, int filesCount, int messagesPerFile) throws IOException {
        for (int i = 0; i < filesCount; i++) {
            createLog(String.format("%s/chat.%d.log", logDirPath, i), messagesPerFile);
        }
    }
}
