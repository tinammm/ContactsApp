# Contacts App
The Contacts App allows users to manage contact information efficiently, including adding, editing, and deleting contacts, managing multiple phone numbers per contact, and more. It follows the MVVM architecture pattern and integrates various Android components for a smooth user experience.

## Key Features
* **MVVM Architecture**: Separation of concerns with clear distinctions between view, view model, and model layers.
* **Room Database**: Utilizes Room for local database management, providing structured storage for contact information.
* __Material Design 3__: Modern and visually appealing UI with light and dark themes.
* __Asynchronous Programming__: Kotlin Coroutines for managing background tasks and ensuring smooth user interactions.
* __Swipe-to-Delete for Numbers__: Allows deleting phone numbers associated with contacts by swiping left.
* __Favorites__: Ability to mark contacts as favorites, displayed prominently at the top of the contacts list.
* __Input Validation__: Ensures data integrity with input checks before inserting into the database.
* __Integration with External Apps__: Clicking on phone numbers launches an intent to choose an app for making calls.
* __Database Asset__: Prepopulates the database with initial contact information for a seamless first-time user experience.


## Screens
* **Main Screen**
  * Displays a list of contacts ordered alphabetically, categorized by the first letter of the contact's name.
  * Favorites are prioritized and listed first.
  * FAB button for adding new contact or adding number to existing.
  * Input validation ensures that on _Add Contact Dialog_ the contact must have a name, the phone number must be properly formatted, and users are prompted with error messages if the input does not meet these criteria before proceeding to save the contact.
 
* **Details Screen**
  * Provides options to edit contact details, delete the contact with a safety dialog for confirmation, and mark the contact as a favorite.
  * Ensures user confirmation before deleting a contact to prevent accidental deletion.
  * Allows users to edit the first name and last name of the contact with input checks for valid inputs.
  * Clicking on phone number initiates an intent to open a dialog for selecting an appropriate app to make a call when clicking on a phone number.
  * Swipe-to-Delete for numbers enables deletion of phone numbers associated with the contact by swiping left, ensuring that swipe delete is only available if the contact has multiple phone numbers to prevent deletion of the last remaining number.

## Screenshots
<div align="center">
    <img src="/screenshots/main_screen_light.jpg?raw=true" width="140px"</img> 
    <img height="0" width="20px">
    <img src="/screenshots/add_contact_dialog.jpg?raw=true" width="140px"</img> 
    <img height="0" width="20px">
    <img src="/screenshots/add_contact_field_errors.jpg?raw=true" width="140px"</img> 
    <img height="0" width="20px">
    <img src="/screenshots/details_screen.jpg?raw=true" width="140px"</img> 
    <img height="0" width="20px">
    <img src="/screenshots/delete_contact_dialog.jpg?raw=true" width="140px"</img> 
    <img height="0" width="20px">
    <img src="/screenshots/edit_contact.jpg?raw=true" width="140px"</img> 
    <img height="0" width="20px">
    <img src="/screenshots/slide_delete.jpg?raw=true" width="140px"</img> 
    <img height="0" width="20px">
    <img src="/screenshots/main_screen_dark.jpg?raw=true" width="140px"</img> 
    <img height="0" width="20px">
    <img src="/screenshots/details_screen_dark.jpg?raw=true" width="140px"</img> 
    <img height="0" width="20px">
</div>

### Database Schema

#### Table: contacts

| Column      | Type      | Description                      |
|-------------|-----------|----------------------------------|
| id          | INTEGER   | Primary Key, auto-generated       |
| firstName   | TEXT      | First name of the contact         |
| lastName    | TEXT      | Last name of the contact (nullable)|
| isFavourite | BOOLEAN   | Indicates if the contact is a favorite |
| color       | INTEGER   | Color associated with the contact |

#### Table: phone_numbers

| Column      | Type      | Description                      |
|-------------|-----------|----------------------------------|
| id          | INTEGER   | Primary Key, auto-generated       |
| contactId   | INTEGER   | Foreign Key referencing contacts.id |
| number      | TEXT      | Phone number                      |
| type        | ENUM      | Type of phone number (HOME, MOBILE, WORK, etc.) |

**Relationship Clarification:**
- The `contactId` column in the `phone_numbers` table serves as a foreign key that references the `id` column in the `contacts` table. This column establishes a relationship where each phone number in the `phone_numbers` table is associated with one and only one contact from the `contacts` table.
- Therefore, each contact in the `contacts` table can have multiple entries in the `phone_numbers` table, with each entry representing a different phone number type (HOME, MOBILE, WORK, etc.).


## License
Copyright 2022 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
