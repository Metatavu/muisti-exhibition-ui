package fi.metatavu.muisti.exhibitionui.test.functional

import org.junit.Assert.*
import org.junit.Test

/**
 * Example test, which uses API operations for testing
 */
class ExampleApiBasedTest {

    @Test
    fun testCreateExhibition() {
        ApiTestBuilder().use {
            assertNotNull(it.admin().exhibitions().create())
        }
    }

}
