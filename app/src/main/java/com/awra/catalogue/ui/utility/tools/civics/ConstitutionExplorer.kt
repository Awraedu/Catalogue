package com.awra.catalogue.ui.utility.tools.civics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList
import com.kyant.capsule.ContinuousRoundedRectangle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConstitutionExplorerContent(onClick: (UtilityItemData) -> Unit) {
    val allChapters = remember { getFullConstitution() }
    val textFieldState = rememberTextFieldState("")
    val scope = rememberCoroutineScope()
    val onSurface = MaterialTheme.colorScheme.onSurface
    
    val filteredChapters = remember(textFieldState.text) {
        val query = textFieldState.text.toString()
        if (query.isBlank()) {
            allChapters
        } else {
            allChapters.mapNotNull { chapter ->
                val filteredArticles = chapter.articles.filter { 
                    it.contains(query, ignoreCase = true) || 
                    chapter.title.contains(query, ignoreCase = true) 
                }
                if (filteredArticles.isNotEmpty() || chapter.title.contains(query, ignoreCase = true)) {
                    chapter.copy(articles = filteredArticles)
                } else {
                    null
                }
            }
        }
    }

    val searchBarState = rememberSearchBarState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Material 3 Expressive Experimental Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .semantics { isTraversalGroup = true }
        ) {
            SearchBar(
                state = searchBarState,
                inputField = {
                    SearchBarDefaults.InputField(
                        textFieldState = textFieldState,
                        searchBarState = searchBarState,
                        onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                        placeholder = { Text("Search Articles...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .semantics { traversalIndex = 0f },
                shape = CircleShape
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            filteredChapters.forEachIndexed { index, chapter ->
                chapterSection(
                    chapter = chapter,
                    onArticleClick = { articleTitle ->
                        onClick(
                            UtilityItemData(
                                title = articleTitle,
                                shortDesc = "Part of ${chapter.title}",
                                detailedExplanation = "Complete legal text for $articleTitle."
                            )
                        )
                    },
                    accentColor = onSurface
                )
                
                if (index < filteredChapters.size - 1) {
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

private fun LazyListScope.chapterSection(
    chapter: ConstitutionChapter,
    onArticleClick: (String) -> Unit,
    accentColor: Color
) {
    item(key = "ch_${chapter.title}") {
        Text(
            text = chapter.title,
            style = MaterialTheme.typography.titleSmall,
            color = accentColor,
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
        )
    }

    itemsIndexed(
        items = chapter.articles,
        key = { _, article -> "art_${chapter.title}_$article" }
    ) { index, article ->
        ArticleItem(
            title = article,
            index = index,
            count = chapter.articles.size,
            onClick = { onArticleClick(article) },
            accentColor = accentColor
        )
        
        if (index < chapter.articles.size - 1) {
            Spacer(modifier = Modifier.height(ListItemDefaults.SegmentedGap))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ArticleItem(
    title: String,
    index: Int,
    count: Int,
    onClick: () -> Unit,
    accentColor: Color
) {
    val outerRadius = 32.dp
    val innerRadius = 8.dp
    val itemShape = remember(index, count) {
        when {
            count == 1 -> ContinuousRoundedRectangle(outerRadius)
            index == 0 -> RoundedCornerShape(topStart = outerRadius, topEnd = outerRadius, bottomStart = innerRadius, bottomEnd = innerRadius)
            index == count - 1 -> RoundedCornerShape(bottomStart = outerRadius, bottomEnd = outerRadius, topStart = innerRadius, topEnd = innerRadius)
            else -> RoundedCornerShape(innerRadius)
        }
    }

    SegmentedListItem(
        onClick = onClick,
        shapes = ListItemDefaults.shapes(shape = itemShape),
        leadingContent = {
            Icon(Icons.Default.Gavel, contentDescription = null, tint = accentColor)
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private data class ConstitutionChapter(
    val title: String,
    val description: String,
    val articles: List<String>
)

private fun getFullConstitution(): List<ConstitutionChapter> {
    return listOf(
        ConstitutionChapter(
            "Chapter 1: The State",
            "Nomenclature, territory, flag, and language.",
            listOf(
                "Article 1: Nomenclature of the State",
                "Article 2: Ethiopian Territorial Jurisdiction",
                "Article 3: The Ethiopian Flag",
                "Article 4: National Anthem",
                "Article 5: Languages",
                "Article 6: Nationality",
                "Article 7: Gender Reference"
            )
        ),
        ConstitutionChapter(
            "Chapter 2: Fundamental Principles",
            "Sovereignty, supremacy, and secularism.",
            listOf(
                "Article 8: Sovereignty of the People",
                "Article 9: Supremacy of the Constitution",
                "Article 10: Human and Democratic Rights",
                "Article 11: Separation of State and Religion",
                "Article 12: Conduct and Accountability of Government"
            )
        ),
        ConstitutionChapter(
            "Chapter 3: Human Rights",
            "Individual liberties and security.",
            listOf(
                "Article 13: Scope and Interpretation",
                "Article 14: Rights to life, liberty and security",
                "Article 15: Right to life",
                "Article 16: Right to Liberty",
                "Article 17: Right to security",
                "Article 18: Prohibition against Inhuman Treatment",
                "Article 19: Right of Persons Arrested",
                "Article 20: Rights of Persons Accused",
                "Article 21: The Rights of Persons Held in Custody and Convicted Prisoners",
                "Article 22: Non-retroactivity of Criminal Law",
                "Article 23: Prohibition of Double Jeopardy",
                "Article 24: Right to Honour and Reputation",
                "Article 25: Right to Equality",
                "Article 26: Right to Privacy",
                "Article 27: Freedom of Religion, Belief and Opinion",
                "Article 28: Crimes Against Humanity"
            )
        ),
        ConstitutionChapter(
            "Chapter 3 (Cont.): Democratic Rights",
            "Group rights and civic freedoms.",
            listOf(
                "Article 29: Right of Thought, Opinion and Expression",
                "Article 30: The Right of Assembly, Demonstration and Petition",
                "Article 31: Freedom of Association",
                "Article 32: Freedom of Movement",
                "Article 33: Rights of Nationality",
                "Article 34: Marital, Personal and Family Rights",
                "Article 35: Rights of Women",
                "Article 36: Rights of Children",
                "Article 37: Right of Access to Justice",
                "Article 38: The Right to Vote and to be Elected",
                "Article 39: Rights of Nations, Nationalities, and Peoples",
                "Article 40: Right to Property",
                "Article 41: Economic, Social and Cultural Rights",
                "Article 42: Rights of Labour",
                "Article 43: The Right to Development",
                "Article 44: Environmental Rights"
            )
        ),
        ConstitutionChapter(
            "Chapter 4: State Structure",
            "Federalism and member states.",
            listOf(
                "Article 45: Form of Government",
                "Article 46: States of the Federation",
                "Article 47: Member States of the FDRE",
                "Article 48: State Border Changes",
                "Article 49: Capital City (Addis Ababa)"
            )
        ),
        ConstitutionChapter(
            "Chapter 5: Division of Powers",
            "Federal vs. State functions.",
            listOf(
                "Article 50: Structure of the Organs of State",
                "Article 51: Powers and Functions of the Federal Government",
                "Article 52: Powers and Functions of States"
            )
        ),
        ConstitutionChapter(
            "Chapter 6: The Federal Houses",
            "Legislative authority (HPR and HoF).",
            listOf(
                "Article 53: The Federal Houses",
                "Article 54: Members of the HPR",
                "Article 55: Powers and Functions of the HPR",
                "Article 56: Political Power",
                "Article 57: Adoption of Laws",
                "Article 58: Meetings of the House; Duration of its Term",
                "Article 59: Decisions and Rules of Procedure",
                "Article 60: Dissolution of the House",
                "Article 61: Members of the HoF",
                "Article 62: Powers and Functions of the HoF",
                "Article 63: Immunity of Members of the HoF",
                "Article 64: Decisions and Rules of Procedure",
                "Article 65: Budget",
                "Article 66: Powers of the Speaker of the House",
                "Article 67: Sessions and Term of the House",
                "Article 68: Prohibition of Simultaneous Membership"
            )
        ),
        ConstitutionChapter(
            "Chapter 7: The President",
            "The Head of State.",
            listOf(
                "Article 69: The President",
                "Article 70: Nomination and Appointment of the President",
                "Article 71: Powers and Functions of the President"
            )
        ),
        ConstitutionChapter(
            "Chapter 8: The Executive",
            "The Prime Minister and Council.",
            listOf(
                "Article 72: The Powers of the Executive",
                "Article 73: Appointment of the Prime Minister",
                "Article 74: Powers and Functions of the Prime Minister",
                "Article 75: The Deputy Prime Minister",
                "Article 76: The Council of Ministers",
                "Article 77: Powers and Functions of the Council of Ministers"
            )
        ),
        ConstitutionChapter(
            "Chapter 9: The Judiciary",
            "Structure and powers of courts.",
            listOf(
                "Article 78: Independence of the Judiciary",
                "Article 79: Judicial Powers",
                "Article 80: Concurrent Jurisdiction of Courts",
                "Article 81: Appointment of Judges",
                "Article 82: Structure of the Council of Constitutional Inquiry",
                "Article 83: Powers and Functions of the Council of Constitutional Inquiry",
                "Article 84: Powers and Functions of the Council of Constitutional Inquiry"
            )
        ),
        ConstitutionChapter(
            "Chapter 10: Policy Objectives",
            "National goals and principles.",
            listOf(
                "Article 85: Objectives",
                "Article 86: Principles for External Relations",
                "Article 87: Principles for National Defence",
                "Article 88: Political Objectives",
                "Article 89: Economic Objectives",
                "Article 90: Social Objectives",
                "Article 91: Cultural Objectives",
                "Article 92: Environmental Objectives"
            )
        ),
        ConstitutionChapter(
            "Chapter 11: Miscellaneous",
            "Emergencies, finance, and census.",
            listOf(
                "Article 93: Declaration of State of Emergency",
                "Article 94: Financial Expenditures",
                "Article 95: Revenue",
                "Article 96: Federal Power of Taxation",
                "Article 97: State Power of Taxation",
                "Article 98: Concurrent Power of Taxation",
                "Article 99: Undesignated Powers of Taxation",
                "Article 100: Directives on Taxation",
                "Article 101: The Auditor General",
                "Article 102: Election Board",
                "Article 103: Population Census Commission",
                "Article 104: Initiation of Amendments",
                "Article 105: Amendment of the Constitution",
                "Article 106: The Version with Final Legal Authority"
            )
        )
    )
}
