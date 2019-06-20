package com.revolut;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revolut.model.Account;

import io.dropwizard.Configuration;

public class RevolutConfiguration extends Configuration {
    
    @Valid
    @NotNull
    private List<Account> accounts = Collections.emptyList();

    
    @JsonProperty("accounts")
    public List<Account> getAccounts() {
		return Collections.synchronizedList(accounts);
	}
    
    @JsonProperty("accounts")
    public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
}
