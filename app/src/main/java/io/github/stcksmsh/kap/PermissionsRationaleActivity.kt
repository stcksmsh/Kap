package io.github.stcksmsh.kap

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.ui.theme.AppTheme

class PermissionsRationaleActivity : ComponentActivity() {

    // Point to your public privacy policy page
    private val privacyPolicyUrl = "https://yourdomain.example/privacy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface {
                    PrivacyRationaleScreen(
                        onOpenFullPolicy = {
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl)))
                            } catch (_: Exception) {}
                        },
                        onClose = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivacyRationaleScreen(
    onOpenFullPolicy: () -> Unit,
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Politika privatnosti") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Zašto tražimo pristup Health Connect-u",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                """
Kap koristi Health Connect da bi čitao i upisivao podatke o hidrataciji (količina i vreme unosa).
Ovi podaci se koriste da:
• sinhronizuju unos vode sa drugim aplikacijama,
• preciznije prate vaše dnevne ciljeve,
• prikažu uvide o hidrataciji.

Kako postupamo s podacima:
• Podaci se koriste isključivo u okviru aplikacije Kap.
• Ne prodajemo podatke i ne delimo ih sa trećim stranama, osim kroz Health Connect, po vašem odobrenju.
• U svakom trenutku možete opozvati dozvole u aplikaciji Kap ili u aplikaciji Health Connect.
• Možete obrisati podatke u Kap-u i/ili u Health Connect-u kad god poželite.
""".trimIndent(),
                textAlign = TextAlign.Start
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = onOpenFullPolicy) {
                    Text("Pogledaj kompletnu politiku")
                }
                Spacer(Modifier.weight(1f))
                Button(onClick = onClose) {
                    Text("Zatvori")
                }
            }
        }
    }
}
