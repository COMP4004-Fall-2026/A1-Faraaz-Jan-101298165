# Assignment 2
Faraaz Jan 101298165 COMP 4004

## 1. Assumptions/Ambiguties

- R3.2
  - In assignment 1, the ui checked `borrower.getBorrowerBookCount() == 0` and said no books are currently borrowed, but the `LibrarySystem.returnBook()` doesnt check that first. Assumed this should be hecked first and throw a seperate LibraryException message.
- R1.3 & R3.3
  - A borrower who is first in line in a title's hold queue gets a pending hold notification when the copy becomes available upon previous user return and `LibrarySystem.getHoldNotification()`

## 2. Changes Made to A1 for BDD

1. `LibrarySystem.java`
   - Added `findUser()` read-only lookup to get a specific user's borrowedBookCount from LibrarySteps.
   - `returnBook()` checks borrowedBookCount == 0 and throws LibraryException with "No books are currently borrowed".
2. `BookTitle.java`
   - `hasNotificationFor()` is public so it can be accessed by the uc03 R3.3 steps

## 3. How to Run

1. Open IntelliJ IDEA in the project folder
2. Import JUnit if not already done (this caused some issues for me)
3. Right click `TestRunner.java` in the `test/java/runners` folder, and select `Run TestRunner`.

## 4. Initial Data

### Users

| Username    | Password    | Role      |
|-------------|-------------|-----------|
| `borrower1` | `password1` | BORROWER  |
| `borrower2` | `password2` | BORROWER  |
| `borrower3` | `password3` | BORROWER  |
| `borrower4` | `password4` | BORROWER  |
| `borrower5` | `password5` | BORROWER  |
| `librarian1`| `admin1`    | LIBRARIAN |
| `librarian2`| `admin2`    | LIBRARIAN |

### Books

| Copy # | Title                   |
|--------|-------------------------|
| `C001` | The Great Gatsby        |
| `C002` | The Hobbit              |
| `C003` | Pride and Prejudice     |
| `C004` | 1984                    |
| `C005` | To Kill a Mockingbird   |
| `C006` | The Catcher in the Rye  |
| `C007` | The Lord of the Rings   |
| `C008` | The Alchemist           |
| `C009` | The Book Thief          |
| `C010` | Dune                    |
| `C011` | Jane Eyre               |
| `C012` | The Kite Runner         |
| `C013` | Little Women            |
| `C014` | The Odyssey             |
| `C015` | The Road                |
| `C016` | The Shining             |
| `C017` | The Stranger            |
| `C018` | The Sun Also Rises      |
| `C019` | Wuthering Heights       |
| `C020` | The Yellow Wallpaper    |