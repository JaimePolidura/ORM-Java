package es.jaime.connection.pool.shared;

import es.jaime.connection.pool.ConnectionPoolEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static es.jaime.connection.pool.ConnectionPoolEntry.*;

public final class ConcurrentFreeConnectionList {
    private final AtomicReference<AtomicReference<ConnectionPoolEntry>[]> connections;
    private final AtomicLong arrayModificationCounter;
    private final AtomicInteger rebalancingCounter;
    private final AtomicBoolean rebalancingLock;

    public ConcurrentFreeConnectionList(int size) {
        this.rebalancingLock = new AtomicBoolean(false);
        this.connections = new AtomicReference<>(new AtomicReference[size]);
        this.rebalancingCounter = new AtomicInteger(0);
        this.arrayModificationCounter = new AtomicLong(0L);

        this.fillConnectionsWithEmptyReferences(connections.get(), 0);
    }

    @Nullable
    public ConnectionPoolEntry get() {
        AtomicReference<ConnectionPoolEntry>[] connectionsArray = connections.get();

        int currentPosition = (int) Thread.currentThread().getId();
        int maxNumberIterations = connectionsArray.length - 1;
        int actualNumberIteration = 1;

        while (actualNumberIteration < maxNumberIterations) {
            AtomicReference<ConnectionPoolEntry> actualEntry = connectionsArray[currentPosition];
            ConnectionPoolEntry connectionEntry = actualEntry.get();

            if(connectionEntry != null && actualEntry.get().compareAndSetState(CONNECTION_NOT_IN_USE, CONNECTION_IN_USE)){
                return connectionEntry;
            }

            currentPosition = currentPosition + 1 < connectionsArray.length ? ++currentPosition : 0;
            actualNumberIteration++;
        }

        return null;
    }

    public void add(ConnectionPoolEntry connection) {
        int currentThreadId = (int) Thread.currentThread().getId();

        if(currentThreadId > connections.get().length){
            rebalanceArray(rebalancingCounter.get());
        }

        ConnectionPoolEntry oldValue = connections.get()[currentThreadId].get();
        if(oldValue != null){
            oldValue.recreate(connection);
        }else{
            arrayModificationCounter.getAndIncrement();
            connections.get()[currentThreadId].set(connection);
        }
    }

    public void remove(ConnectionPoolEntry connectionPoolEntry) {
        long currentThreadId = connectionPoolEntry.getCreatedByThreadId();

        connections.get()[(int) currentThreadId].get().close();
    }

    public void removeAll() {
        for (AtomicReference<ConnectionPoolEntry> connectionPoolEntryAtomicReference : this.connections.get()) {
            connectionPoolEntryAtomicReference.get().close();
        }
    }

    private void rebalanceArray(int oldRebalancingCounter) {
        if(tryAcquireRebalancingLock()){
            if(oldRebalancingCounter != rebalancingCounter.get()){
                return;
            }

            AtomicReference<ConnectionPoolEntry>[] connectionsToRebalance = null;
            long oldArrayModificantionCounter = 0;

            do {
                oldArrayModificantionCounter = arrayModificationCounter.get();
                connectionsToRebalance = connections.get();

                AtomicReference<ConnectionPoolEntry>[] connectionsRebalanced = Arrays.copyOf(connectionsToRebalance, connectionsToRebalance.length * 2);
                fillConnectionsWithEmptyReferences(connectionsRebalanced, connectionsToRebalance.length);
            }while (oldArrayModificantionCounter != arrayModificationCounter.get());

            this.rebalancingCounter.incrementAndGet();
            this.connections.set(connectionsToRebalance);

            releaseRebalancingLock();
        }
    }

    private boolean tryAcquireRebalancingLock() {
        return rebalancingLock.compareAndSet(false, true);
    }

    private void releaseRebalancingLock() {
        rebalancingLock.set(false);
    }

    private void fillConnectionsWithEmptyReferences(AtomicReference<ConnectionPoolEntry>[] array, int start) {
        for (int i = start; i < array.length; i++) {
            array[i] = new AtomicReference<>(null);
        }
    }
}
