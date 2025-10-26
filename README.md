# Expense Manager FX

![Java](https://img.shields.io/badge/Java-17-blue?logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-25.0.1-orange)

A **JavaFX-based Expense Tracker** to easily manage your daily expenses. Track, filter, and delete expenses by date, and get instant summaries of your spending.

---

## 📌 Features

- Add expenses with **name, amount, and date**
- Delete **single expense** or **all expenses for a selected date**
- View **all dates with expenses**
- Sort dates **ascending/descending**
- See **total spending for a selected date**
- Clean, responsive GUI with JavaFX and custom CSS styling

---

## 📁 Project Structure

    src/
    └─ com.piyush.expensetracker/
        ├─ Expense.java
        ├─ ExpenseManagerFX.java
        └─ style.css

 
> Note: `style.css` is inside the package for JavaFX resource loading.
> 
>
---

## 🏃 How to Run

### Requirements

- Java 17+ (or compatible)
- JavaFX SDK (any compatible version)
- IntelliJ IDEA or any IDE supporting JavaFX

### Steps (IntelliJ IDEA)

1. Open the project in IntelliJ IDEA.
2. Add JavaFX SDK to your project libraries:
    - **File → Project Structure → Libraries → + → Java**  
      Select the `lib` folder from your JavaFX SDK.
3. Configure Run settings:
    - **Run → Edit Configurations → VM options**:
      ```
      --module-path /path/to/javafx-sdk-25.0.1/lib --add-modules javafx.controls,javafx.fxml
      ```
      *(Replace `/path/to/javafx-sdk-25.0.1` with your actual JavaFX SDK path)*
4. Right-click `ExpenseManagerFX.java` → **Run 'ExpenseManagerFX.main()'**
5. The app window will open. 🎉

---

## ⚙️ Future Improvements

- Persistent storage (CSV, JSON, or DB)
- Monthly/weekly summary charts
- Expense categories and filters
- Dark mode / multiple themes
- Export expenses as PDF

---

## 💡 Tips

- Keep `style.css` in the same package as `ExpenseManagerFX.java` or adjust the resource path accordingly.
- Use the **ListView** on the left to filter expenses by date.
- Use **Add Expense** and **Delete Selected** buttons to manage your data.
