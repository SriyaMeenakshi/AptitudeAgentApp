# Aptitude Agent - Detective Edition

<div align="center">

🕵️ **Solve Cases Using Your Aptitude Skills** 🕵️

*A gamified aptitude learning app with AI-powered hints and detective storytelling*

</div>

## 🎯 Overview

**Aptitude Agent - Detective Edition** transforms aptitude test preparation into an immersive
detective mystery game. As a trainee detective, you solve cases by answering aptitude questions that
serve as clues. Each correct answer brings you closer to cracking the case!

## ✨ Key Features

### 🎮 Game Mechanics

- **Case-Based Learning**: Each case requires solving 5 aptitude clues
- **Progressive Difficulty**: From Rookie to Chief Inspector level cases
- **XP & Leveling System**: Earn experience points and rank up
- **Badge Achievements**: Unlock special badges for completing challenging cases
- **Daily Mystery**: Solve a new challenge every day for bonus XP
- **Streak Tracking**: Build and maintain your solving streak

### 🤖 AI-Powered Learning (RunAnywhere SDK)

- **AI Chief Detective**: Your personal AI mentor powered by on-device LLM
- **Contextual Hints**: Get detective-style hints for difficult questions
- **Detailed Explanations**: AI-generated case analysis for each answer
- **Interactive Chat**: Ask the Chief Detective for help anytime
- **Adaptive Difficulty**: Questions that match your skill level

### 📚 Aptitude Categories

- **Logical Reasoning**: Deductive thinking and pattern analysis
- **Numerical Ability**: Mathematical problem solving
- **Verbal Reasoning**: Language comprehension and analogies
- **Pattern Recognition**: Sequence and series completion
- **Data Interpretation**: Analyze evidence and statistics
- **Analytical Thinking**: Complex problem decomposition

### 🎨 Detective Theme

