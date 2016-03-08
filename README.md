##What is TypeShift?

TypeShift is a series of modules written over the past few years in collaboration between CUNY, NYIT and Louisiana Tech. These modules are used to extract and modify feature sets pulled from a proprietary test data set of keystroke dynamics. These feature sets can then be analyzed for the purposes of user authentication.

##The Data

No data is contained in this repository due to intellectual property liscensing. However, a description of the data is:

The typing data was collected from 486 Louisiana Tech University students (hereinafter referred to as "subjects") in two sessions, 6 months apart. Subjects received unique IDs identifying them across sessions, and we collected a number of self-reported demographics for each subject.

The subjects were 41% female to 59% male. 82% are L1 English speakers, while 17% are non-native English speakers. Finally, 88% of subjects report being right-hand dominant, while 9% report being left-handed.

The mean typing rate was 168.9 intraword keystrokes per minute with a standard deviation of 50.04.

In each session, subjects were presented with a set of 12 prompts to respond to (from a set of 36). The order that the prompts were presented was randomized. There is no overlap of prompts across sessions.

Examples of prompts include:

- List the recent movies you've seen or books you've read. When did you see or read them? What were they about?

- How would you design a class if you were the teacher? What subject would you teach? How would you structure your course?

Each subject was required to type at least 300 characters in response to each prompt, but was given unlimited time. The average response contains 448 characters and 87 words. Our system recorded the timing associated with the subjects' keystrokes, and cursor position.

##src Folder
The src folder contains all of the existing modules that have been written. These modules are divided among the two institutions where they were written. In the subfolders of **cuny** and **nyit** you will find the various modules. The modules and their intended purpose is outlined below.

##CUNY Modules

**Demographics**
Cog Load 2 Way
Cog Load 3 Way
Cog Load 6 Way
Gender
Handedness
Primary Language
Query All
Subject ID

**Demographics Extractors**
Demographic Extractor
Handedness

**Keyboard**
Canonical Finger
Character Type Transition
Finger Hand Hold
Finger Hand Speed
Finger Hand Transition Speed
Finger Hold
Finger Row Transition Speed
Finger Speed
Finger Transition Speed
Hand Finger To Row Transition Speed
Hand Hold
Hand Speed
Hand to Hand Row Speed
Hand to Row Speed
Hand Transition Speed
Lag Times
Row Finger Transition Speed
Row Hand Hold
Row Hand Speed
Row Hand to Hand Speed
Rown Hand to Row Hand Speed
Row Hold
Row Speed
Row to Hand Finger Transition Speed
Row to Hand Speed
Row Transition Speed
Session Typing Rate

**Keytouch**
Key Touch Context Cum KH2KI

Key Touch Context KHKI Trigraph
Contextualizes each unigraph hold and digraph interval in trigraph context

Key Touch Context Ngram

Key Touch Context Uni Hold DiInt

Key Touch Fusion Ngram

Key Touch Fusion Sep KHKI Digraph
Contextualizes each unigraph hold in digraph context

Key Touch Fusion Uni Hold DiInt
Implements a specific version of ngram fusion. It takes a unigraph key hold and digraph key interval

Key Touch Hold
Generates full permutation of two UpperCase alpha combinations and places them into a TreeSet which ensures no duplicates and automatically sorts them.

Key Touch
Represents an entire key cycle from press to release for slurs. KeyTouch list is ordered by first key press.

Key Touch Ngram
Highly modular code to allow for selection of ngram length, as well as whether to include interval pauses, keyholds or both.

Key Touch Sub Ngram

Key Touch Trigraph

Key Touch Unigram

**Lex**
EXTRACTORS
Bigram Extractor
Create IDF Map
Create Spell Check Map
Create Syntax Model
Create TFIDF Map
Create User Pause Metrics Map
Create Word Frequency Map
Extract All Answers
POS Extractor
Sentence Detector
Spacebar Punctuation Metrics
TF IDF Extract
TF IDF
Token Extender
Tokenize
Visual Char Stream

