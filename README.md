# Catalogue - Ethiopian Freshman Study Companion

Catalogue is a modern, expressive Android application designed to help Ethiopian university students excel in their freshman common courses. The app provides a comprehensive suite of study tools, including standardized module handouts, interactive practice quizzes, tactical flashcards, and a specialized academic dictionary.

## 🚀 Features

### 📚 Learning Materials
- **Standardized Handouts**: Detailed study materials for all common freshman courses including Communicative English, Psychology, Anthropology, Logic, Civics, Physics, Chemistry, Biology, Mathematics, Geography, History, Economics, and Sociology.
- **Chapter-wise Organization**: Content is structured exactly according to the Ministry of Education's standardized freshman curriculum.

### 🧠 Interactive Study Tools
- **Customizable Quizzes**: 
  - Choose your difficulty: Easy, Medium, or Hard.
  - Set the number of questions (up to 100).
  - Select specific chapters to focus your practice.
  - Get instant feedback with detailed "Why?" explanations.
- **tactile Flashcards**:
  - 3D-flipping cards with realistic spring physics.
  - Front/Back interaction for active recall.
  - Filter by difficulty and units.
- **Academic Dictionary (Terms)**:
  - Searchable list of key concepts and their formal definitions.
  - Unique high-contrast expressive shapes for quick scanning.
  - Global access via the Home FAB.
  - Bookmarking system for quick revision.

### ✨ Modern Design & UX
- **Adaptive UI**: High-expressivity theme that adapts its color palette based on the subject you are studying.
- **Material 3 Expressive**: Utilizes the latest Material 3 components, including segmented buttons, split buttons, and expressive shapes (Pills, Hearts, Cookies, etc.).
- **Neutral Mode**: Study tools and configuration popups use a clean neutral mode for maximum focus and contrast.

## 🛠️ Technology Stack
- **Language**: 100% Kotlin
- **UI**: Jetpack Compose with Material 3 Expressive API
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Persistence Library with Flow support
- **Navigation**: State-driven dynamic navigation
- **Animations**: Compose Animation (Spring, Graphics Layer, animateDpAsState)

## 📖 Curriculum Coverage
The app currently seeds data for the following modules:
- Communicative English Skills I & II
- General Psychology & Life Skills
- Social Anthropology
- Logic and Critical Thinking
- Moral and Civic Education
- General Physics
- General Chemistry
- General Biology
- Mathematics for Natural Sciences
- Geography of Ethiopia and the Horn
- History of Ethiopia and the Horn
- Introduction to Economics
- Introduction to Sociology

## 🛠️ Setup
1. Clone the repository.
2. Open in **Android Studio Ladybug (2024.2.1)** or newer.
3. Sync Gradle and run the `:app` module.
4. On first launch, the database will automatically seed with the complete curriculum.

## 📄 License
MIT License. See `LICENSE` for details.