- **Dark Mode Design**: Immersive noir detective atmosphere
- **Neon Accents**: Cyan (#00EAFF) and Purple (#B600F0) highlights
- **Mystery Aesthetics**: Detective silhouettes, case files, evidence boards
- **Smooth Animations**: Engaging transitions and loading states
- **Professional UI**: Clean Jetpack Compose Material 3 design

## 📱 Screens

### 1. **Splash Screen**

- Animated detective silhouette
- Loading dots animation
- Brand introduction

### 2. **Home Screen (Detective HQ)**

- Welcome message with rank
- XP progress bar
- Main menu cards:
    - 📂 Case Files (Level Selection)
    - 🎯 Daily Mystery
    - 🤖 AI Chief Detective
- Stats dashboard: Level, Cases Solved, Streak

### 3. **Case Files Screen**

- List of all available cases
- Locked/unlocked states
- Difficulty badges (Rookie/Detective/Inspector/Chief)
- Progress tracking for active cases
- XP rewards and badge information

### 4. **Quiz Screen** (Main Gameplay)

- Case progress indicator
- Aptitude question as detective clue
- Multiple choice answers (A, B, C, D)
- Hint system with AI integration
- Real-time answer validation
- Detailed explanations after submission
- Visual feedback (correct/incorrect)

### 5. **Case Solved Screen**

- Victory animation
- XP earned breakdown
- New level/rank notification
- Badge achievements
- Case summary report
- Navigation to next case or HQ

### 6. **AI Chief Detective Screen**

- Chat interface with AI mentor
- Model download and loading
- Real-time streaming responses
- Tips and guidance section
- Detective-style conversation

### 7. **Daily Mystery Screen**

- Special daily challenge
- Bonus XP rewards
- Streak maintenance
- One attempt per day

### 8. **Profile Screen**

- Detective profile card
- Level and XP progress
- Statistics grid (Cases, Streak, Completion %, Badges)
- Earned badges showcase
- Case history

## 🛠️ Technical Architecture

### Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with ViewModel
- **Navigation**: Jetpack Navigation Compose
- **AI SDK**: RunAnywhere SDK with llama.cpp
- **State Management**: Kotlin Flows & StateFlow
- **Animations**: Compose Animation APIs
- **Material Design**: Material 3

### Project Structure

```
com.runanywhere.startup_hackathon20/
├── model/
│   └── DetectiveModels.kt          # Data classes for game entities
├── viewmodel/
│   ├── GameViewModel.kt            # Game state and logic
│   └── AIViewModel.kt              # AI integration and SDK management
├── ui/
│   ├── screens/
│   │   ├── SplashScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── CaseFilesScreen.kt
│   │   ├── QuizScreen.kt
│   │   ├── CaseSolvedScreen.kt
│   │   ├── AIChiefScreen.kt
│   │   ├── DailyMysteryScreen.kt
│   │   └── ProfileScreen.kt
│   └── theme/
│       ├── Color.kt                # Detective color palette
│       └── Theme.kt                # Dark theme configuration
├── navigation/
│   └── Navigation.kt               # Navigation graph and routes
├── MainActivity.kt                 # Entry point
└── MyApplication.kt                # SDK initialization
```

### ViewModels

#### GameViewModel

Manages core game state:

- User profile (XP, level, rank, badges)
- Case progression
- Question generation
- Answer validation
- Daily mystery
- Achievement tracking

#### AIViewModel

Handles AI features via RunAnywhere SDK:

- Model download and loading
- Hint generation
- Explanation generation
- Question generation (future feature)
- Chat with AI Chief Detective

## 🚀 Quick Start

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 24+ (Android 7.0+)
- ~400 MB storage for AI model
- Internet connection for model download

### Installation Steps

1. **Clone the Repository**

```bash
git clone <repository-url>
cd Hackss
```

2. **Open in Android Studio**

- Open Android Studio
- Select "Open an Existing Project"
- Navigate to the project folder

3. **Build the Project**

```bash
./gradlew build
```

4. **Run on Device/Emulator**

- Click Run button or press Shift+F10
- Select target device
- Wait for installation

### First-Time Setup

1. **Launch App**: Open "Aptitude Agent"
2. **AI Chief Setup**:
    - Navigate to "AI Chief Detective" from home
    - Tap "Setup" in top right
    - Tap "Download" on the AI model (374 MB)
    - Wait for download to complete
    - Tap "Load" to activate AI Chief
3. **Start Playing**: Return to home and select "Case Files"
4. **Choose First Case**: Tap "The Missing Logic" (Case #1)
5. **Solve Clues**: Answer 5 questions to complete the case

## 🎓 How to Play

### Solving a Case

1. Select an unlocked case from Case Files
2. Read the aptitude question (clue)
3. Choose from 4 answer options (A, B, C, D)
4. Tap "Submit Answer"
5. View explanation and continue
6. Solve 5 clues to close the case

### Using Hints

- Tap "💡 Need a Hint?" during a question
- AI Chief provides subtle guidance
- Each hint used reduces bonus XP

### Leveling Up

- Earn 100 XP per level
- XP sources:
    - Complete cases (100-500 XP)
    - Daily mystery (50 XP)
    - No-hint bonus (+50 XP)
- Rank progression:
    - Level 1-4: Trainee Detective
    - Level 5-9: Junior Detective
    - Level 10-19: Detective
    - Level 20-29: Senior Detective
    - Level 30-39: Inspector
    - Level 40+: Chief Inspector

## 🎨 Color Palette

```kotlin
// Primary Colors
NeonBlue = #00EAFF      // Primary accent, buttons, progress
NeonPurple = #B600F0    // Secondary accent, badges
DarkBackground = #0A0E1A // Main background
DarkSurface = #131824    // Card backgrounds
DarkCard = #1A202E       // Elevated surfaces
DarkAccent = #242D3F     // Subtle elements

// Status Colors
SuccessGreen = #00FF94   // Correct answers
WarningYellow = #FFB800  // Badges, warnings
ErrorRed = #FF3860       // Wrong answers

// Text Colors
TextPrimary = #FFFFFF    // Main text
TextSecondary = #B0B8C8  // Secondary text
TextTertiary = #6B7280   // Disabled text
```

## 🔧 Configuration

### Adjusting Difficulty

Edit `GameViewModel.kt` → `generateInitialCases()` to modify:

- Number of clues per case
- XP rewards
- Difficulty progression

### Adding More Questions

Edit `GameViewModel.kt` → `generateClue()` functions to add custom questions for each category.

### Customizing AI Prompts

Edit `AIViewModel.kt` → `build*Prompt()` functions to modify:

- Hint style
- Explanation format
- Chief Detective personality

## 📊 Dependencies

```kotlin
// RunAnywhere SDK
implementation(files("libs/RunAnywhereKotlinSDK-release.aar"))
implementation(files("libs/runanywhere-llm-llamacpp-release.aar"))

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.6")

// Compose & Material 3
implementation(platform("androidx.compose:compose-bom:2024.x.x"))
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
```

## 🐛 Troubleshooting

### AI Chief Not Loading

- Ensure model is downloaded (check storage)
- Verify internet connection during download
- Check `largeHeap="true"` in AndroidManifest.xml
- Close other apps to free memory

### Slow AI Responses

- Normal for on-device inference
- Performance depends on device CPU
- Typical response time: 5-15 seconds

### App Crashes

- Update Android System WebView
- Clear app cache
- Reinstall the app
- Check device has sufficient storage

### Questions Not Loading

- Restart the app
- Check GameViewModel initialization
- Review logcat for errors

## 🎯 Future Enhancements

### Planned Features

- [ ] User accounts with cloud sync
- [ ] Multiplayer competitive mode
- [ ] Custom case creator
- [ ] More question categories
- [ ] Voice narration
- [ ] Augmented reality clue finding
- [ ] Leaderboards
- [ ] Social sharing
- [ ] In-app achievements
- [ ] Weekly challenges

### AI Improvements

- [ ] Dynamic question generation
- [ ] Personalized difficulty adjustment
- [ ] Natural conversation flow
- [ ] Voice interaction
- [ ] Multi-language support

## 📄 License

This project uses the RunAnywhere SDK. See SDK documentation for licensing details.

## 🤝 Contributing

Contributions are welcome! Areas for improvement:

- Add more aptitude questions
- Design new cases
- Improve UI animations
- Optimize AI prompts
- Bug fixes

## 📧 Support

For issues or questions:

- Check existing GitHub issues
- Create new issue with detailed description
- Include device info and logs

## 🙏 Acknowledgments

- **RunAnywhere SDK**: On-device AI inference
- **Material Design**: UI components
- **Jetpack Compose**: Modern Android UI
- **llama.cpp**: Efficient LLM inference

---

<div align="center">

**Made with ❤️ for aspiring detectives and aptitude learners**

🕵️ Happy Investigating! 🕵️

</div>
