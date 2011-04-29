package com.sarxos.aliorapi;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.aliorapi.entity.BrokerAccount;
import com.sarxos.aliorapi.entity.BrokerOrder;
import com.sarxos.aliorapi.entity.BrokerPaper;
import com.sarxos.aliorapi.entity.MoneyAccount;
import com.sarxos.aliorapi.receiver.BrokerAccountsReceiver;
import com.sarxos.aliorapi.receiver.MoneyAccountsReceiver;
import com.sarxos.aliorapi.receiver.OrdersReceiver;
import com.sarxos.aliorapi.receiver.PapersReceiver;

/**
 * Main Alior API class.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class AliorClient {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AliorClient.class.getSimpleName());

	/**
	 * Alior login URL address
	 */
	public static final String LOGIN_URL = "https://aliorbank.pl/hades/do/Login";

	/**
	 * Alior auth URL address
	 */
	public static final String AUTH_URL = "https://aliorbank.pl/hades/do/Authorization";

	/**
	 * Main account page
	 */
	public static final String INDEX_URL = "https://aliorbank.pl/retail/index.do";

	/**
	 * Logout URL
	 */
	public static final String LOGOUT_URL = "https://aliorbank.pl/retail/vibankSSOLogout.do";

	/**
	 * Web driver used to control session.
	 */
	private WebDriver driver = null;

	/**
	 * Selenium created from web driver.
	 */
	private AliorSelenium selenium = null;

	/**
	 * Is user logged in.
	 */
	private boolean loggedIn = false;

	public AliorClient(Class<? extends WebDriver> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Web driver class for Alior client cannot be null!");
		}
		try {
			driver = clazz.newInstance();
			if (driver instanceof HtmlUnitDriver) {
				((HtmlUnitDriver) driver).setJavascriptEnabled(true);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return;
		}

		selenium = new AliorSelenium(driver, LOGIN_URL);
	}

	/**
	 * Login to the Alior profile.
	 * 
	 * @param uid
	 *            - user ID
	 * @param passwd
	 *            - user password
	 * @return true if user has been logged in, false otherwise
	 * @throws AliorClientException
	 *             when client is already logged in
	 */
	public boolean login(String uid, String passwd) throws AliorClientException {

		if (loggedIn) {
			throw new AliorClientException("User is already logged in");
		}

		driver.get(LOGIN_URL);

		// find field for the client number
		WebElement clientNumber = driver.findElement(By.id("inputContent"));
		clientNumber.sendKeys(uid);
		clientNumber.submit();

		// escape from the frame and go to the authentication page
		driver.navigate().to(AUTH_URL);

		// fill required password chars
		char[] pwd = passwd.toCharArray();
		for (int i = 0; i < pwd.length; i++) {
			WebElement box = null;
			try {
				box = driver.findElement(By.id("PASSFIELD" + (i + 1)));
				if (box != null) {
					if (!"true".equals(box.getAttribute("readonly"))) {
						box.sendKeys(Character.toString(pwd[i]));
					}
				}
			} catch (NoSuchElementException e) {
				// do nothing
			}
		}

		driver.findElement(By.id("submitButton")).click();

		// check whether or not logout link can be found, if it can be found
		// that means we have successfully logged in

		WebElement logoutLink = null;

		int max = 5;
		int attempts = 0;
		do {
			try {
				logoutLink = driver.findElement(By.linkText("Wyloguj"));
				break;
			} catch (NoSuchElementException e) {
				LOG.debug("Logout link has not been found. Trying one more time");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					LOG.error("Interrupt exception", e);
				}
			}
		} while (attempts++ < max);

		if (logoutLink != null) {
			loggedIn = true;
		}

		if (LOG.isDebugEnabled()) {
			if (loggedIn) {
				LOG.info("Successfully logged to " + uid + " Alior profile");
			} else {
				LOG.error("Unable to login to " + uid + " Alior profile");
			}
		}

		return loggedIn;
	}

	/**
	 * Logout client.
	 * 
	 * @return true
	 * @throws AliorClientException
	 *             when client is not logged in
	 */
	public boolean logout() throws AliorClientException {
		if (!loggedIn) {
			throw new AliorClientException("User is not logged in");
		}
		driver.navigate().to(LOGOUT_URL);
		return true;
	}

	/**
	 * @return the driver
	 */
	public WebDriver getDriver() {
		return driver;
	}

	public void close() {
		driver.close();
	}

	/**
	 * @return true if client is logged in, false otherwise
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * Check if client is logged in. Throw exception if not.
	 * 
	 * @throws AliorClientException
	 *             if client is not logged in
	 */
	private void checkLogin() throws AliorClientException {
		if (!loggedIn) {
			throw new AliorClientException("User is not logged in!");
		}
	}

	/**
	 * Get all money accounts from Alior bank profile.
	 * 
	 * @return List of all money accounts within Alior profile
	 * @throws AliorClientException
	 *             oops!
	 */
	public List<MoneyAccount> getMoneyAccounts() throws AliorClientException {
		checkLogin();
		return new MoneyAccountsReceiver().fetch(driver);
	}

	/**
	 * Get all broker accounts from Alior bank profile.
	 * 
	 * @return List of all broker account within Alior profile
	 * @throws AliorClientException
	 *             oops!
	 */
	public List<BrokerAccount> getBrokerAccounts() throws AliorClientException {
		checkLogin();
		return new BrokerAccountsReceiver().fetch(driver);
	}

	/**
	 * Get list of securities on given broker account.
	 * 
	 * @param account
	 *            - broker account to check
	 * @return List of papers on the broker account
	 * @throws AliorClientException
	 */
	public List<BrokerPaper> getSecurities(BrokerAccount account) throws AliorClientException {
		checkLogin();
		return new PapersReceiver(account).fetch(driver);
	}

	/**
	 * Return all orders from broker account.
	 * 
	 * @param account
	 *            - broker account
	 * @return List of all broker orders from given account
	 * @throws AliorClientException
	 *             oops!
	 */
	public List<BrokerOrder> getOrders(BrokerAccount account) throws AliorClientException {
		checkLogin();
		return new OrdersReceiver(selenium).fetch(account);
	}
}
