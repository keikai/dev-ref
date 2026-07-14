/* WebDriverTestCase.java

	Purpose:

	Description:

	History:
		2:01 PM 12/17/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.test.webdriver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Strings;
import org.zkoss.test.webdriver.ztl.ClientWidget;
import org.zkoss.test.webdriver.ztl.Element;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;
import org.zkoss.test.webdriver.ztl.ZK;

/**
 * A base class to test using WebDriver.
 * <p>Currently support Chromium headless</p>
 * @author jumperchen
 */
public abstract class BaseTestCase {
	private static final Logger log = LoggerFactory.getLogger(BaseTestCase.class);
	public static final String PACKAGE = System.getProperty("zkWebdriverTestURLPackage", "");
	private static final ThreadLocal<Integer> port = new ThreadLocal<>();
	protected static int static_port;
	protected static final ThreadLocal<WebDriver> _local = new ThreadLocal<WebDriver>();
	private static final String JS_DROP_FILES = "var c=arguments,b=c[0],k=c[1];c=c[2];for(var d=b.ownerDocument||document,l=0;;){var e=b.getBoundingClientRect(),g=e.left+(k||e.width/2),h=e.top+(c||e.height/2),f=d.elementFromPoint(g,h);if(f&&b.contains(f))break;if(1<++l)throw b=Error('Element not interactable'),b.code=15,b;b.scrollIntoView({behavior:'instant',block:'center',inline:'center'})}var a=d.createElement('INPUT');a.setAttribute('type','file');a.setAttribute('multiple','');a.setAttribute('style','position:fixed;z-index:2147483647;left:0;top:0;');a.onchange=function(b){a.parentElement.removeChild(a);b.stopPropagation();var c={constructor:DataTransfer,effectAllowed:'all',dropEffect:'none',types:['Files'],files:a.files,setData:function(){},getData:function(){},clearData:function(){},setDragImage:function(){}};window.DataTransferItemList&&(c.items=Object.setPrototypeOf(Array.prototype.map.call(a.files,function(a){return{constructor:DataTransferItem,kind:'file',type:a.type,getAsFile:function(){return a},getAsString:function(b){var c=new FileReader;c.onload=function(a){b(a.target.result)};c.readAsText(a)}}}),{constructor:DataTransferItemList,add:function(){},clear:function(){},remove:function(){}}));['dragenter','dragover','drop'].forEach(function(a){var b=d.createEvent('DragEvent');b.initMouseEvent(a,!0,!0,d.defaultView,0,0,0,g,h,!1,!1,!1,!1,0,null);Object.setPrototypeOf(b,null);b.dataTransfer=c;Object.setPrototypeOf(b,DragEvent.prototype);f.dispatchEvent(b)})};d.documentElement.appendChild(a);a.getBoundingClientRect();return a;";
	private static final String HOST;
	private final static int OS_TYPE;
	private static final String REMOTE_WEB_DRIVER_URL = System.getProperty("RemoteWebDriverUrl", "");

	private final static String OS = System.getProperty("os.name").toLowerCase();

