package es.jaime.transactions;

public enum TransactionPropagationLevel {
    //If there is a transaction in progress, join that existing transaction, otherwise, create a new one.
    REQUIRED,
    //Always create new transaction. When this transaction is finished, resume previous transaction
    REQUIRES_NEW,
    //Join existing transaction, if no transaction is in progress, don't create a new one.
    SUPPORTS,
    //Run without transaction. When this commit/rollback is called, resume previous transaction
    NOT_SUPPORTED,
    //If there is a transaction in progress, join that existing transaction, otherwise, throw exception.
    MANDATORY,
    //If there is a transaction in progress, throw exception.
    NEVER,
}
