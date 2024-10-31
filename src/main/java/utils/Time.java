package utils;

public class Time {
    // this will be initialized at startup so it will consistently store the time the app started
    public static float startTime = System.nanoTime();

    // returns the time passed from starting the app in nanoseconds
    public static float getTime() {
        return (float)((System.nanoTime() - startTime) * 1E-9);
    }
}
