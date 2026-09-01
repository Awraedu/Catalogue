package com.awra.catalogue.ui.utility.tools.geography

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun CountryDetailSheet(item: UtilityItemData) {
    val colorScheme = MaterialTheme.colorScheme
    val rawId = item.shortDesc.removePrefix("ISO: ")
    val isoCode = (nameToIso[rawId] ?: nameToIso[item.title] ?: rawId).uppercase()
    val flagEmoji = getFlagEmoji(isoCode)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(28.dp),
            color = colorScheme.primaryContainer,
            tonalElevation = 6.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (flagEmoji.isNotEmpty()) {
                    Text(flagEmoji, fontSize = 48.sp)
                } else {
                    Icon(
                        Icons.Default.Public,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Surface(
            color = colorScheme.secondaryContainer.copy(alpha = 0.5f),
            shape = CircleShape,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = "ISO Code: $isoCode",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSecondaryContainer
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = colorScheme.surfaceContainerHigh
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Geographic Info",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "This interactive map allows you to explore the borders and relative locations of nations across the globe.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

fun getFlagEmoji(isoCode: String): String {
    if (isoCode.length != 2) return ""
    val firstChar = Character.codePointAt(isoCode, 0) - 0x41 + 0x1F1E6
    val secondChar = Character.codePointAt(isoCode, 1) - 0x41 + 0x1F1E6
    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}

private val nameToIso = mapOf(
    "Afghanistan" to "AF",
    "Albania" to "AL",
    "Algeria" to "DZ",
    "Andorra" to "AD",
    "Angola" to "AO",
    "Antigua and Barbuda" to "AG",
    "Argentina" to "AR",
    "Armenia" to "AM",
    "Australia" to "AU",
    "Austria" to "AT",
    "Azerbaijan" to "AZ",
    "Bahamas" to "BS",
    "Bahrain" to "BH",
    "Bangladesh" to "BD",
    "Barbados" to "BB",
    "Belarus" to "BY",
    "Belgium" to "BE",
    "Belize" to "BZ",
    "Benin" to "BJ",
    "Bhutan" to "BT",
    "Bolivia" to "BO",
    "Bosnia and Herzegovina" to "BA",
    "Botswana" to "BW",
    "Brazil" to "BR",
    "Brunei" to "BN",
    "Brunei Darussalam" to "BN",
    "Bulgaria" to "BG",
    "Burkina Faso" to "BF",
    "Burundi" to "BI",
    "Cabo Verde" to "CV",
    "Cambodia" to "KH",
    "Cameroon" to "CM",
    "Canada" to "CA",
    "Central African Republic" to "CF",
    "Chad" to "TD",
    "Chile" to "CL",
    "China" to "CN",
    "Colombia" to "CO",
    "Comoros" to "KM",
    "Congo" to "CG",
    "Republic of Congo" to "CG",
    "Congo, Democratic Republic of the" to "CD",
    "Democratic Republic of the Congo" to "CD",
    "Costa Rica" to "CR",
    "Côte d'Ivoire" to "CI",
    "Ivory Coast" to "CI",
    "Croatia" to "HR",
    "Cuba" to "CU",
    "Cyprus" to "CY",
    "Czechia" to "CZ",
    "Czech Republic" to "CZ",
    "Denmark" to "DK",
    "Djibouti" to "DJ",
    "Dominica" to "DM",
    "Dominican Republic" to "DO",
    "Ecuador" to "EC",
    "Egypt" to "EG",
    "El Salvador" to "SV",
    "Equatorial Guinea" to "GQ",
    "Eritrea" to "ER",
    "Estonia" to "EE",
    "Eswatini" to "SZ",
    "Swaziland" to "SZ",
    "Ethiopia" to "ET",
    "Fiji" to "FJ",
    "Finland" to "FI",
    "France" to "FR",
    "Gabon" to "GA",
    "Gambia" to "GM",
    "The Gambia" to "GM",
    "Georgia" to "GE",
    "Germany" to "DE",
    "Ghana" to "GH",
    "Greece" to "GR",
    "Grenada" to "GD",
    "Guatemala" to "GT",
    "Guinea" to "GN",
    "Guinea-Bissau" to "GW",
    "Guyana" to "GY",
    "Haiti" to "HT",
    "Honduras" to "HN",
    "Hungary" to "HU",
    "Iceland" to "IS",
    "India" to "IN",
    "Indonesia" to "ID",
    "Iran" to "IR",
    "Iraq" to "IQ",
    "Ireland" to "IE",
    "Israel" to "IL",
    "Italy" to "IT",
    "Jamaica" to "JM",
    "Japan" to "JP",
    "Jordan" to "JO",
    "Kazakhstan" to "KZ",
    "Kenya" to "KE",
    "Kiribati" to "KI",
    "Korea, North" to "KP",
    "North Korea" to "KP",
    "Dem. Rep. Korea" to "KP",
    "Korea, South" to "KR",
    "South Korea" to "KR",
    "Republic of Korea" to "KR",
    "Kosovo" to "XK",
    "Kuwait" to "KW",
    "Kyrgyzstan" to "KG",
    "Laos" to "LA",
    "Lao PDR" to "LA",
    "Latvia" to "LV",
    "Lebanon" to "LB",
    "Lesotho" to "LS",
    "Liberia" to "LR",
    "Libya" to "LY",
    "Liechtenstein" to "LI",
    "Lithuania" to "LT",
    "Luxembourg" to "LU",
    "Madagascar" to "MG",
    "Malawi" to "MW",
    "Malaysia" to "MY",
    "Maldives" to "MV",
    "Mali" to "ML",
    "Malta" to "MT",
    "Marshall Islands" to "MH",
    "Mauritania" to "MR",
    "Mauritius" to "MU",
    "Mexico" to "MX",
    "Micronesia" to "FM",
    "Moldova" to "MD",
    "Monaco" to "MC",
    "Mongolia" to "MN",
    "Montenegro" to "ME",
    "Morocco" to "MA",
    "Mozambique" to "MZ",
    "Myanmar" to "MM",
    "Burma" to "MM",
    "Namibia" to "NA",
    "Nauru" to "NR",
    "Nepal" to "NP",
    "Netherlands" to "NL",
    "New Zealand" to "NZ",
    "Nicaragua" to "NI",
    "Niger" to "NE",
    "Nigeria" to "NG",
    "North Macedonia" to "MK",
    "Macedonia" to "MK",
    "Norway" to "NO",
    "Oman" to "OM",
    "Pakistan" to "PK",
    "Palau" to "PW",
    "Panama" to "PA",
    "Papua New Guinea" to "PG",
    "Paraguay" to "PY",
    "Peru" to "PE",
    "Philippines" to "PH",
    "Poland" to "PL",
    "Portugal" to "PT",
    "Qatar" to "QA",
    "Romania" to "RO",
    "Russia" to "RU",
    "Russian Federation" to "RU",
    "Rwanda" to "RW",
    "Saint Kitts and Nevis" to "KN",
    "Saint Lucia" to "LC",
    "Saint Vincent and the Grenadines" to "VC",
    "Samoa" to "WS",
    "San Marino" to "SM",
    "Sao Tome and Principe" to "ST",
    "Saudi Arabia" to "SA",
    "Senegal" to "SN",
    "Serbia" to "RS",
    "Seychelles" to "SC",
    "Sierra Leone" to "SL",
    "Singapore" to "SG",
    "Slovakia" to "SK",
    "Slovenia" to "SI",
    "Solomon Islands" to "SB",
    "Somalia" to "SO",
    "South Africa" to "ZA",
    "South Sudan" to "SS",
    "Spain" to "ES",
    "Sri Lanka" to "LK",
    "Sudan" to "SD",
    "Suriname" to "SR",
    "Sweden" to "SE",
    "Switzerland" to "CH",
    "Syria" to "SY",
    "Taiwan" to "TW",
    "Tajikistan" to "TJ",
    "Tanzania" to "TZ",
    "Thailand" to "TH",
    "Timor-Leste" to "TL",
    "Togo" to "TG",
    "Tonga" to "TO",
    "Trinidad and Tobago" to "TT",
    "Tunisia" to "TN",
    "Turkey" to "TR",
    "Turkmenistan" to "TM",
    "Tuvalu" to "TV",
    "Uganda" to "UG",
    "Ukraine" to "UA",
    "United Arab Emirates" to "AE",
    "United Kingdom" to "GB",
    "United States" to "US",
    "Uruguay" to "UY",
    "Uzbekistan" to "UZ",
    "Vanuatu" to "VU",
    "Vatican City" to "VA",
    "Venezuela" to "VE",
    "Vietnam" to "VN",
    "Yemen" to "YE",
    "Zambia" to "ZM",
    "Zimbabwe" to "ZW",
    // Territories and others often found in SVGs
    "Greenland" to "GL",
    "Western Sahara" to "EH",
    "Puerto Rico" to "PR",
    "French Guiana" to "GF",
    "New Caledonia" to "NC",
    "Falkland Islands" to "FK",
    "Faroe Islands" to "FO",
    "Faeroe Islands" to "FO",
    "Guam" to "GU",
    "Martinique" to "MQ",
    "Reunion" to "RE",
    "Mayotte" to "YT",
    "Curacao" to "CW",
    "Curaçao" to "CW",
    "Aruba" to "AW",
    "Anguilla" to "AI",
    "Montserrat" to "MS",
    "British Virgin Islands" to "VG",
    "Saba (Netherlands)" to "BQ",
    "St. Eustatius (Netherlands)" to "BQ",
    "Sint Maarten" to "SX"
)
