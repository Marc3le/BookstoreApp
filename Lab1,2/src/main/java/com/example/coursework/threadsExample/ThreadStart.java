package com.example.coursework.threadsExample;

public class ThreadStart {
    public static void main(String[] args) {
        SampleThread sampleThread = new SampleThread();
        Thread thread = new Thread(sampleThread);
        thread.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();

        System.out.println("Baigiu darba");
    }
}
