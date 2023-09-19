package com.revenuecat.purchases.ui.revenuecatui.data.processed

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.revenuecat.purchases.ui.revenuecatui.data.TestData
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class VariableDataProviderTest {

    private lateinit var applicationContext: Context
    private lateinit var variableDataProvider: VariableDataProvider

    @Before
    fun setUp() {
        applicationContext = mockk()
        variableDataProvider = VariableDataProvider(applicationContext)
    }

    @Test
    fun `applicationName processes app name correctly`() {
        val testAppName = "test app name"
        val packageManager = mockk<PackageManager>()
        every { applicationContext.packageManager } returns packageManager
        every { applicationContext.applicationInfo } returns mockk<ApplicationInfo>().apply {
            every { loadLabel(packageManager) } returns testAppName
        }
        assertThat(variableDataProvider.applicationName).isEqualTo(testAppName)
    }

    @Test
    fun `localizedPrice provides correct price`() {
        val rcPackage = TestData.Packages.annual
        assertThat(variableDataProvider.localizedPrice(rcPackage)).isEqualTo("$67.99")
    }

    @Test
    fun `localizedPricePerMonth provides correct price`() {
        val rcPackage = TestData.Packages.annual
        assertThat(variableDataProvider.localizedPricePerMonth(rcPackage, Locale.US)).isEqualTo("$5.67")
    }

    @Test
    fun `localizedPricePerMonth provides correct price in other locales`() {
        val rcPackage = TestData.Packages.annual
        assertThat(variableDataProvider.localizedPricePerMonth(rcPackage, Locale("es", "ES"))).isEqualTo("5,67 US\$")
    }
}