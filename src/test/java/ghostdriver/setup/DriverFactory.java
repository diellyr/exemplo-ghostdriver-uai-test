package ghostdriver.setup;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

public class DriverFactory
{
	private static WebDriver driver;
	private static Wait<WebDriver> wait;
	protected static DesiredCapabilities dcaps;

	public static WebDriver getDriver()
	{
		if (driver == null || ((RemoteWebDriver) driver).getSessionId() == null)
		{
			createDriver();
		}

		return driver;
	}

	public static WebDriver createDriver()
	{
		dcaps = DesiredCapabilities.phantomjs();
		dcaps.setJavascriptEnabled(true);
		dcaps.setCapability("takesScreenshot", false);
		
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--web-security=false");
		cliArgsCap.add("--ssl-protocol=any");
		cliArgsCap.add("--ignore-ssl-errors=true");
		cliArgsCap.add("--proxy-type=none");
		
		File phantomExec = new File("src/test/resources/phantomjs.exe");
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomExec.getAbsolutePath());
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
		driver = new PhantomJSDriver(dcaps);

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		wait = null;

		return driver;
	}

	public static Wait<WebDriver> getWait()
	{
		if (wait == null)
		{
			wait = createWait(10, 100);
		}

		return wait;
	}

	public static Wait<WebDriver> createWait(long withTimeout, long pollingEvery)
	{
		return new FluentWait<WebDriver>(getDriver())
				.withTimeout(withTimeout, TimeUnit.SECONDS)
				.pollingEvery(pollingEvery, TimeUnit.MILLISECONDS)
				.ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class);		
	}
}
