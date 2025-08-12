package com.bina.home.data.database

import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertNotNull

class AppDatabaseUnitTest {
    @Test
    fun `userDao should return dao instance`() {
        // given
        val dao = mockk<UserDao>()
        val db = object : AppDatabase() {
            override fun userDao(): UserDao = dao
            override fun clearAllTables() {}
            override fun createInvalidationTracker() = throw NotImplementedError()
        }
        // when
        val result = db.userDao()
        // then
        assertNotNull(result)
    }
}
