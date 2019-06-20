package com.revolut.model;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;

public class TestAccount {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void serializesToJSON() throws Exception {
		final Account account = new Account(1L, Account.Type.CHEQUING, new BigDecimal("12345"), "Dave");
		final String expected = MAPPER
				.writeValueAsString(MAPPER.readValue(fixture("fixtures/account.json"), Account.class));
		assertThat(MAPPER.writeValueAsString(account)).isEqualTo(expected);
	}

	@Test
	public void deserializesFromJSON() throws Exception {
		final Account account = new Account(1L, Account.Type.CHEQUING, new BigDecimal("12345"), "Dave");
		assertThat(MAPPER.readValue(fixture("fixtures/account.json"), Account.class)).isEqualTo(account);
	}

}
