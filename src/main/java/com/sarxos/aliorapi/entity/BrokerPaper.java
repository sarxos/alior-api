package com.sarxos.aliorapi.entity;

import java.util.Date;


public class BrokerPaper {

	private String name = null;
	private int availableQuantity = 0;
	private int lockedQuantity = 0;
	private int otherLockedQuantity = 0;
	private int kdwp = 0;
	private double price = 0;
	private Date date = null;
	private double evaluation = 0;

	public BrokerPaper(String name) {
		super();
		this.name = name;
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
	 * @return the availableQuantity
	 */
	public int getAvailableQuantity() {
		return availableQuantity;
	}

	/**
	 * @param availableQuantity the availableQuantity to set
	 */
	public void setAvailableQuantity(int availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	/**
	 * @return the lockedQuantity
	 */
	public int getLockedQuantity() {
		return lockedQuantity;
	}

	/**
	 * @param lockedQuantity the lockedQuantity to set
	 */
	public void setLockedQuantity(int lockedQuantity) {
		this.lockedQuantity = lockedQuantity;
	}

	/**
	 * @return the otherLockedQuantity
	 */
	public int getOtherLockedQuantity() {
		return otherLockedQuantity;
	}

	/**
	 * @param otherLockedQuantity the otherLockedQuantity to set
	 */
	public void setOtherLockedQuantity(int otherLockedQuantity) {
		this.otherLockedQuantity = otherLockedQuantity;
	}

	/**
	 * @return the kdwp
	 */
	public int getKdwp() {
		return kdwp;
	}

	/**
	 * @param kdwp the kdwp to set
	 */
	public void setKDWP(int kdwp) {
		this.kdwp = kdwp;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the evaluation
	 */
	public double getEvaluation() {
		return evaluation;
	}

	/**
	 * @param evaluation the evaluation to set
	 */
	public void setEvaluation(double evaluation) {
		this.evaluation = evaluation;
	}

	@Override
	public String toString() {
		int q = availableQuantity + lockedQuantity + otherLockedQuantity;
		return name + " " + q + " " + Account.NUMBER_FORMAT.format(evaluation);
	}
}
