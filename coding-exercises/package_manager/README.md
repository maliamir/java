# Robotic Arm Package Dispatcher

This repository contains the package sorting logic for **Smarter Technology’s** robotic automation factory. The system classifies packages into specific dispatch stacks based on their dimensions (cm) and mass (kg).

---

## 🛠 Sorting Logic

The algorithm evaluates packages based on two criteria: **Bulky** and **Heavy**.

### 1. Criteria Definitions
* **Bulky**: A package is bulky if its volume (Width × Height × Length) is $\ge 1,000,000 \text{ cm}^3$ **OR** if any single dimension is $\ge 150 \text{ cm}$.
* **Heavy**: A package is heavy if its mass is $\ge 20 \text{ kg}$.

### 2. Dispatch Categories
Based on the criteria above, packages are routed to one of the following stacks:

| Stack | Description |
| :--- | :--- |
| **`STANDARD`** | Packages that are neither bulky nor heavy. |
| **`SPECIAL`** | Packages that are **either** bulky or heavy. |
| **`REJECTED`** | Packages that are **both** bulky and heavy. |

---

## 💻 Implementation

The core logic is implemented in the `sort` function within the `PackageManager` class.

### Method Signature
```java
public static String sort(int width, int height, int length, int mass)


## 💻 Usage

// Standard Package
PackageManager.sort(10, 10, 10, 10); // Returns "STANDARD"

// Heavy Package (Special)
PackageManager.sort(10, 10, 10, 25); // Returns "SPECIAL"

// Bulky & Heavy Package (Rejected)
PackageManager.sort(200, 200, 200, 30); // Returns "REJECTED"


## 🚦 Testing
The implementation includes built-in assertions to verify sorting accuracy. To execute the tests, run the class with the -ea (enable assertions) flag:

Bash
javac PackageManager.java
java -ea PackageManager