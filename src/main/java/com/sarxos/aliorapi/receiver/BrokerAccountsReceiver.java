package com.sarxos.aliorapi.receiver;

import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.aliorapi.AliorClient;
import com.sarxos.aliorapi.AliorClientException;
import com.sarxos.aliorapi.entity.BrokerAccount;

/**
 * Helper class designed to fetch list of all accounts.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class BrokerAccountsReceiver extends Receiver {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AliorClient.class.getSimpleName());

	/**
	 * @param driver
	 *            - web driver instance
	 * @return Return list of all accounts.
	 * @throws AliorClientException
	 *             when something is oops!
	 */
	public List<BrokerAccount> fetch(WebDriver driver) throws AliorClientException {

		LOG.debug("Fetching accounts");

		driver.navigate().to(AliorClient.INDEX_URL);

		// find broker accounts div
		WebElement accountsDiv = getElement(driver, By.id("brokerAccountsBoxDiv"));
		if (accountsDiv == null) {
			throw new AliorClientException("Cannot find broker accounts div");
		}

		// get list of table TRs
		List<WebElement> trs = getElements(accountsDiv, By.cssSelector("table.boxTbl.table-header > tbody > tr"));
		List<BrokerAccount> accounts = new LinkedList<BrokerAccount>();
		for (WebElement tr : trs) {
			accounts.add(convertToAccount(tr));
		}

		return accounts;
	}

	private BrokerAccount convertToAccount(WebElement tr) {

		BrokerAccount account = null;

		List<WebElement> tds = tr.findElements(By.tagName("td"));
		for (int i = 0; i < tds.size(); i++) {
			WebElement td = tds.get(i);
			switch (i) {
			case 0:
				String number = td.getText().replaceAll("\\s", "");
				account = new BrokerAccount(number);
				break;

			case 1:
				String type = td.getText().trim();
				account.setType(type);
				break;

			case 2:
				String secur = td.getText().replaceAll("\\s", "").replaceAll(",", ".");
				double securval = Double.parseDouble(secur);
				account.setSecuritiesEvaluation(securval);
				break;

			case 3:
				String cash = td.getText().replaceAll("\\s", "").replaceAll(",", ".");
				double cashval = Double.parseDouble(cash);
				account.setLiquidCash(cashval);
				break;

			}
		}

		return account;
	}
}
