package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.WhatsAppCallActionRow
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriGoldHarvest
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary

@Composable
fun AboutUsScreen(
    onBookNowClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .testTag("about_us_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AgriGreenDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pappu_tractor),
                        contentDescription = "Pappu Tractor Logo",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "PAPPU TRACTOR",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Text(
                        text = "पप्पू ट्रैक्टर कृषि सेवाएं • किसानों का सच्चा साथी",
                        fontSize = 13.sp,
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Introduction
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "हमारा परिचय (About Pappu Tractor)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "पप्पू ट्रैक्टर कृषि सेवा केंद्र पिछले 15 वर्षों से ग्रामीण क्षेत्र के अन्नदाताओं की सेवा में समर्पित है। हमारा उद्देश्य छोटे और बड़े सभी किसान भाइयों तक उन्नत कृषि तकनीक और आधुनिक मशीनरी को उचित और किफायती दरों पर पहुंचाना है।",
                        fontSize = 13.sp,
                        color = Color(0xFF374151),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "पारंपरिक खेती में लगने वाले भारी श्रम, समय और लागत को कम करके किसान भाइयों की उपज और मुनाफा बढ़ाना ही हमारा परम लक्ष्य है।",
                        fontSize = 13.sp,
                        color = Color(0xFF374151),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Agriculture Services Info
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "हमारी कृषि सेवाएं (Our Agriculture Services)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ServiceBullet(
                        title = "1. शक्तिशाली न्यू हॉलैंड ट्रैक्टर सेवा:",
                        desc = "गहरी जुताई, कल्टीवेटर, समतलीकरण (लेवलर) तथा भारी कृषि ढुलाई कार्य।"
                    )

                    ServiceBullet(
                        title = "2. डीलक्स थ्रेशर 50 HP (सुपरफास्ट गहाई):",
                        desc = "गेहूं, सरसों, चना आदि फसलों की 100% साफ दाना निकासी व पशुओं के लिए उत्तम मुलायम भूसा निर्माण।"
                    )

                    ServiceBullet(
                        title = "3. निफा रोटावेटर जुताई:",
                        desc = "एक ही चक्कर में मिट्टी को भुरभुरा बनाना, फसल अवशेषों को खाद में बदलना और खेत बुवाई हेतु तैयार करना।"
                    )

                    ServiceBullet(
                        title = "4. स्मॉल राइस मिल (मिनी धान कुटाई):",
                        desc = "धान से बिना टूटे उच्च गुणवत्ता का पॉलिश किया हुआ सफेद चावल निकालने की घर पहुंच सुविधा।"
                    )

                    ServiceBullet(
                        title = "5. आधुनिक गेहूं कटाई मशीन (रीपर):",
                        desc = "जमीन की सतह से सटकर तेजी से कटाई, समय और मजदूरी की भारी बचत।"
                    )
                }
            }
        }

        // Why Choose Us
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "पप्पू ट्रैक्टर ही क्यों चुनें?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )

                    ReasonRow(title = "समयबद्ध सेवा", desc = "तय समय पर खेत में मशीन व चालक की मौजूदगी")
                    ReasonRow(title = "पारदर्शी व उचित दर", desc = "कोई छिपा हुआ शुल्क नहीं, सभी के लिए समान भाव")
                    ReasonRow(title = "प्रशिक्षित चालक", desc = "कुशल व अनुभवी ऑपरेटर द्वारा सावधानीपूर्वक काम")
                    ReasonRow(title = "त्वरित ग्राहक सहायता", desc = "फोन व व्हाट्सएप पर तत्काल प्रतिक्रिया")
                }
            }
        }

        // Action CTA
        item {
            Button(
                onClick = onBookNowClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("about_book_now_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("अभी मशीन बुक करें (Book a Service)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        item {
            WhatsAppCallActionRow(
                customMessage = "नमस्ते पप्पू ट्रैक्टर, मुझे आपके सेवा केंद्र के बारे में जानकारी चाहिए।"
            )
        }
    }
}

@Composable
private fun ServiceBullet(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AgriGreenDark
        )
        Text(
            text = desc,
            fontSize = 12.sp,
            color = Color(0xFF4B5563),
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun ReasonRow(title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = AgriGreenPrimary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AgriGreenDark
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = Color(0xFF374151)
            )
        }
    }
}
