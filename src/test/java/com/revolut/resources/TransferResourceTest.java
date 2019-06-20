package com.revolut.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import com.revolut.model.Account;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class TransferResourceTest {

    private static final List<Account> accounts = new ArrayList<>();
    static {
        accounts.add(new Account(1L, Account.Type.CHEQUING, new BigDecimal("12345"), "Dave"));
        accounts.add(new Account(3L, Account.Type.SAVINGS, new BigDecimal("65234"), "Dave"));
    }

    @ClassRule public static final ResourceTestRule resources =
        ResourceTestRule.builder().addResource(new TransferResource(accounts)).build();

    @Test
    public void testTransferSufficientBalance() {
        final Response response = resources.target("/transfer/1/3/100").request().accept(MediaType.APPLICATION_JSON).post(null);
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        List<Account> updatedAccounts = response.readEntity(new GenericType<List<Account>>() {});
        assertEquals(2, updatedAccounts.size());
        assertEquals(new BigDecimal("12245"), updatedAccounts.get(0).getBalance());
        assertEquals(new BigDecimal("65334"), updatedAccounts.get(1).getBalance());
    }
    
    @Test
    public void testTransferPrecision() {
        final Response response = resources.target("/transfer/1/3/100.01").request().accept(MediaType.APPLICATION_JSON).post(null);
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        List<Account> updatedAccounts = response.readEntity(new GenericType<List<Account>>() {});
        assertEquals(2, updatedAccounts.size());
        assertEquals(new BigDecimal("12144.99"), updatedAccounts.get(0).getBalance());
        assertEquals(new BigDecimal("65434.01"), updatedAccounts.get(1).getBalance());
    }

    @Test
    public void testTransferInSufficientBalance() {
        final Response response = resources.target("/transfer/1/3/12346").request().accept(MediaType.APPLICATION_JSON).post(null);
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        assertEquals(new BigDecimal("12345"), accounts.get(0).getBalance());
    }
    
    @Test
    public void testTransferInvalidAmount() {
        final Response response = resources.target("/transfer/1/3/-5000").request().accept(MediaType.APPLICATION_JSON).post(null);
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
    }
    
    @Test
    public void testTransferNonExistantFromAccount() {
        final Response response = resources.target("/transfer/2/3/12344").request().accept(MediaType.APPLICATION_JSON).post(null);
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
    }
    
    @Test
    public void testTransferNonExistantToAccount() {
        final Response response = resources.target("/transfer/1/2/12344").request().accept(MediaType.APPLICATION_JSON).post(null);
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
    }
    
    @Test
    public void testTransferSameAccounts() {
        final Response response = resources.target("/transfer/1/1/12344").request().accept(MediaType.APPLICATION_JSON).post(null);
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
    }
    
}
