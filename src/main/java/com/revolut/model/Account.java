package com.revolut.model;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.errorprone.annotations.Immutable;

@Immutable
public class Account {

	public enum Type {
		CHEQUING, SAVINGS
	};

	@NotNull
	private Long id;

	@NotNull
	private Type type;

	private BigDecimal balance;

	@Valid
	@NotNull
	private String owner;

	public Account() {
	}

	public Account(Long id, Type type, BigDecimal balance, String owner) {
		super();
		this.id = id;
		this.type = type;
		this.balance = balance;
		this.owner = owner;
	}

	@JsonProperty
	public Long getId() {
		return id;
	}

	@JsonProperty
	public Type getType() {
		return type;
	}

	@JsonProperty
	public BigDecimal getBalance() {
		return balance;
	}

	@JsonProperty
	public String getOwner() {
		return owner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;

		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else {
			return id.equals(other.id);
		}
		return true;
	}

}
