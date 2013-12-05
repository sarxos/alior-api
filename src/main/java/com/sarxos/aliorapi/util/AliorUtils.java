package com.sarxos.aliorapi.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.aliorapi.AliorClientException;


public class AliorUtils {

	private static final Logger LOG = LoggerFactory.getLogger(AliorUtils.class);

	public static final void waitForDialogDisappear(WebDriver driver, int seconds) throws AliorClientException {

		long t = System.currentTimeMillis();

		int top = -1;

		WebElement loadingDialog = driver.findElement(By.id("loadingDialog"));
		do {

			String css = loadingDialog.getCssValue("top");
			try {
				top = Integer.valueOf(css.replaceAll("px", ""));
			} catch (NumberFormatException e) {
				throw new RuntimeException("Invalid CSS top value: " + css, e);
			}

			LOG.debug("Wait for loading dialog disappear (top: {})", top);

			if (System.currentTimeMillis() - t > seconds * 1000) {
				throw new AliorClientException("Loading spinner still visible");
			}

			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

		} while (top > 0);

		LOG.debug("Loading dialog disappeared");

	}
}
