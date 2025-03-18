package com.example.coursework.threadsExample;

public class SampleThread implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i + " VEIKSMAS");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                return;
            }
        }

    }
}