	private boolean connectWaiting = true;
	static {
		if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
			OS_TYPE = 1;
		} else if (OS.contains("win")) {
			OS_TYPE = 2;
		} else if (OS.contains("mac")) {
			OS_TYPE = 3;
		} else if (OS.contains("sunos")) {
			OS_TYPE = 4;
		} else {
			OS_TYPE = -1;
		}
	}

	static {
		// Shadowed copy of zk-webdriver's BaseTestCase with ONE fix: honor an
		// explicitly-set -DHost=... instead of always overwriting it with the
		// outbound-socket local address. On machines with a VPN (utun) the
		// probe returns a point-to-point address (e.g. 172.16.0.2) that does
		// not accept inbound connections, so the embedded Jetty is unreachable.
		// Delete this file once zk-webdriver honors the Host property.
		String host = System.getProperty("Host");
		if (host == null) {
			host = "127.0.0.1";
			// lookup IP for support Docker Chrome instance
			try {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress("google.com", 80));
				host = socket.getLocalAddress().getHostAddress();
			} catch (IOException e) {
				try {
					InetAddress candidateAddress = getLocalHostLANAddress();
					host = candidateAddress.getHostAddress();
				} catch (UnknownHostException ex) {
					ex.printStackTrace();
				}

			}
		}
		HOST = host;
	}
	private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
		try {
			InetAddress candidateAddress = null;
			// Iterate all NICs (network interface cards)...
			for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				// Iterate all IP addresses assigned to each card...
				for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {

						if (inetAddr.isSiteLocalAddress()) {
							// Found non-loopback site-local address. Return it immediately...
							return inetAddr;
						}
						else if (candidateAddress == null) {
							// Found non-loopback address, but not necessarily site-local.
							// Store it as a candidate to be returned if site-local address is not subsequently found...
							candidateAddress = inetAddr;
							// Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
							// only the first. For subsequent iterations, candidate will be non-null.
						}
					}
				}
			}
			if (candidateAddress != null) {
				// We did not find a site-local address, but we found some other non-loopback address.
				// Server might have a non-site-local address assigned to its NIC (or it might be running
				// IPv6 which deprecates the "site-local" concept).
				// Return this non-loopback candidate address...
				return candidateAddress;
			}
			// At this point, we did not find a non-loopback address.
			// Fall back to returning whatever InetAddress.getLocalHost() returns...
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress;
		}
		catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}

	protected WebDriver driver;

	protected static int getPort() {
		return Optional.ofNullable(port.get()).orElse(static_port);
	}

	protected static String getContextPath() {
		String zkWebdriverContextPath = System.getProperty(
				"zkWebdriverContextPath");
		if (zkWebdriverContextPath == null) {
			throw new IllegalArgumentException("\"zkWebdriverContextPath\" is not found in System.getProperty()");
		}
		return zkWebdriverContextPath;
	}

	protected static String getHost() {
		return HOST;
	}

	protected static String getAddress() {
		return "http://" + getHost() + ":" + getPort() + getContextPath();
	}

	protected boolean isHeadless() {
		return true;
	}

	protected WebDriver getWebDriver() {
		if (driver == null) {
			ChromeOptions driverOptions = getWebDriverOptions();
			driver = isUsingRemoteWebDriver(driverOptions)
					? new DockerRemoteWebDriver(getRemoteWebDriverUrl(), driverOptions)
					: new ChromiumHeadlessDriver(driverOptions, isHeadless());
		}
		return driver;
	}

	protected String getRemoteWebDriverUrl() {
		return REMOTE_WEB_DRIVER_URL;
	}

	protected boolean isUseDocker() {
		return false;
	}

	@SuppressWarnings("unchecked")
	protected boolean isUsingRemoteWebDriver(ChromeOptions driverOptions) {
		return isUseDocker();
	}
	protected boolean isUsingRemoteWebDriver(FirefoxOptions driverOptions) {
		return isUseDocker();
	}

	/**
	 * Gets the WebDriver options.
	 * You can add arguments for Chromium to change settings like locale or user-agent string.
	 * A list of available options can be found at <a href="http://chromedriver.chromium.org/capabilities">Capabilities {@literal &} ChromeOptions</a>.
	 *
	 * @return WebDriver options
	 */
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("window-size=1920,1080", "--remote-allow-origins=*");
		return options;
	}

	public WebDriver connect() {
		return connect(null);
	}

	protected String getTestURL(String file) {
		String simple = this.getClass().getSimpleName();
		String name = this.getClass().getName().replace(PACKAGE, "").replace(".", "/").replace(simple, "");

		return name + file;
	}

	protected String getFileLocation() {
		String className = this.getClass().getName().replace(PACKAGE, "").replace(".", "/");
		int lastTest = className.lastIndexOf("Test");
		return className.substring(0, lastTest) + getFileExtension();
	}

	protected String getFileExtension() {
		return ".zul";
	}

	public WebDriver connect(String location) {
		WebDriver webDriver = initWebDriver();
		final String address = getAddress();
		if (location == null) {
			location = getFileLocation();
			String loc = address + location;
			int errCode = getStatusCode(loc);
			if (errCode == 404) {
				loc = address + location.replace("_", "-");
				errCode = getStatusCode(loc);
				if (errCode == 404) {
					loc = address + location.replace("-", "_");
					errCode = getStatusCode(loc);
				}
				if (errCode == 404) {
					fail("Error Code: " + errCode + ", from URL[" + loc + "]");
				}
			}
			webDriver.get(loc);
		} else {
			String loc = address + location;
			int errCode = getStatusCode(loc);
			if (errCode != 200) {
				fail("Error Code: " + errCode + ", from URL[" + loc + "]");
			}
			webDriver.get(loc);
		}

		// manual to disable waiting connection.
		if (connectWaiting) {
			// add a check for zk.wpd script loading automatically.
			if (ExpectedConditions.alertIsPresent().apply(webDriver) == null) {
				try {
					if (((JavascriptExecutor) this.driver).executeScript(
							"return Array.from(document.scripts).some(script => script.src.includes('zk.wpd'));")
							== Boolean.TRUE) {
						waitForAjaxResponse(2, getTimeout(),
								System.currentTimeMillis(),
								"!window.zk || window.zk.loading");
					}
				} catch (UnhandledAlertException e) {
					// ignore to outside to handle.
				}
			}
		}

		return webDriver;
	}

	/**
	 * Disables the connection waiting.
	 * <p>By default, it will wait for the connection to be established. In some cases,
	 * you may want to disable it, such as alert text at connect phase.</p>
	 */
	protected void disableConnectionWait() {
		connectWaiting = false;
	}

	/**
	 * Enables the connection waiting.
	 * @see #disableConnectionWait()
	 */
	protected void enableConnectionWait() {
		connectWaiting = true;
	}

	protected WebDriver initWebDriver() {
		WebDriver webDriver = getWebDriver();
		_local.set(webDriver);
		return webDriver;
	}

	protected int getTimeout() {
		return 4000;
	}

	@AfterEach
	public void stop() {
		if (driver != null) {
			driver.quit();
			driver = null;
		}
		_local.set(null);
	}

	protected static String getServerPort() {
		return System.getProperty("jetty.port", "0");
	}

	protected static String getBaseResource() {
		return System.getProperty("zkWebdriverBaseResource",
				"./src/main/webapp/");
	}

	protected void initServer(Server currentServer) throws Exception {
		for (Connector c : currentServer.getConnectors()) {
			if (c instanceof NetworkConnector) {
				if (((NetworkConnector) c).getLocalPort() > 0) {
					static_port = ((NetworkConnector) c).getLocalPort();
					port.set(static_port);
					break;
				}
			}
		}
	}

	protected void destroyServer(Server currentServer) {
		port.set(null);
	}

	/**
	 * Waits for Ajax response. (excluding animation check)
	 * @see #waitResponse(int)
	 */
	protected void waitResponse() {
		waitResponse(getTimeout());
	}

	/**
	 * Waits for Ajax response.
	 * <p>By default the timeout time is specified in config.properties
	 * @param includingAnimation if true, it will include animation check.
	 * @see #waitResponse(int, boolean)
	 */
	protected void waitResponse(boolean includingAnimation) {
		waitResponse(getTimeout(), includingAnimation);
	}

	/**
	 * Returns the wait response speed.
	 * <p>Default: 500ms</p>
	 * @return
	 */
	protected int getSpeed() {
		return 500;
	}

	/**
	 * Waits for Ajax response according to the timeout attribute.
	 * @param timeout
	 * @param includingAnimation if true, it will include animation check.
	 *
	 */
	protected void waitResponse(int timeout, boolean includingAnimation) {
		sleep(getSpeed() / 2); // take a break first.
		String defaultScript = "!!zAu.processing() || (window.mobx && window.mobx._getGlobalState() && window.mobx._getGlobalState().isRunningReactions)";
		waitForAjaxResponse(getRetryCount(includingAnimation), timeout, System.currentTimeMillis(),
				includingAnimation
						? defaultScript + " || !!jq.timers.length"
						: defaultScript);
	}

	/**
	 * Returns the retry count for waiting for Ajax response.
	 * Default: 3 and if including animation, 5.
	 */
	protected int getRetryCount(boolean includingAnimation) {
		return includingAnimation ? 5 : 3;
	}

	private void waitForAjaxResponse(int loopCount, int timeout, long startTime, String waitForScript) {
		int ms = getSpeed();
		int i = 0;
		while (i < loopCount) { // make sure the command is triggered.
			while (Boolean.parseBoolean(getEval(waitForScript))) {
				if (System.currentTimeMillis() - startTime > timeout) {
					fail("Test case timeout!");
					break;
				}
				i = 0; // reset
				sleep(ms);
			}
			i++;
			sleep(ms);
		}
	}

	public static String getEval(String script) {
		WebDriver driver = _local.get();
		return String.valueOf(((JavascriptExecutor) driver).executeScript("return (" + script + ")"));
	}

	public static void eval(String script) {
		WebDriver driver = _local.get();
		((JavascriptExecutor) driver).executeScript("(" + script + ")");
	}

	/**
	 * Causes the currently executing thread to sleep for the specified number
	 * of milliseconds, subject to the precision and accuracy of system timers
	 * and schedulers. The thread does not lose ownership of any monitors.
	 * @param millis the length of time to sleep in milliseconds.
	 */
	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Waits for Ajax response according to the timeout attribute.(excluding animation check)
	 * @param timeout the time. (millisecond).
	 * @see #waitResponse(int, boolean)
	 */
	protected void waitResponse(int timeout) {
		waitResponse(timeout, false);
	}

	/**
	 * Returns the Widget object of the UUID.
	 * @param uuid the element ID.
	 */
	protected Widget widget(String uuid) {
		return new Widget(uuid);
	}

	/**
	 * Returns the Widget object of the given element.
	 * @param element the element.
	 */
	protected Widget widget(Element element) {
		return new Widget(element);
	}

	/**
	 * Returns the Widget object from the JQuery object.
	 * @param jQuery the JQuery object.
	 */
	protected Widget widget(JQuery jQuery) {
		return new Widget(jQuery);
	}

	/**
	 * Returns the Jquery object of the selector
	 * <p> Default: without "#" sign
	 * @param selector the selector
	 */
	protected JQuery jq(String selector) {
		return new JQuery(selector);
	}

	/**
	 * Returns the Jquery object of the ZKClientObject.
	 * @param el the ZKClientObject
	 */
	protected JQuery jq(ClientWidget el) {
		if (el instanceof JQuery)
			return (JQuery) el;
		return new JQuery(el);
	}

	/**
	 * Returns the ZK object of the ZKClientObject.
	 * @param el the ZKClientObject
	 */
	protected ZK zk(ClientWidget el) {
		if (el instanceof ZK)
			return (ZK) el;
		return new ZK(el);
	}

	/**
	 * Returns the ZK object of the selector
	 * @param selector the selector of the element
	 */
	protected ZK zk(String selector) {
		return new ZK(selector);
	}

	/**
	 * Returns the int value from the given string number.
	 * @param number the string number, if null or empty, 0 is assumed.
	 */
	public static int parseInt(String number) {
		if (number != null) {
			number = number.replaceAll("[^-0-9\\.]", "");
			int decimal = number.indexOf('.');
			if (decimal > 0)
				number = number.substring(0, decimal);

			if (!"".equals(number.trim())) {
				return Integer.parseInt(number);
			} else {
				return 0;
			}
		}
		return 0;
	}

	/**
	 * Execute all handlers and behaviors attached to the matched elements for the given event type
	 * @param widget
	 * @param event
	 */
	protected void trigger(ClientWidget widget, String event) {
		((JavascriptExecutor) driver).executeScript(jq(widget).toLocator() + ".trigger('" + event + "')");
	}

	public static WebElement toElement(ClientWidget locator) {
		WebDriver webDriver = _local.get();
		if (locator instanceof Widget)
			return (WebElement) ((JavascriptExecutor) webDriver).executeScript("return (" + locator + ").$n();");
		else if (locator instanceof JQuery)
			return (WebElement) ((JavascriptExecutor) webDriver).executeScript("return (" + locator + ")[0];");
		return (WebElement) ((JavascriptExecutor) webDriver).executeScript("return (" + locator + ");");
	}

	/**
	 * Trims the multiline string into one line string.
	 */
	public static String trim(String text) {
		return text.replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2").replace("\n", "").replace("\r", "");
	}

	/**
	 * Returns the text of zk.log from client side.
	 */
	protected String getZKLog() {
		return jq("#zk_log").val().trim();
	}

	/**
	 * Returns the text of zk.log from client side.
	 */
	protected boolean isZKLogAvailable() {
		WebElement log = null;
		try {
			log = getWebDriver().findElement(By.id("zk_log"));
		} catch (NoSuchElementException e) {
			//do nothing
		}
		return log != null;
	}

	/**
	 * Closes the zk.log console and removes it.
	 */
	protected void closeZKLog() {
		jq("#zk_logbox").remove();
	}

	// browser operation

	/**
	 * Sets the focus state to the given locator.
	 * @param locator
	 */
	protected void focus(ClientWidget locator) {
		eval(jq(locator).toLocator() + ".focus()");
	}

	/**
	 * Sets the blur state to the given locator.
	 * @param locator
	 */
	protected void blur(ClientWidget locator) {
		eval(jq(locator).toLocator() + ".blur()");
	}

	/**
	 * Clicks upon the given locator.
	 * @param locator
	 */
	protected void click(ClientWidget locator) {
		toElement(locator).click();
	}

	protected void clickAt(ClientWidget locator, int offsetX, int offsetY) {
		WebElement webElement = toElement(locator);
		getActions().moveToElement(webElement, offsetX, offsetY).click().perform();
	}

	/**
	 * Right clicks upon the given locator.
	 * @param locator
	 */
	protected void rightClick(ClientWidget locator) {
		Actions act = getActions();
		act.contextClick(toElement(locator));
		act.perform();
	}

	/**
	 * Double clicks upon the given locator.
	 * @param locator
	 */
	protected void dblClick(ClientWidget locator) {
		Actions act = getActions();
		act.doubleClick(toElement(locator));
		act.perform();
	}

	/**
	 * Checks the given locator. It's the same as {@link #click(ClientWidget)} internally.
	 * @param locator
	 */
	protected void check(ClientWidget locator) {
		click(locator);
	}

	/**
	 * Types the text into the given locator.
	 * <p>By default, it will simulate a real user behavior to focus the input elemnt
	 * from the given locator,
	 * and then replace the old text with the new text and then blur the input element.</p>
	 * @param locator
	 * @param text
	 */
	protected void type(ClientWidget locator, String text) {
		focus(locator);
		WebElement webElement = toElement(locator);
		webElement.sendKeys(Keys.chord(isMac() ? Keys.META : Keys.CONTROL, "a"));

		// Fix "" text cannot be inserted, we use "BACK_SPACE" instead.
		if (Strings.isEmpty(text)) {
			webElement.sendKeys(Keys.BACK_SPACE);
		} else {
			webElement.sendKeys(text);
		}
		blur(locator);
	}

	/**
	 * Use this method to simulate typing into an element, which may set its value.
	 * @param keysToSend character sequence to send to the element
	 */
	protected void sendKeys(ClientWidget locator, CharSequence... keysToSend) {
		getWebDriver().findElement(locator).sendKeys(keysToSend);
	}

	/**
	 * Selects an comboitem from the given combobox.
	 * @param combobox
	 * @param index
	 */
	protected Widget selectComboitem(Widget combobox, int index) {
		click(combobox.$n("btn"));
		waitResponse(true);
		Element element = jq(combobox.$n("pp")).find(".z-comboitem").get(index);
		click(element);
		waitResponse(true);
		return widget(element);
	}

	/**
	 * Gets the HTTP response status code.
	 *
	 * @param url the URL
	 * @return status code. -1 if the connection has any exception.
	 */
	public static int getStatusCode(String url) {
		HttpURLConnection http = null;
		try {
			URL u = new URL(url);
			http = (HttpURLConnection) u.openConnection();
			return http.getResponseCode();
		} catch (Exception e) {
			return -1;
		} finally {
			if (http != null)
				http.disconnect();
		}
	}

	/**
	 * Strips ;jsessionid= in URL because it is annoying when comparing URLs.
	 *
	 * @param url the original URL
	 * @return stripped URL
	 */
	protected String stripJsessionid(String url) {
		return url.replaceAll(";jsessionid=[^?]*", "");
	}

	public boolean hasError() {
		return Boolean
				.valueOf(getEval("!!jq('.z-messagebox-error')[0] || !!jq('.z-errorbox')[0] || !!jq('.z-error')[0]"));
	}

	/**
	 * Asserts no ZK error
	 */
	protected void assertNoZKError() {
		assertFalse(hasError());
	}

	/**
	 * Asserts no ZK and JS error
	 */
	protected void assertNoAnyError() {
		assertNoJSError();
		assertNoZKError();
	}

	/**
	 * Asserts no JavaScript errors occur.
	 * If there is any error on browser console, an {@link Assertions#fail(String)} would be raised.
	 */
	protected void assertNoJSError() {
		driver.manage().logs().get(LogType.BROWSER).getAll().stream()
				.filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue()).findFirst()
				.ifPresent(log -> fail(log.toString()));
	}

	/**
	 * Returns the text content of zk messagebox from client side.
	 */
	protected String getMessageBoxContent() {
		return jq(".z-messagebox").text().replaceAll("\u00A0", " ").trim();
	}

	/**
	 * Returns the browser actions.
	 * Starting from 1.4.39.0.1, pauses for 100 milliseconds after {@link Actions#clickAndHold}.
	 */
	protected Actions getActions() {
		return getActions(Duration.ofMillis(100));
	}

	/**
	 * Returns the browser actions.
	 * 
	 * @param pause the pause duration after {@link Actions#clickAndHold}
	 * @since 1.4.39.0.1
	 */
	protected Actions getActions(Duration pause) {
		return new Actions(getWebDriver()) {
			@Override
			public Actions clickAndHold() {
				return super.clickAndHold().pause(pause);
			}

			@Override
			public Actions clickAndHold(WebElement target) {
				return super.clickAndHold(target).pause(pause);
			}
		};
	}

	/**
	 * Simulates drag and drop a file to a Dropupload element.
	 *
	 * @param element Dropupload element
	 * @param file file path
	 * @throws FileNotFoundException file not found
	 */
	protected void dropUploadFile(JQuery element, Path file) throws FileNotFoundException {
		dropUploadFiles(element, Collections.singletonList(file), 0, 0);
	}

	/**
	 * Simulates drag and drop files to a Dropupload element.
	 *
	 * @param element Dropupload element
	 * @param files file paths
	 * @param offsetX Drop offset x relative to the top/left corner of the drop area. Center if 0.
	 * @param offsetY Drop offset y relative to the top/left corner of the drop area. Center if 0.
	 * @throws FileNotFoundException file not found
	 * @see <a href="https://stackoverflow.com/a/38830823">Selenium: Drag and Drop from file system to WebDriver?</a>
	 */
	protected void dropUploadFiles(JQuery element, List<Path> files, int offsetX, int offsetY)
			throws FileNotFoundException {
		List<String> paths = new ArrayList<>();
		for (Path file : files) {
			if (!Files.isRegularFile(file)) {
				throw new FileNotFoundException(file.toString());
			}
			paths.add(file.toAbsolutePath().toString());
		}

		eval(element + ".show()"); // needed to interact
		String value = String.join("\n", paths);
		WebElement input = (WebElement) ((JavascriptExecutor) driver).executeScript(JS_DROP_FILES, toElement(element),
				offsetX, offsetY);
		input.sendKeys(value);
	}

	/**
	 * Press hot key (e.g. Ctrl + X) to trigger a cut action.
	 * It's caller's responsibility to focus/select text before calling this method.
	 */
	protected void cut() {
		final Actions actions = getActions();
		if (isMac())
			actions.keyDown(Keys.SHIFT).sendKeys(Keys.DELETE).keyUp(Keys.SHIFT).perform();
		else
			actions.keyDown(Keys.CONTROL).sendKeys("x").keyUp(Keys.CONTROL).perform();
	}

	/**
	 * Press hot key (e.g. Ctrl + C) to trigger a copy action.
	 * It's caller's responsibility to focus/select text before calling this method.
	 */
	protected void copy() {
		// Workaround for https://bugs.chromium.org/p/chromedriver/issues/detail?id=30
		final Actions actions = getActions();
		if (isMac())
			actions.keyDown(Keys.META).sendKeys("c").keyUp(Keys.META).perform();
		else
			actions.keyDown(Keys.CONTROL).sendKeys("c").keyUp(Keys.CONTROL).perform();
	}

	/**
	 * Press hot key (e.g. Ctrl + V) to trigger a paste action.
	 * It's caller's responsibility to click/focus on a DOM node before calling this method.
	 */
	protected void paste() {
		final Actions actions = getActions();
		if (isMac())
			actions.keyDown(Keys.META).sendKeys("v").keyUp(Keys.META).perform();
		else
			actions.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).perform();
	}

	/**
	 * Trigger a select all action.
	 * It's caller's responsibility to click/focus on a DOM node before calling this method.
	 */
	protected void selectAll() {
		eval("document.activeElement.select && document.activeElement.select()");
		sleep(100);
	}

	/**
	 * Moves the text cursor to the specified position in the given input element or textarea.
	 *
	 * @param locator pointing to an input element or textarea
	 * @param position position (starts from 0)
	 */
	protected void setCursorPosition(ClientWidget locator, int position) {
		eval(zk(locator) + String.format(".setSelectionRange(%1$d, %1$d)", position));
		sleep(100);
	}

	/**
	 * Hover.
	 *
	 * @param locator element
	 */
	protected void mouseOver(ClientWidget locator) {
		getActions().moveToElement(toElement(locator)).pause(100).perform();
	}

	/**
	 * Drag the element from (x, y) (start from element center point),
	 * move the mouse by offset, and drop.
	 *
	 * @param locator element
	 * @param fromX X (start from element center point)
	 * @param fromY Y (start from element center point)
	 * @param offsetX Offset X (negative means left)
	 * @param offsetY Offset Y (negative means up)
	 */
	protected void dragdropTo(ClientWidget locator, int fromX, int fromY, int offsetX, int offsetY) {
		getActions().moveToElement(toElement(locator)).moveByOffset(fromX, fromY).clickAndHold()
				.moveByOffset(offsetX, offsetY).release().perform();
	}

	protected static boolean isUnix() {
		return OS_TYPE == 1;
	}

	protected static boolean isWindows() {
		return OS_TYPE == 2;
	}

	protected static boolean isSolaris() {
		return OS_TYPE == 4;
	}

	protected static boolean isMac() {
		return OS_TYPE == 3;
	}
}
