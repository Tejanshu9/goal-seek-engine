# Goal Seek Engine

A full-stack financial calculation application that combines a powerful **Spring Boot backend** with a sleek **terminal-inspired frontend** to perform goal seek operations and manage mathematical formulas.

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?style=flat-square)
![Theme](https://img.shields.io/badge/Theme-Cyberpunk%20Terminal-00ff9f?style=flat-square)

## Live Demo

🔗 **Application:** [https://goal-seek-engine-mf5tpg.fly.dev/]


## Overview

The Goal Seek Engine determines what input value is required to achieve a specified outcome based on user-defined formulas. Perfect for financial planning, loan calculations, investment projections, and any scenario where you need to work backwards from a desired result.

**Key Capabilities:**
- 🎯 **Goal Seeking** - Find unknown variable values to achieve target outcomes
- 📐 **Formula Management** - Create, read, update, and delete custom formulas
- 🔬 **Formula Evaluation** - Test formulas with specific variable values
- 🚀 **Multiple Algorithms** - Brent's method, Newton-Raphson, and Bisection for robust convergence
- 🎨 **Modern UI** - Dark cyberpunk terminal-inspired interface
- 📖 **Interactive Docs** - Built-in Swagger UI for API exploration

## Pre-loaded Financial Formulas

| Formula Name | Description | Variables |
|-------------|-------------|-----------|
| `SIP_FUTURE_VALUE` | Future value of SIP investment | P (monthly investment), r (monthly rate), n (months) |
| `EMI_CALCULATION` | EMI for loans | P (principal), r (monthly rate), n (months) |
| `SIMPLE_INTEREST` | Simple interest calculation | P (principal), R (annual rate), T (years) |
| `COMPOUND_INTEREST` | Compound interest amount | P, r (annual rate), n (compounds/year), t (years) |
| `PRESENT_VALUE` | Present value of future cash | CF (cash flow), r (rate), n (periods) |
| `FUTURE_VALUE` | Future value calculation | PV, r (rate), n (periods) |
| `CREDIT_UTILIZATION` | Credit utilization % | UsedCredit, CreditLimit |
| `ROI` | Return on Investment | FinalValue, InitialValue |
| `DEBT_TO_INCOME` | Debt-to-income ratio | MonthlyDebt, MonthlyIncome |

## Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- Modern web browser (Chrome 88+, Firefox 85+, Safari 14+)

### Build and Run
```bash
# Clone or navigate to the project directory
cd goal-seek-engine

# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

The application starts on **http://localhost:8080**

### Access the Application

- **Web Interface:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console

## Features

### 🎯 Goal Seek Calculator

Find the input value needed to achieve your target outcome.

**Example Use Cases:**
- What monthly SIP achieves ₹10L in 10 years?
- What interest rate results in ₹25K EMI?
- What credit limit maintains 30% utilization?

**How It Works:**
1. Select a formula
2. Choose which variable to solve for
3. Enter your target value
4. Provide known variable values
5. Optionally set bounds and initial guess
6. Get the computed value with convergence details

### 📐 Formula Manager

Full CRUD operations for mathematical formulas through both UI and API.

**Create Custom Formulas:**
```json
{
  "name": "CUSTOM_SAVINGS",
  "expression": "P * (1 + r)^n + C * (((1 + r)^n - 1) / r)",
  "description": "Savings with initial deposit and contributions",
  "outputVariable": "FV",
  "variables": ["P", "r", "n", "C"]
}
```

### 🔬 Formula Evaluator

Test any formula by providing values for all variables and seeing the computed result instantly.

## Usage Examples

### Web Interface

1. **Navigate to http://localhost:8080**
2. **Goal Seek Tab**: 
   - Select "SIP_FUTURE_VALUE"
   - Set target value: `1000000`
   - Choose seek variable: `P`
   - Enter known values: `r = 0.01, n = 120`
   - Click "Calculate"
   - Result: ₹4,347.09 monthly investment needed

3. **Formula Manager Tab**:
   - Click "Create New Formula"
   - Fill in formula details
   - Save and test immediately

### API Examples

#### 1. Goal Seek: Find Monthly SIP for Target Corpus

**Question:** What monthly SIP is needed to accumulate ₹10,00,000 in 10 years at 12% annual return?
```bash
curl -X POST http://localhost:8080/api/goal-seek \
  -H "Content-Type: application/json" \
  -d '{
    "formulaName": "SIP_FUTURE_VALUE",
    "knownValues": {
      "r": 0.01,
      "n": 120
    },
    "seekVariable": "P",
    "targetValue": 1000000,
    "lowerBound": 100,
    "upperBound": 50000
  }'
```

**Response:**
```json
{
  "success": true,
  "formulaName": "SIP_FUTURE_VALUE",
  "seekVariable": "P",
  "computedValue": 4347.09,
  "targetValue": 1000000.0,
  "achievedValue": 1000000.0,
  "error": 1.8e-9,
  "iterations": 42,
  "algorithm": "Brent",
  "message": "Converged successfully"
}
```

#### 2. Goal Seek: Find Interest Rate for EMI Constraint

**Question:** At what interest rate will EMI be ₹25,000 for a ₹20,00,000 loan over 20 years?
```bash
curl -X POST http://localhost:8080/api/goal-seek \
  -H "Content-Type: application/json" \
  -d '{
    "formulaName": "EMI_CALCULATION",
    "knownValues": {
      "P": 2000000,
      "n": 240
    },
    "seekVariable": "r",
    "targetValue": 25000,
    "lowerBound": 0.001,
    "upperBound": 0.03
  }'
