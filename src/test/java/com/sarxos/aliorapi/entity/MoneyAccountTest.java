package com.sarxos.aliorapi.entity;

import junit.framework.TestCase;

import org.junit.Test;


public class MoneyAccountTest extends TestCase {

	@Test
	public void test_Create() {
		String nbr = "06249000050000400053847446";
		MoneyAccount ma = new MoneyAccount(nbr);
		assertEquals(ma.getNumber(), nbr);
	}

	@Test
	public void test_ValidNumber() {
		String nbr = "06249000050040400053847446";
		try {
			new MoneyAccount(nbr);
			assertTrue(false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

}