MODULES
Character Type
Consonant Frequency
Consonant Timing
Count Sent Word Char
Edit Metrics
Function Word Metrics
In Revision
Keystroke Count
Lex Events Btw Pause
Lexical Diversity
Linear Regression
Pearsons Coefficient
POS Metrics
POS Pause
Punctuation Pause
Spelling Metrics
TF IDF Metrics
Token Extract
Typing Bursts
Typing Rate
Viz Char Stream Metrics

**Mwe**
Mwe Extractor
Mwe Timing Demog
Mwe Timing
Token Extended

**Ngram Model**
Bigram
Bigram Model
Fourgram
Fourgram Model
Generate Model From COCA
Language Model Generator
Model Generator Exception
Trigram
Trigram Model
Unigram Token List Generator

**Pause**
Between Pauses Duration Average
Between Pauses Duration
Between Pauses Keystroke Count Average
Key Up Down
Key Up Down Stats
KSE
Pause Bursts
Pause Count
Pause Count Stats
Pause Duration
Pause Duration Stats
Pase Vs Typing Rate
Run Pause

**Predictability**
Consonant Timing Predictability
Digraph Duration Pred
Digraph Expanded Duration Pred
Digraph Longest Duration Pred
Digraph Z
Hand Transition Predictability
Key Hold Predictability
Key Hold Pred
Key Interval
Key Stroke Predictability
Kt Duration Category Pred
Pk Duration Pred
Mwe Timing Pred Demog
Mwe Timing Predictability
Pennebaker Pred
POS Predictability
Predictability
Test Predictability
Touch Zone Predictability
Trigraph Duration
Trigraph Duration Pred
Trigraph Z
Word Plus KS Predictability
Word Predictability

UID
Word Token Surprisal

**Revision**
Revision CSV
Revision
Revision Ngram Fusion
Revision Ngram
ReVisualize
Test Parse Revision
Unigraph Hold Revision

**Util**
Aggregator
Arff Manipulator
Availability Prune
Calculate Expected Values
Convert Test Vectors
Correlation Tester Keystroke
Correlation Tester Word
Csv to Arff
Expected Value Calculator
Generate Classifier File
Keystroke Map
Keystroke Pointer
Make Symmetric Test Vectors
Measure Word Timing
Merge Arffs
Process Arff Experiment
Process Weka Pipeline
Test Extraction Module
Test Vector Creator
Test Vector Exception
Test Vector Processor
Trigram Counter
Vector To Weka Converter
Weka Pipeline Exception
Weka Pipeline

**WordVar**
WordVariationCSV

##NYIT Modules

**AppWindow**
AppWindow
AppWindowScriptAG
AppWindowScript

**Ballchair**
getThStats

**Events**
Event Iterator
Event List
Event Parser
Generic Event

KEYSTROKE RESEARCH
DocState
T Event Iterator
T Event
T Key Event
T Mouse Event
T Parser
T Time Event

PHASE 6 7
Key Stroke Iterator
Key Stroke
Key Stroke Parser

**Extractor**
Answer
By Order Comparator
Data Node
Dat Selector
Db Config
Extraction Module
Feature Extractor
Feature
Test Vector Shutdown Module

**Lex/modules**
Character Type
Erase Pattern
Familiarity

**Module**
Ancillary Data Test
Atomic Slurs
Cog Load
Cognitive Load Example
Digraph Duration
Example Module 2
Feature Survey
Feature Word Count
Four Graph Duration
Key Hold
Key Interval
Keystroke Duration Serializer
Key Stroke Survey
Key To Text Test
Latency Stats
Pause After Word
PP Burst
P P Burst
PP Burst Metrics
PP Burst Test
P PR Burst
PR2 Burst
PR Burst
PR P Burst
PR PR Burst
RP2 Burst
RP Burst
RR Burst
Slur Density
Slur In Word
Split Trigraph Duration
Time Analysis
Trigraph Duration
Word Count

**Post Processing**
PP Burst Postprocess
PR Burst Postprocess
Remove Features
Remove Invalid
RP Burst Postprocess
RR Burst Postprocess

**Tools**
Ancillary Data Interface
Availability Analyzer
Average Value Analyzer
Convert Generics
Custom Pipeline
Pipeline Modules List
Raw Data Fetcher
Segment Answer
Slur Capture
Test Data
Text Data Import
