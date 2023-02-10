package com.revenuecat.purchases.common.diagnostics

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.revenuecat.purchases.common.FileHelper
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class DiagnosticsFileHelperTest {

    private val testDiagnosticsEvent = DiagnosticsEvent.Log(
        name = DiagnosticsLogEventName.ENDPOINT_HIT,
        properties = emptyMap()
    )
    private val diagnosticsFilePath = DiagnosticsFileHelper.DIAGNOSTICS_FILE_PATH

    private lateinit var fileHelper: FileHelper

    private lateinit var diagnosticsFileHelper: DiagnosticsFileHelper

    @Before
    fun setup() {
        fileHelper = mockk()
        diagnosticsFileHelper = DiagnosticsFileHelper(fileHelper)
    }

    @Test
    fun `appendEventToDiagnosticsFile calls are correct`() {
        val contentToAppend = "$testDiagnosticsEvent\n"
        every { fileHelper.appendToFile(diagnosticsFilePath, contentToAppend) } just Runs
        diagnosticsFileHelper.appendEventToDiagnosticsFile(testDiagnosticsEvent)
        verify(exactly = 1) { fileHelper.appendToFile(diagnosticsFilePath, contentToAppend) }
    }

    @Test
    fun `cleanSentDiagnostics calls are correct`() {
        every { fileHelper.removeFirstLinesFromFile(diagnosticsFilePath, 2) } just Runs
        diagnosticsFileHelper.cleanSentDiagnostics(2)
        verify(exactly = 1) { fileHelper.removeFirstLinesFromFile(diagnosticsFilePath, 2) }
    }

    @Test
    fun `deleteDiagnosticsFile calls are correct`() {
        every { fileHelper.deleteFile(diagnosticsFilePath) } returns true
        diagnosticsFileHelper.deleteDiagnosticsFile()
        verify(exactly = 1) { fileHelper.deleteFile(diagnosticsFilePath) }
    }

    @Test
    fun `readDiagnosticsFile returns empty list if file is empty`() {
        every { fileHelper.fileIsEmpty(diagnosticsFilePath) } returns true
        assertThat(diagnosticsFileHelper.readDiagnosticsFile()).isEqualTo(emptyList<JSONObject>())
        verify(exactly = 1) { fileHelper.fileIsEmpty(diagnosticsFilePath) }
        verify(exactly = 0) { fileHelper.readFilePerLines(diagnosticsFilePath) }
    }

    @Test
    fun `readDiagnosticsFile reads content as json`() {
        every { fileHelper.fileIsEmpty(diagnosticsFilePath) } returns false
        every { fileHelper.readFilePerLines(diagnosticsFilePath) } returns listOf(
            "{}", "{\"test_key\": \"test_value\"}"
        )
        val result = diagnosticsFileHelper.readDiagnosticsFile()
        assertThat(result.size).isEqualTo(2)
        assertThat(result[0].length()).isEqualTo(0)
        assertThat(result[1]["test_key"]).isEqualTo("test_value")
        verify(exactly = 1) { fileHelper.readFilePerLines(diagnosticsFilePath) }
    }
}