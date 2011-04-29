package com.sarxos.aliorapi.receiver;

import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.aliorapi.AliorClient;
import com.sarxos.aliorapi.AliorClientException;
import com.sarxos.aliorapi.entity.MoneyAccount;


/**
 * Helper class designed to fetch list of all accounts.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class MoneyAccountsReceiver {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MoneyAccountsReceiver.class.getSimpleName());

	/**
	 * @param driver - web driver instance
	 * @return Return list of all accounts.
	 * @throws AliorClientException when something is oops!
	 */
	public List<MoneyAccount> fetch(WebDriver driver) throws AliorClientException {

		driver.navigate().to(AliorClient.INDEX_URL);

		int max = 5;
		int attempts = 0;

		// go to the accounts page
		WebElement accountsLink = null;
		attempts = 0;
		do {
			try {
				accountsLink = driver.findElement(By.id("m_Accounts"));
				accountsLink = accountsLink.findElement(By.tagName("a"));
				accountsLink.click();
				break;
			} catch (NoSuchElementException e) {
				LOG.debug("Element m_Accounts has not been found. Trying one more time");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					LOG.error("Interrupt exception", e);
				}
			}
		} while (attempts++ < max);

		if (accountsLink == null) {
			throw new AliorClientException("Cannot find accounts link");
		}

		// find first dojox grid page
		// TODO: find other dojox grid pages

		WebElement table = null;
		attempts = 0;
		do {
			try {
				table = driver.findElement(By.id("grid-page-0"));
				break;
			} catch (NoSuchElementException e) {
				LOG.debug("Element grid-page-0 has not been found. Trying one more time");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					LOG.error("Interrupt exception", e);
				}
			}
		} while (attempts++ < max);

		if (table == null) {
			throw new AliorClientException("Cannot find grid table");
		}

		// parse all grid rows

		List<MoneyAccount> accounts = new LinkedList<MoneyAccount>();

		List<WebElement> rows = table.findElements(By.cssSelector("table.dojoxGrid-row-table"));
		for (WebElement row : rows) {
			List<WebElement> trs = row.findElements(By.tagName("tr"));
			for (WebElement tr : trs) {
				accounts.add(parseRowToAccount(tr));
			}
		}

		return accounts;
	}

	/**
	 * Change TR element to {@link MoneyAccount} instance
	 * 
	 * @param tr - TR to read
	 * @return Return new {@link MoneyAccount} object
	 * @throws AliorClientException
	 */
	private MoneyAccount parseRowToAccount(WebElement tr) throws AliorClientException {
		List<WebElement> tds = tr.findElements(By.tagName("td"));
		MoneyAccount account = null;
		for (int i = 0; i < tds.size(); i++) {
			WebElement td = tds.get(i);
			switch (i) {
				case 0:
					// on/off overnight
					break;

				case 1:
					// account number and role
					String[] num_role = td.getText().split("\n");
					if (num_role.length != 2) {
						throw new AliorClientException(
							"Account number/role pair length is " + num_role.length +
							" instead of 2");
					}
					String number = num_role[0].trim().replaceAll("\\s", "");
					String role = num_role[1].trim().split(":")[1];

					account = new MoneyAccount(number);
					account.setRole(role);
					break;

				case 2:
					String[] name_type = td.getText().split("\n");
					if (name_type.length != 2) {
						throw new AliorClientException(
							"Account name/type pair length is " + name_type.length +
							" instead of 2");
					}
					String name = name_type[0].trim();
					String type = name_type[1].trim();
					account.setName(name);
					account.setType(type);
					break;

				case 3:
					String currency = td.getText().trim();
					account.setCurrency(currency);
					break;

				case 4:
					String bookBalance = td.getText().replaceAll("\\s", "").replaceAll(",", ".");
					double bbal = Double.parseDouble(bookBalance);
					account.setBookBalance(bbal);
					break;

				case 5:
					String availableBalance = td.getText().replaceAll("\\s", "").replaceAll(",", ".");
					double abal = Double.parseDouble(availableBalance);
					account.setAvailableBalance(abal);
					break;
			}
		}

		return account;
	}
}
