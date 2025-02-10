# Ft_Hangout: An Android Contact Management App

## Overview

Ft_Hangout is a mobile application developed in Java for Android that allows users to manage their personal contacts efficiently. Users can create, edit, and delete contacts while maintaining a persistent record using an SQLite database. The app also includes messaging functionality, enabling users to send and receive text messages from their saved contacts. Additionally, it supports multiple languages, a customizable UI, and persistent session tracking.

## Features

### Core Features

- **Create, Edit, and Delete Contacts**: Manage contacts with at least five details per entry.
- **Persistent Storage**: Save contacts in a private SQLite database instead of the shared contacts table.
- **Contact Summary on Homepage**: A list displaying a summary of each saved contact.
- **Detailed Contact View**: Click on a contact to see full details.
- **Messaging Support**: Send and receive SMS messages with saved contacts.
- **Conversation History**: View a structured chat history showing both sender and receiver.
- **Multi-Language Support**: The app supports two languages, with one as the default.
- **Session Tracking**: When the app is put in the background, the timestamp is saved and displayed in a toast message upon returning.
- **UI Customization**: A menu option allows users to change the app’s header color.
- **Landscape & Portrait Modes**: The app is fully responsive in both orientations.
- **Custom App Icon**: The app icon features the 42 logo.

### Bonus Features

- **Contact Photos**: Users can assign a picture to each contact.
- **Auto-Create Contact from Incoming SMS**: If a message is received from an unknown number, a new contact is instantly created with the number as the name.
- **Material Design UI**: A clean and visually appealing user interface.
- **Calling Support**: Users can initiate a call directly from the contact details.

## Technologies Used

- **Language**: Java
- **Development Environment**: Android Studio
- **Database**: SQLite (local storage for contacts)
- **UI Design**: Standard Android UI components with Material Design principles
- **Messaging API**: Android SMS Manager for sending and receiving messages

## Project Structure

### Data Layer

- **SQLite Database**: Manages contacts and conversation history.
- **DAO (Data Access Objects)**: Handles database interactions.

### Business Logic

- **Contact Management**: CRUD operations for contacts.
- **Messaging Service**: Handles sending and receiving SMS.
- **Localization Service**: Manages language switching.

### UI Layer

- **MainActivity**: Displays the homepage with contact summaries.
- **ContactDetailsActivity**: Shows full contact information.
- **MessagingActivity**: Displays conversation history.
- **SettingsActivity**: Allows users to change the header color.

## Constraints

- **No External Libraries**: The app is developed using only native Android components and Java.
- **Manual Testing**: Ensure proper testing since automated tests are not specified in the requirements.
