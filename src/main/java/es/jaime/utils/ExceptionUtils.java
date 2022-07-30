package es.jaime.utils;

public final class ExceptionUtils {
    public static void runChecked(CheckedRunnable runnable) {
        try {
            runnable.run();
        }catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e.getMessage());
        }
    }
}
