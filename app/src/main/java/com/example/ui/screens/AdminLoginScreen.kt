package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    adminError: String?,
    onLoginClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .padding(20.dp)
            .testTag("admin_login_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AgriGreenDark)
            }
            Text(
                text = "व्यवस्थापक पोर्टल (Admin Portal)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AgriGreenDark
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Security Notice Banner (Strict Rule Compliance)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "सुरक्षा सूचना (Security Architecture Note):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "यह स्क्रीन केवल अग्रिम UI प्रोटोटाइप है। उत्पादन (Production) में वास्तविक व्यवस्थापक प्रमाणीकरण सुरक्षित सर्वर-साइड टोकन व बैकएंड ऑथेंटिकेशन द्वारा संरक्षित होगा। केवल हिडन फ्रंटएंड पर्याप्त सुरक्षा नहीं है।",
                        fontSize = 11.sp,
                        color = Color(0xFF5D4037),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = AgriGreenPrimary,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = "पप्पू ट्रैक्टर व्यवस्थापक लॉगिन",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )

                Text(
                    text = "बुकिंग प्रबंधन, मशीन दर संपादन और स्थिति नियंत्रण हेतु 4 अंकों का सुरक्षा पिन दर्ज करें:",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 8) pin = it },
                    label = { Text("सुरक्षा पिन / पासवर्ड") },
                    placeholder = { Text("पिन दर्ज करें (उदा. 7388)") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = AgriGreenPrimary)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                if (adminError != null) {
                    Text(
                        text = adminError,
                        color = Color(0xFFD32F2F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = { onLoginClick(pin) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_login_submit_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("डैशबोर्ड में प्रवेश करें (Enter Dashboard)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AgriGreenContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 प्रोटोटाइप डेमो पिन: 7388 या 1234",
                        fontSize = 11.sp,
                        color = AgriGreenDark,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
