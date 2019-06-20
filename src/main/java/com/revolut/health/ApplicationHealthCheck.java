package com.revolut.health;

import java.util.List;

import com.codahale.metrics.health.HealthCheck;
import com.revolut.model.Account;

public class ApplicationHealthCheck extends HealthCheck {
    private final List<Account> accounts;

    public ApplicationHealthCheck(List<Account> users) {
        this.accounts = users;
    }

    @Override
    protected Result check() throws Exception {
        if (accounts.isEmpty()) {
            return Result.unhealthy("No Accounts Found!");
        }
        return Result.healthy();
    }
}