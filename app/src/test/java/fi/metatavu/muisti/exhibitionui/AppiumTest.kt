package fi.metatavu.muisti.exhibitionui

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.AndroidKeyCode
import io.appium.java_client.remote.MobileCapabilityType
import org.junit.Assert
import org.junit.Test
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import java.io.File
import java.net.URL

class AppiumTest {

    @Test
    @Throws(Exception::class)
    fun testApp() {
        val classpathRoot = File(System.getProperty("user.dir")!!)
        val app = File(classpathRoot.absolutePath, "app-debug.apk")
        val capabilities = DesiredCapabilities()
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android")
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "10.0")
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator")
        capabilities.setCapability("app", app.absolutePath)
        capabilities.setCapability("automationName", "UiAutomator2")
        capabilities.setCapability(MobileCapabilityType.APP_PACKAGE, "fi.metatavu.muisti.exhibitionui")
        capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, "views.MainActivity")
        val driver: AndroidDriver<WebElement> = AndroidDriver(URL("http://localhost:4723/wd/hub"), capabilities)
        Thread.sleep(5000)
        val dropdowns = driver.findElementsByXPath("//*").filter { it.getAttribute("class") == "android.widget.LinearLayout" && it.getAttribute("clickable") == "true"}
        dropdowns[0].click()
        Thread.sleep(500)
        driver.findElementsByXPath("//*").filter { it.getAttribute("class") == "android.widget.ListView"}[0].click()
        Thread.sleep(500)
        dropdowns[1].click()
        Thread.sleep(500)
        driver.findElementsByXPath("//*").filter { it.getAttribute("class") == "android.widget.CheckedTextView"}[1].click()
        driver.pressKeyCode(AndroidKeyCode.BACK)
        Thread.sleep(1000)
        Assert.assertEquals("PREVIEW PAGE", driver.findElementsByXPath("//*").filter { it.getAttribute("class") == "android.widget.Button"}[0].text);
    }
}