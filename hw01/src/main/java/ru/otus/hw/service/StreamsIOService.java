package ru.otus.hw.service;

import ru.otus.hw.exceptions.AnswerReadException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class StreamsIOService implements IOService {
    private final PrintStream printStream;

    private final InputStream inputStream;

    public StreamsIOService(PrintStream printStream, InputStream inputStream) {

        this.printStream = printStream;
        this.inputStream = inputStream;
    }

    @Override
    public void printLine(String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        printStream.printf(s + "%n", args);
    }

    @Override
    public int inputNumber() {
        try {
            Scanner in = new Scanner(inputStream);
            return in.nextInt();
        } catch (Exception e) {
            throw new AnswerReadException("Error reading the response. The answer must be an integer", e);
        }
    }
}
