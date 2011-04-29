package com.sarxos.aliorapi.entity;



public class BrokerAccount extends Account {

	/**
	 * Total securities evaluation
	 */
	private double securitiesEvaluation = 0;

	/**
	 * Liquid cash = cash + outstanding
	 */
	private double liquidCash = 0;

	public BrokerAccount(String number) {
		super(number);
	}

	/**
	 * @return the securitiesEvaluation
	 */
	public double getSecuritiesEvaluation() {
		return securitiesEvaluation;
	}

	/**
	 * @param securitiesEvaluation the securitiesEvaluation to set
	 */
	public void setSecuritiesEvaluation(double securitiesEvaluation) {
		this.securitiesEvaluation = securitiesEvaluation;
	}

	/**
	 * @return the cash
	 */
	public double getLiquidCash() {
		return liquidCash;
	}

	/**
	 * @param cash the cash to set
	 */
	public void setLiquidCash(double cash) {
		this.liquidCash = cash;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append(getNumber()).append(" ");
		sb.append(NUMBER_FORMAT.format(getSecuritiesEvaluation())).append(" + ");
		sb.append(NUMBER_FORMAT.format(getLiquidCash()));

		return sb.toString();
	}
}
