package es.jaime.utils;

@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
