package com.repuchello;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Pavel on 10/12/2016.
 */

class Message {
    public LocalDateTime dateTime;
    public String username;
    public String text;

    public Message(LocalDateTime dateTime, String username, String text) {
        this.dateTime = dateTime;
        this.username = username;
        this.text = text;
    }

    public Message(String logLine) {
        String[] parts = logLine.split("\\t", 3);
        dateTime = LocalDateTime.parse(parts[0]);
        username = parts[1];
        text = parts[2];
    }

    @Override
    public String toString() {
        return String.format("dt:%s\tnick:%s\tmsg:%s", dateTime, username, text);
    }

    public String toLogLine() {
        return String.format(
                "%s\t%s\t%s\n",
                dateTime.format(DateTimeFormatter.ISO_DATE_TIME),
                username,
                text);
    }
}
