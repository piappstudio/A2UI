package com.piappstudio.a2ui.a2ui.catalog

import android.content.Context
import androidx.a2ui.compose.ui.A2uiCatalog
import androidx.a2ui.compose.ui.catalog.A2uiBasicCatalogV1
import androidx.a2ui.model.protocol.A2uiExecutionContext
import androidx.compose.material3.Text
import androidx.compose.material3.a2ui.catalog.A2uiAudioPlayerRenderer
import androidx.compose.material3.a2ui.catalog.A2uiImageRenderer
import androidx.compose.material3.a2ui.catalog.A2uiVideoRenderer
import androidx.compose.material3.a2ui.catalog.MaterialA2uiBasicCatalogV1Defaults
import androidx.compose.material3.a2ui.catalog.materialA2uiBasicCatalogV1
import coil.compose.AsyncImage
import com.piappstudio.a2ui.security.SafeUrlPolicy

fun createBankingCatalog(context: Context): A2uiCatalog {
    val appContext = context.applicationContext
    val defaults = A2uiBasicCatalogV1.Functions(appContext)
    val functions = object : A2uiBasicCatalogV1.Functions by defaults {
        override fun openUrl(url: String, executionContext: A2uiExecutionContext) {
            if (SafeUrlPolicy.isAllowed(url)) {
                defaults.openUrl(url, executionContext)
            }
        }
    }
    val basic = materialA2uiBasicCatalogV1(
        image = MaterialA2uiBasicCatalogV1Defaults.image(
            A2uiImageRenderer { url, contentDescription, contentScale, modifier, onError ->
                if (SafeUrlPolicy.isAllowed(url)) {
                    AsyncImage(
                        model = url,
                        contentDescription = contentDescription,
                        contentScale = contentScale,
                        modifier = modifier,
                        onError = { state -> onError(state.result.throwable) },
                    )
                } else {
                    Text("Image blocked")
                    onError(SecurityException("Blocked image URL"))
                }
            },
        ),
        video = MaterialA2uiBasicCatalogV1Defaults.video(
            A2uiVideoRenderer { _, _, onError ->
                Text("Video is not enabled in this demo.")
                onError(null)
            },
        ),
        audioPlayer = MaterialA2uiBasicCatalogV1Defaults.audioPlayer(
            A2uiAudioPlayerRenderer { _, _, _, onError ->
                Text("Audio is not enabled in this demo.")
                onError(null)
            },
        ),
        functions = functions,
    )
    return A2uiCatalog(
        catalogId = BANKING_CATALOG_ID,
        components = basic.components + listOf(
            EmiSummaryComponent,
            ExchangeRateSummaryComponent,
            VerificationStatusComponent,
            TransactionLocationCardComponent,
            DocumentAttachmentComponent,
        ),
        functions = basic.functions,
        themeSchema = basic.themeSchema,
        isInline = true,
    )
}
