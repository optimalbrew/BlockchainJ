package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 27/11/2018.
 */
public abstract class AbstractExecutionContext implements ExecutionContext {
    private final Map<Address, AccountState> accountStates = new HashMap<>();

    @Override
    public void transfer(Address senderAddress, Address receiverAddress, BigInteger amount) {
        AccountState sender = this.getAccountState(senderAddress);
        AccountState receiver = this.getAccountState(receiverAddress);

        sender.subtractFromBalance(amount);
        receiver.addToBalance(amount);
    }

    @Override
    public void incrementNonce(Address address) {
        AccountState accountState = this.getAccountState(address);

        accountState.incrementNonce();
    }

    @Override
    public BigInteger getBalance(Address address) {
        return this.getAccountState(address).getBalance();
    }

    @Override
    public long getNonce(Address address) {
        return this.getAccountState(address).getNonce();
    }

    @Override
    public void commit() {
        for (Map.Entry<Address, AccountState> entry : this.accountStates.entrySet()) {
            Address address = entry.getKey();
            AccountState accountState = entry.getValue();

            if (!accountState.wasChanged())
                continue;

            this.updateAccountState(address, accountState);
        }

        this.accountStates.clear();
    }

    @Override
    public void rollback() {
        this.accountStates.clear();
    }

    @Override
    public AccountState getAccountState(Address address) {
        if (this.accountStates.containsKey(address))
            return this.accountStates.get(address);

        AccountState accountState = this.retrieveAccountState(address);

        this.putAccountState(address, accountState);

        return accountState;
    }

    @Override
    public void putAccountState(Address address, AccountState accountState) {
        this.accountStates.put(address, accountState);
    }

    abstract AccountState retrieveAccountState(Address address);

    abstract void updateAccountState(Address address, AccountState accountState);
}
