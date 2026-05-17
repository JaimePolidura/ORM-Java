package es.jaime.transactions;

import es.jaime.connection.pool.ConnectionPool;
import es.jaime.javaddd.domain.exceptions.IllegalState;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;

@AllArgsConstructor
public class TransactionManager {
    private final ThreadLocal<Stack<LiveTransaction>> liveThreadTransactions = ThreadLocal.withInitial(Stack::new);
    private final ThreadLocal<Boolean> rollbackAllTransactionsFlag = ThreadLocal.withInitial(() -> false);
    private final ConnectionPool connectionPool;

    public void startTransaction(TransactionPropagationLevel propagationLevel) throws SQLException {
        switch (propagationLevel) {
            case REQUIRED -> {
                if (realTransactionInProgress()) {
                    joinPrevTransaction(propagationLevel);
                } else {
                    createNewTransaction(propagationLevel);
                }
            }
            case REQUIRES_NEW -> createNewTransaction(propagationLevel);
            case SUPPORTS -> {
                if (realTransactionInProgress()) {
                    joinPrevTransaction(propagationLevel);
                } else {
                    createFakeTransaction(propagationLevel);
                }
            }
            case NOT_SUPPORTED -> createFakeTransaction(propagationLevel);
            case MANDATORY -> {
                if (realTransactionInProgress()) {
                    joinPrevTransaction(propagationLevel);
                } else {
                    throw new IllegalState("Cannot create transaction with propagation level MANDATORY, and no real "
                            + "transaction has been created");
                }
            }
            case NEVER -> {
                if (realTransactionInProgress()) {
                    throw new IllegalState("Cannot create transaction with propagation level NEVER and a "
                            + "transaction has been created");
                }
            }
        }
    }

    public void rollbackTransaction() throws SQLException {
        if (noTransactionInProgress()) {
            throw new IllegalState("Cannot rollback transaction, when no transaction has been created.");
        }

        LiveTransaction transactionToRollback = liveThreadTransactions.get().pop();
        switch (transactionToRollback.propagationLevel) {
            case REQUIRES_NEW, REQUIRED, SUPPORTS, MANDATORY -> rollbackTransaction(transactionToRollback);
            case NEVER, NOT_SUPPORTED -> {}
        }

        connectionPool.release(transactionToRollback.connection());
    }

    public void commitTransaction() throws SQLException {
        if (noTransactionInProgress()) {
            throw new IllegalState("Cannot commit transaction, when no transaction has been created.");
        }

        LiveTransaction transactionToRollback = liveThreadTransactions.get().pop();
        switch (transactionToRollback.propagationLevel) {
            case REQUIRES_NEW, REQUIRED, SUPPORTS, MANDATORY -> commitTransaction(transactionToRollback);
            case NEVER, NOT_SUPPORTED -> {}
        }

        connectionPool.release(transactionToRollback.connection());
    }

    private void commitTransaction(LiveTransaction transaction) throws SQLException {
        if (transaction.isRealTransaction() && !transaction.isJoiningTransaction() && !rollbackAllTransactionsFlag.get()) {
            transaction.connection().commit();
        } else if (transaction.isRealTransaction() && !transaction.isJoiningTransaction() && rollbackAllTransactionsFlag.get()) {
            rollbackTransaction(transaction);
        }
    }

    private void rollbackTransaction(LiveTransaction transaction) throws SQLException {
        if (transaction.isRealTransaction() && !transaction.isJoiningTransaction()) {
            transaction.connection().rollback();
            rollbackAllTransactionsFlag.set(Boolean.FALSE);
        } else {
            rollbackAllTransactionsFlag.set(Boolean.TRUE);
        }
    }

    public Connection getCurrentConnection() {
        if (noTransactionInProgress()) {
            throw new IllegalState("Cannot execute sql when no transaction has been started");
        }

        return liveThreadTransactions.get().peek().connection();
    }

    private boolean realTransactionInProgress() {
        Stack<LiveTransaction> currentTransactionsInProgress = liveThreadTransactions.get();
        return !currentTransactionsInProgress.isEmpty() && currentTransactionsInProgress.peek().isRealTransaction();
    }

    private void createFakeTransaction(TransactionPropagationLevel propagationLevel) throws SQLException {
        Stack<LiveTransaction> currentTransactionsInProgress = liveThreadTransactions.get();
        Connection newConnection = connectionPool.acquire();
        newConnection.setAutoCommit(true);
        currentTransactionsInProgress.push(new LiveTransaction(propagationLevel, newConnection, false, false));
    }

    private void createNewTransaction(TransactionPropagationLevel propagationLevel) throws SQLException {
        Stack<LiveTransaction> currentTransactionsInProgress = liveThreadTransactions.get();
        Connection newConnection = connectionPool.acquire();
        newConnection.setAutoCommit(false);
        currentTransactionsInProgress.push(new LiveTransaction(propagationLevel, newConnection, true, false));
    }

    private void joinPrevTransaction(TransactionPropagationLevel propagationLevel) {
        Stack<LiveTransaction> currentTransactionsInProgress = liveThreadTransactions.get();
        LiveTransaction lastTransaction = currentTransactionsInProgress.peek();
        currentTransactionsInProgress.add(new LiveTransaction(propagationLevel, lastTransaction.connection,
                lastTransaction.isRealTransaction(), true));
    }


    private boolean noTransactionInProgress() {
        return liveThreadTransactions.get().isEmpty();
    }

    private record LiveTransaction(TransactionPropagationLevel propagationLevel,
                                   Connection connection,
                                   boolean isRealTransaction,
                                   boolean isJoiningTransaction) {
    }
}
