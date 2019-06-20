package com.revolut.resources;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.revolut.model.Account;

@Path("/transfer/{from}/{to}/{amount}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransferResource {
	private Map<Long, Account> accounts;

    public TransferResource(List<Account> accounts) {
    	this.accounts = accounts.stream()
    							.collect(Collectors.toMap(Account::getId,
    									account -> account, 
    									(account1, account2) -> account1, 
    									ConcurrentHashMap::new));
    }

    @POST
	public Response transferFunds(@PathParam("from") @NotNull final Long from,
									 @PathParam("to") @NotNull  final Long to, 
									 @PathParam("amount") @NotNull final BigDecimal amount) {
    	if (amount.signum() <= 0 || from.equals(to)) {
    		return Response.status(Status.BAD_REQUEST).entity("Invalid request parameters.").build();
    	}
    	
    	Account fromAccount = accounts.get(from);    	
    	Account toAccount = accounts.get(to);
    	
    	if (fromAccount == null || toAccount == null) {
    		return Response.status(Status.NOT_FOUND).entity("Invalid account.").build();
    	}
    	
		if (fromAccount.getBalance().compareTo(amount) < 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(String.format(
							"I'm sorry %s, I'm afraid I can't do that. Account %d has insufficient funds to complete this transfer.",
							fromAccount.getOwner(), fromAccount.getId()))
					.build();
		}
    	
		fromAccount = accounts.compute(from, (id, account) -> account == null ? null : debitAccountFunction.apply(amount).apply(id, account));
		toAccount = accounts.compute(to, (id, account) -> account == null ? null : creditAccountFunction.apply(amount).apply(id, account));
    	
    	if (fromAccount == null || toAccount == null) {
    		return Response.status(Status.NOT_FOUND).entity("Invalid account.").build();
    	}
		
		return Response.ok(Arrays.asList(fromAccount, toAccount)).build();
    }

	private static Function<BigDecimal, BiFunction<Long, Account, Account>> debitAccountFunction = amount -> (id, account) -> {
		BigDecimal updatedBalance = account.getBalance().subtract(amount);
		return new Account(account.getId(), account.getType(), updatedBalance, account.getOwner());
	};
	
	private static Function<BigDecimal, BiFunction<Long, Account, Account>> creditAccountFunction = amount -> (id, account) -> {
		BigDecimal updatedBalance = account.getBalance().add(amount);
		return new Account(account.getId(), account.getType(), updatedBalance, account.getOwner());
	};
}