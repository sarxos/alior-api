package com.sarxos.aliorapi.receiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.aliorapi.AliorClientException;
import com.sarxos.aliorapi.entity.BrokerAccount;
import com.sarxos.aliorapi.entity.BrokerPaper;


public class PapersReceiver extends Receiver {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(PapersReceiver.class.getSimpleName());

	/**
	 * Default used date format.
	 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

	private BrokerAccount account = null;

	public PapersReceiver(BrokerAccount account) {
		this.account = account;
	}

	public List<BrokerPaper> fetch(WebDriver driver) throws AliorClientException {

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
					option.click();
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
					try {
						Thread.sleep(5000);
						LOG.debug("Waiting for the JSON to finish loading");
					} catch (InterruptedException e) {
						LOG.error(e.getMessage(), e);
					}
				}
			} while (tries++ < max);

		} while (!account.getNumber().equals(number) && attempts++ < 5);

		// now we are sure we are in the correct account, so switch to the
		// account details and fetch list of all securities

		WebElement balance = getElement(driver, By.id("Broker.Account.AccountBalance"));
		click(balance);

		WebElement div = getElement(driver, By.id("securities_balance_body"));
		List<WebElement> trs = getElements(div, By.cssSelector("table > tbody > tr[onmouseover]"));

		List<BrokerPaper> papers = new LinkedList<BrokerPaper>();
		for (WebElement tr : trs) {
			papers.add(convertTR(tr));
		}

		return papers;
	}

	private BrokerPaper convertTR(WebElement tr) {
		BrokerPaper paper = null;
		List<WebElement> tds = getElements(tr, By.tagName("td"));
		for (int i = 0; i < tds.size(); i++) {
			WebElement td = tds.get(i);
			switch (i) {
				case 0:
					// name
					String name = td.getText().trim();
					paper = new BrokerPaper(name);
					break;

				case 1:
					// buy/sell
					break;

				case 2:
					// available quantity
					int aq = Integer.parseInt(td.getText().trim().replaceAll("\\s", ""));
					paper.setAvailableQuantity(aq);
					break;

				case 3:
					// blocked quantity
					int bq = Integer.parseInt(td.getText().trim().replaceAll("\\s", ""));
					paper.setLockedQuantity(bq);
					break;

				case 4:
					// other blocked quantity
					int obq = Integer.parseInt(td.getText().trim().replaceAll("\\s", ""));
					paper.setOtherLockedQuantity(obq);
					break;

				case 5:
					// kdwp
					int kdwp = Integer.parseInt(td.getText().trim().replaceAll("\\s", ""));
					paper.setKDWP(kdwp);
					break;

				case 6:
					// price
					String p = td.getText().trim().replaceAll("PLN", "");
					p = p.replaceAll("\\s", "");
					p = p.replaceAll(",", ".");
					double price = Double.parseDouble(p);
					paper.setPrice(price);
					break;

				case 7:
					// date
					String d = td.getText().trim();
					if (d.length() > 0) {
						Date date = null;
						try {
							date = DATE_FORMAT.parse(d);
						} catch (ParseException e) {
							LOG.error("Cannot parse date '" + d + "'");
						}
						paper.setDate(date);
					}
					break;

				case 8:
					// evaluation
					String ev = td.getText().trim();
					if (ev.length() > 0) {
						ev = ev.replaceAll("PLN", "");
						ev = ev.replaceAll("\\s", "");
						ev = ev.replaceAll(",", ".");
						double eval = Double.parseDouble(ev);
						paper.setEvaluation(eval);
					}
					break;
			}
		}

		return paper;
	}
}
