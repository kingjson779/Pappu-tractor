package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AgriRepository
import com.example.ui.theme.CallBlue
import com.example.ui.theme.WhatsAppGreen
import com.example.util.ContactUtils

@Composable
fun WhatsAppCallActionRow(
    modifier: Modifier = Modifier,
    customMessage: String = "नमस्ते पप्पू ट्रैक्टर, मुझे कृषि यंत्र सेवा के बारे में जानकारी चाहिए।"
) {
    val context = LocalContext.current
    val whatsAppNumber by AgriRepository.businessWhatsApp.collectAsState()
    val phoneNumber by AgriRepository.businessPhone.collectAsState()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // WhatsApp Button
        Button(
            onClick = {
                ContactUtils.openWhatsApp(
                    context = context,
                    phoneNumber = whatsAppNumber,
                    message = customMessage
                )
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("whatsapp_action_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = WhatsAppGreen,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "WhatsApp",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "व्हाट्सएप करें",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Phone Call Button
        Button(
            onClick = {
                ContactUtils.dialPhoneNumber(context, phoneNumber)
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("call_action_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CallBlue,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "सीधे कॉल करें",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
