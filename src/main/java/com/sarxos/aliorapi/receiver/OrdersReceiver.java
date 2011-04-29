package com.sarxos.aliorapi.receiver;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.aliorapi.AliorClientException;
import com.sarxos.aliorapi.AliorSelenium;
import com.sarxos.aliorapi.entity.BrokerAccount;
import com.sarxos.aliorapi.entity.BrokerOrder;

public class OrdersReceiver extends Receiver {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(OrdersReceiver.class.getSimpleName());

	/**
	 * Default used date format.
	 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

	private WebDriver driver = null;
	private AliorSelenium selenium = null;

	public OrdersReceiver(AliorSelenium selenium) {
		this.selenium = selenium;
		this.driver = selenium.getWrappedDriver();
	}

	public List<BrokerOrder> fetch(BrokerAccount account) throws AliorClientException {

		LOG.debug("Fetching orders for account " + account.getNumber());

		// go to the broker accounts link

		WebElement link = getElement(driver, By.cssSelector("li#m_Broker > a"));
		if (link == null) {
			throw new AliorClientException("Cannot find link to broker section");
		} else {
			click(link);
		}

		// switch account to desired one

		int max = 5;
		int attempts = 0;
		String number = null;
		do {

			WebElement select = getElement(driver, By.cssSelector("select#p_broker_acc"));
			String opt = account.getNumber().substring(21);

			List<WebElement> options = select.findElements(By.tagName("option"));
			for (WebElement option : options) {
				if (option.getText().equals(opt)) {
					option.setSelected();
					break;
				}
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}

			// go to account details to check if we are on the correct account
			// (due to race condition)

			WebElement details = getElement(driver, By.id("Broker.Account.AccountDetails"));
			click(details);

			int tries = 0;
			do {
				List<WebElement> trs = getElements(driver, By.cssSelector("div#szczegoly_umowy_body > table > tbody > tr"));
				if (trs.size() > 0) {
					WebElement tr = trs.get(0);
					List<WebElement> tds = getElements(tr, By.tagName("td"));
					if (tds.size() > 1) {
						WebElement td = tds.get(1);
						number = td.getText().replaceAll("\\s", "").trim();
						break;
					} else {
						throw new AliorClientException("Too few (" + tds.size() + ") account details tds");
					}
				} else {
					waitForAMoment();
				}
			} while (tries++ < max);

		} while (!account.getNumber().equals(number) && attempts++ < 5);

		// now we are sure we are in the correct account, so switch to orders
		// and fetch all orders for given account

		attempts = 0;
		do {
			link = getElement(driver, By.cssSelector("li#Broker\\.Orders > a"));
			if (link == null) {
				throw new AliorClientException("Cannot find link to orders section");
			} else {
				click(link);
			}
			waitForAMoment();
			By by = By.xpath("//h1[text()='Zlecenie']");
			try {
				driver.findElement(by);
				break;
			} catch (NoSuchElementException e) {
				LOG.debug("Element by " + by + " has not been found. Trying one more time");
			}

		} while (attempts++ < max);

		// go to the active orders tab

		attempts = 0;
		do {
			link = getElement(driver, By.id("Broker.Orders.ActiveOrdersList"));
			if (link == null) {
				throw new AliorClientException("Cannot find link to active orders section");
			} else {
				click(link);
			}
			waitForAMoment();
			By by = By.xpath("//h1[text()='Zlecenia bie¿¹ce']");
			try {
				driver.findElement(by);
				break;
			} catch (NoSuchElementException e) {
				LOG.debug("Element by " + by + " has not been found. Trying one more time");
			}

		} while (attempts++ < max);

		// now collect orders

		List<BrokerOrder> orders = new LinkedList<BrokerOrder>();

		WebElement div = getElement(driver, By.id("brokerOrderMonitorWipeArea"));
		div = getElement(div, By.id("grid-page-0"));
		List<WebElement> trs = getElements(div, By.cssSelector("div.dojoxGrid-row > table.dojoxGrid-row-table > tbody > tr"));

		for (int i = 0; i < trs.size(); i++) {

			LOG.debug("Getting order " + i);

			attempts = 0;
			do {
				selenium.getEval("onCibGridActionClick(\"DETAILS\", \"" + i + "\", undefined);");
				waitForAMoment();
				waitForAMoment();
				By by = By.xpath("//h1[text()='Szczegó³y zlecenia']");
				try {
					driver.findElement(by);
					break;
				} catch (NoSuchElementException e) {
					LOG.debug("Element by " + by + " has not been found. Trying one more time");
				}

			} while (attempts++ < max);

		}

		// for (WebElement tr : trs) {
		// tr.findElement(arg0)
		// }
		// driver.manage().ime().

		return Collections.EMPTY_LIST;
	}

	public static void main(String[] args) {
		System.out.println("\u0380\u0261");
	}
}
