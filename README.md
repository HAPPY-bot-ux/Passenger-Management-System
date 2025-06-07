# 🚌 InterSA Passenger Management System

A Java Swing-based desktop application for managing passenger details for InterSA transportation. This application allows users to **insert**, **view**, and **delete** passenger records stored in a **MySQL database**.

---

## ✨ Features

- 🚀 Insert passenger details (First Name, Last Name, Destination, Phone)
- 📋 View all existing passenger records in a table
- ❌ Delete selected passenger entries
- 👁️ Toggle table visibility (Show/Hide)
- 💾 Integrated with MySQL for persistent storage

---

## 🛠️ Technologies Used

- **Java Swing** for GUI
- **MySQL** as the database
- **JDBC** for database connectivity

---

## 🧩 Database Setup

Before running the project, ensure you have MySQL installed and set up with the following:

### 🔧 Database Configuration

- **Database Name**: `fa3_assessment`
- **Table Name**: `passenger_details`

### 🧱 Table Structure

```sql
CREATE DATABASE IF NOT EXISTS fa3_assessment;

USE fa3_assessment;

CREATE TABLE IF NOT EXISTS passenger_details (
    FName VARCHAR(50),
    LName VARCHAR(50),
    Dest VARCHAR(100),
    Phone VARCHAR(20)
);
