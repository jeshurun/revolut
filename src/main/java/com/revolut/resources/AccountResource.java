package com.revolut.resources;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.revolut.model.Account;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {
	private List<Account> accounts;

    public AccountResource(List<Account> accounts) {
    	this.accounts = accounts;
    }

    @GET
    public List<Account> getAccounts(@QueryParam("user") final Optional<String> user) {
    	if (!user.isPresent()) {
    		return Collections.emptyList();
		}
		return accounts.stream()
					   .filter(a -> a.getOwner().equals(user.get()))
					   .collect(Collectors.toList());
    }
}