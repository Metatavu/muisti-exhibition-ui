package fi.metatavu.muisti.exhibitionui

import fi.metatavu.muisti.api.client.models.Exhibition
import fi.metatavu.muisti.exhibitionui.test.functional.ApiTestBuilder
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.AndroidKeyCode
import io.appium.java_client.remote.MobileCapabilityType
import org.junit.Assert
import org.junit.Test
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import java.io.File
import java.lang.RuntimeException
import java.net.ConnectException
import java.net.URL

/**
 * Example appium test
 */
class AppiumTest {

    @Test
    @Throws(Exception::class)
    fun testApp() {

        ApiTestBuilder().use { it ->
            val exhibition: Exhibition = it.admin().exhibitions().create();
            Assert.assertNotNull(exhibition)
            val app = File("/root/tmp", "app-debug.apk")
            val capabilities = DesiredCapabilities()
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android")
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "10.0")
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator")
            capabilities.setCapability("app", app.absolutePath)
            capabilities.setCapability("automationName", "UiAutomator2")
            capabilities.setCapability(MobileCapabilityType.APP_PACKAGE, "fi.metatavu.muisti.exhibitionui")
            capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, "views.MainActivity")
            val driver: AndroidDriver<WebElement> = AndroidDriver(URL("http://localhost:4723/wd/hub"), capabilities)
            Thread.sleep(10000)
            val dropdowns = driver.findElementsByXPath("//*")
                .filter { it.getAttribute("class") == "android.widget.LinearLayout" && it.getAttribute("clickable") == "true" }
            dropdowns[0].click()
            driver.quit()
        }
    }
}