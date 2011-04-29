package com.sarxos.aliorapi;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;

public class AliorSelenium extends WebDriverBackedSelenium {

	public AliorSelenium(WebDriver baseDriver, String baseUrl) {
		super(baseDriver, baseUrl);
	}

	public void windowMinimize() {
		commandProcessor.doCommand("windowMinimize", new String[] {});
	}
}
