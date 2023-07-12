package es.jaime.connection.transactions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DatabaseTransacionExecutor {
    private final DatabaseTransactionManager transactionManager;

    public void execute(ExceptionHandlingMethod exceptionHandlingMethod, Runnable runnable) {
        try{
            transactionManager.start();
            runnable.run();
            transactionManager.commit();
        }catch (Exception e) {
            transactionManager.rollback();

            handleException(exceptionHandlingMethod, e);
        }
    }

    private static void handleException(ExceptionHandlingMethod exceptionHandlingMethod, Exception e) {
        switch (exceptionHandlingMethod) {
            case ONLY_PRINT:
                e.printStackTrace();
                break;
            case ONLY_RETHROW:
                throw new RuntimeException(e.getMessage());
            case RETHROW_AND_PRINT:
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
        }
    }

    public enum ExceptionHandlingMethod {
        IGNORE, ONLY_PRINT, ONLY_RETHROW, RETHROW_AND_PRINT
    }
}
