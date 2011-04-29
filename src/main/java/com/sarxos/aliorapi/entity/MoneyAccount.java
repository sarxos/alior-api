package com.sarxos.aliorapi.entity;



public class MoneyAccount extends Account {

	private String role = null;

	private String name = null;

	private String currency = null;

	private double bookBalance = 0;

	private double availableBalance = 0;

	public MoneyAccount(String number) {
		super(number);
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the bookBalance
	 */
	public double getBookBalance() {
		return bookBalance;
	}

	/**
	 * @param bookBalance the bookBalance to set
	 */
	public void setBookBalance(double bookBalance) {
		this.bookBalance = bookBalance;
	}

	/**
	 * @return the availableBalance
	 */
	public double getAvailableBalance() {
		return availableBalance;
	}

	/**
	 * @param availableBalance the availableBalance to set
	 */
	public void setAvailableBalance(double availableBalance) {
		this.availableBalance = availableBalance;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append(getNumber()).append(" ");
		sb.append(NUMBER_FORMAT.format(availableBalance)).append(" ");
		sb.append(currency);

		return sb.toString();
	}
}