```

#### 3. Create Custom Formula
```bash
curl -X POST http://localhost:8080/api/formulas \
  -H "Content-Type: application/json" \
  -d '{
    "name": "CUSTOM_SAVINGS",
    "expression": "P * (1 + r)^n + C * (((1 + r)^n - 1) / r)",
    "description": "Savings with initial deposit and monthly contributions",
    "outputVariable": "FV",
    "variables": ["P", "r", "n", "C"]
  }'
```

#### 4. Evaluate Formula
```bash
curl -X POST http://localhost:8080/api/goal-seek/evaluate/EMI_CALCULATION \
  -H "Content-Type: application/json" \
  -d '{
    "P": 1000000,
    "r": 0.00833,
    "n": 240
  }'
```

## REST API Endpoints

### Formula Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/formulas` | Create a new formula |
| `GET` | `/api/formulas` | Get all formulas |
| `GET` | `/api/formulas/{name}` | Get formula by name |
| `PUT` | `/api/formulas/{name}` | Update a formula |
| `DELETE` | `/api/formulas/{name}` | Delete a formula |

### Goal Seek Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/goal-seek` | Perform goal seek calculation |
| `POST` | `/api/goal-seek/evaluate/{formulaName}` | Evaluate formula with values |

## Technology Stack

### Backend
- **Spring Boot 3.x** - Application framework
- **Spring Data JPA** - Database operations
- **H2 Database** - In-memory database
- **exp4j** - Expression parsing and evaluation
- **SpringDoc OpenAPI** - API documentation
- **Maven** - Build tool

### Frontend
- **HTML5** - Semantic markup
- **CSS3** - Custom properties, Grid, Flexbox, animations
- **Vanilla JavaScript** - ES6+, async/await, Fetch API
- **Outfit & JetBrains Mono** - Typography

### Algorithms
- **Brent's Method** - Primary algorithm (combines bisection, secant, inverse quadratic interpolation)
- **Newton-Raphson** - Fast convergence with numerical derivatives
- **Bisection** - Reliable fallback method

## Configuration

Edit `src/main/resources/application.properties`:
```properties
# Server configuration
server.port=8080

# H2 Database
spring.datasource.url=jdbc:h2:mem:goalseekdb
spring.datasource.username=sa
spring.datasource.password=

# Goal Seek Settings
goalseek.max-iterations=1000
goalseek.tolerance=1e-10
goalseek.default-lower-bound=-1000000
goalseek.default-upper-bound=1000000
```

## Project Structure
```
goal-seek-engine/
├── src/main/
│   ├── java/com/fintech/goalseek/
│   │   ├── GoalSeekEngineApplication.java
│   │   ├── algorithm/
│   │   │   ├── GoalSeekAlgorithm.java
│   │   │   ├── GoalSeekResult.java
│   │   │   ├── BisectionAlgorithm.java
│   │   │   ├── NewtonRaphsonAlgorithm.java
│   │   │   └── BrentAlgorithm.java
│   │   ├── config/
│   │   │   ├── DataInitializer.java
│   │   │   └── OpenApiConfig.java
│   │   ├── controller/
│   │   │   ├── FormulaController.java
│   │   │   └── GoalSeekController.java
│   │   ├── dto/
│   │   │   ├── FormulaRequest.java
│   │   │   ├── FormulaResponse.java
│   │   │   ├── GoalSeekRequest.java
│   │   │   ├── GoalSeekResponse.java
│   │   │   └── ErrorResponse.java
│   │   ├── entity/
│   │   │   └── Formula.java
│   │   ├── exception/
│   │   │   ├── FormulaNotFoundException.java
│   │   │   ├── FormulaDuplicateException.java
│   │   │   ├── FormulaEvaluationException.java
│   │   │   ├── GoalSeekException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── repository/
│   │   │   └── FormulaRepository.java
│   │   └── service/
│   │       ├── FormulaService.java
│   │       ├── FormulaEvaluator.java
│   │       └── GoalSeekService.java
│   └── resources/
│       ├── static/
│       │   ├── index.html
│       │   ├── styles.css
│       │   └── script.js
│       └── application.properties
├── pom.xml
└── README.md
```

## Design System

### Color Palette (Dark Cyberpunk Terminal)

| Variable | Value | Usage |
|----------|-------|-------|
| `--accent-primary` | `#00ff9f` | Primary actions, highlights |
| `--accent-secondary` | `#00d4ff` | Secondary elements, links |
| `--accent-tertiary` | `#ff6b9d` | Variable names, accents |
| `--bg-deep` | `#0a0e14` | Page background |
| `--bg-primary` | `#0d1117` | Card backgrounds |



## License

Apache License 2.0

---

