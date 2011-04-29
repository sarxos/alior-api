package com.sarxos.aliorapi.receiver;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Receiver {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(Receiver.class.getSimpleName());

	protected static final int DELAY = 5000;

	protected static final int MAX_ATTEMPTS = 5;

	/**
	 * @param ctx
	 *            - search context
	 * @param by
	 *            - find by...
	 * @return Return elements list
	 */
	protected WebElement getElement(SearchContext ctx, By by) {

		LOG.debug("Getting element " + by);

		int attempts = 0;
		do {
			try {
				return ctx.findElement(by);
			} catch (NoSuchElementException e) {
				LOG.debug("Element by " + by + " has not been found. Trying one more time");
				waitForAMoment();
			}
		} while (attempts++ < MAX_ATTEMPTS);

		return null;
	}

	/**
	 * @param ctx
	 *            - search context
	 * @param by
	 *            - find by...
	 * @return Return element
	 */
	protected List<WebElement> getElements(SearchContext ctx, By by) {

		LOG.debug("Getting elements " + by);

		int attempts = 0;
		do {
			try {
				return ctx.findElements(by);
			} catch (NoSuchElementException e) {
				LOG.debug("Elements by " + by + " has not been found. Trying one more time");
				waitForAMoment();
			}
		} while (attempts++ < MAX_ATTEMPTS);

		throw new RuntimeException("Cannot find element " + by);
	}

	protected void click(WebElement element) {
		if (element == null) {
			throw new IllegalArgumentException("Web element to click cannot be null");
		}

		LOG.debug("Click element " + element.getTagName());

		try {
			element.click();
		} catch (ElementNotVisibleException e) {
			LOG.debug("Elements " + element.getTagName() + " is not visible");
		}
	}

	protected void waitForAMoment() {
		try {
			LOG.debug("Wait " + DELAY + "ms");
			Thread.sleep(DELAY);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
