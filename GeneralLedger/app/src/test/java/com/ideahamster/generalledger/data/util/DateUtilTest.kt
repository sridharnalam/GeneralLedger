package com.ideahamster.generalledger.data.util

import android.text.TextUtils
import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.anyOrNull

class DateUtilTest {

    @Test
    fun `null dateString changeDateFormat returns default`() {
        mockkStatic(TextUtils::class)
        every { TextUtils.isEmpty(anyOrNull()) } answers {
            args[0] == null || arg<String>(0).isEmpty()
        }
        assertEquals(DateUtil.changeDateFormat(null), "N/A")
    }

    @Test
    fun `empty dateString changeDateFormat returns default`() {
        mockkStatic(TextUtils::class)
        every { TextUtils.isEmpty(any()) } answers {
            arg<String>(0).isEmpty()
        }
        assertEquals(DateUtil.changeDateFormat(""), "N/A")
    }

    @Test
    fun `valid dateString changeDateFormat returns valid`() {
        mockkStatic(TextUtils::class)
        every { TextUtils.isEmpty(any()) } answers {
            arg<String>(0).isEmpty()
        }
        assertEquals(DateUtil.changeDateFormat("2022-01-31 05:29:55 -0400"), "2022-01-31 18:29:55")
    }

    @Test
    fun `invalid dateString changeDateFormat returns default`() {
        mockkStatic(TextUtils::class)
        mockkStatic(Log::class)
        every { TextUtils.isEmpty(any()) } answers {
            arg<String>(0).isEmpty()
        }
        every { Log.e(any(), any()) } returns 0
        assertEquals(DateUtil.changeDateFormat("2022-31 05:29:55 -0400"), "N/A")
    }

    @Test
    fun `empty dateString toSourceDateString returns default`() {
        mockkStatic(TextUtils::class)
        every { TextUtils.isEmpty(any()) } answers {
            arg<String>(0).isEmpty()
        }
        assertEquals(DateUtil.toSourceDateString(""), "N/A")
    }

    @Test
    fun `valid dateString toSourceDateString returns valid`() {
        mockkStatic(TextUtils::class)
        every { TextUtils.isEmpty(any()) } answers {
            arg<String>(0).isEmpty()
        }
        assertEquals(DateUtil.toSourceDateString("2022-01-31 18:29:55"), "2022-01-31 18:29:55 +0900")
    }

    @Test
    fun `invalid dateString toSourceDateString returns default`() {
        mockkStatic(TextUtils::class)
        mockkStatic(Log::class)
        every { TextUtils.isEmpty(any()) } answers {
            arg<String>(0).isEmpty()
        }
        every { Log.e(any(), any()) } returns 0
        assertEquals(DateUtil.toSourceDateString("2022-01-31 18:29"), "N/A")
    }
}