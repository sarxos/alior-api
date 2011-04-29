package com.sarxos.aliorapi.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * HTTP client.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class AliorHttpClient extends DefaultHttpClient {

	/**
	 * Create naive SSLSocket factory which will authorize any TSL/SSL host.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected static class NaiveSSLFactory {

		/**
		 * @return Return naive SSL socket factory (authorize any SSL/TSL host)
		 */
		public static SSLSocketFactory createNaiveSSLSocketFactory() {
			X509TrustManager manager = new NaiveX509TrustManager();
			SSLContext sslcontext = null;
			try {
				TrustManager[] managers = new TrustManager[] { manager };
				sslcontext = SSLContext.getInstance("SSL");
				sslcontext.init(null, managers, null);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}
			return new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		}
	}

	/**
	 * The goal of this trust manager is to do nothing - it will authorize any
	 * TSL/SSL secure connection.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected static class NaiveX509TrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] certs, String str) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] certs, String str) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AliorHttpClient.class.getSimpleName());

	/**
	 * Proxy host.
	 */
	private static String PROXY_HOST = (String) System.getProperties().get("http.proxyHost");

	/**
	 * Proxy port number.
	 */
	private static String PROXY_PORT = (String) System.getProperties().get("http.proxyPort");

	static {
		if (PROXY_HOST != null && PROXY_PORT != null) {
			LOG.info("Setting proxy '" + PROXY_HOST + ":" + PROXY_PORT + "'");
		}
	}

	/**
	 * HTTP proxy.
	 */
	private HttpHost proxy = null;

	public AliorHttpClient() {
		super();
		init();
	}

	public AliorHttpClient(ClientConnectionManager conman, HttpParams params) {
		super(conman, params);
		init();
	}

	public AliorHttpClient(ClientConnectionManager conman) {
		super(conman);
		init();
	}

	public AliorHttpClient(HttpParams params) {
		super(params);
		init();
	}

	/**
	 * Initialize client.
	 */
	private void init() {

		if (PROXY_HOST != null && PROXY_PORT != null) {
			proxy = new HttpHost(PROXY_HOST, Integer.parseInt(PROXY_PORT), "http");
			getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}

		// set up TSL/SSL naive settings
		SSLSocketFactory factory = NaiveSSLFactory.createNaiveSSLSocketFactory();
		ClientConnectionManager manager = getConnectionManager();

		// add https 443 by default
		SchemeRegistry registry = manager.getSchemeRegistry();
		registry.register(new Scheme("https", 443, factory));
	}

	/**
	 * @return Return proxy used by this client or null if no proxy is used
	 */
	public HttpHost getProxy() {
		return proxy;
	}

	/**
	 * Download file from specific URL.
	 * 
	 * @param from - file to download
	 * @param to - file to be stored locally
	 * @throws HttpException when something wrong happens
	 */
	public void download(String from, File to) throws HttpException {
		int attempts = 0;
		int max = 5;
		do {
			try {
				download0(from, to);
				return;
			} catch (HttpException e) {
				LOG.error(
						"Invalid download attempt. " +
						(attempts < max - 1 ? " Trying one more time" : "Fatal."), e);
			}
		} while (attempts++ < max);
	}

	/**
	 * File download impl.
	 * 
	 * @param url - file to download URL
	 * @param f - destination file
	 * @throws HttpException
	 */
	private void download0(String url, File f) throws HttpException {

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			LOG.error("File " + f.getPath() + " does not exist");
			return;
		}

		HttpEntity entity = null;

		try {
			HttpGet get = new HttpGet(url);
			HttpResponse response = execute(get);
			entity = response.getEntity();
			entity.writeTo(fos);
		} catch (Exception e) {
			throw new HttpException(
					"Cannot download file from '" + url + "' to '" +
					f.getPath() + "'", e);
		} finally {

			if (entity != null) {
				try {
					entity.getContent().close();
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}
}
