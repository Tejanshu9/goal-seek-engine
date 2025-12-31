# Goal Seek Engine

A Spring Boot application that provides a **Goal Seek Engine** for financial calculations. This engine determines the input value required to achieve a specified outcome based on user-defined formulas.

## Features

- **Formula Management**: Create, read, update, and delete financial formulas with custom variables
- **Goal Seek Calculation**: Find unknown variable values to achieve target outcomes
- **Multiple Algorithms**: Uses Brent's method, Newton-Raphson, and Bisection for robust convergence
- **Pre-loaded Formulas**: Includes common financial formulas (SIP, EMI, NPV, etc.)
- **REST API**: Clean RESTful endpoints with OpenAPI documentation
- **Swagger UI**: Interactive API documentation and testing

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

- Java 17 or higher
- Maven 3.6+

### Build and Run

```bash
# Clone or navigate to the project directory
cd goal-seek-engine

# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

The application starts on `http://localhost:8080`

### Access Swagger UI

Open your browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Formula Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/formulas` | Create a new formula |
| `GET` | `/api/formulas` | Get all formulas |
| `GET` | `/api/formulas/{name}` | Get formula by name |
| `PUT` | `/api/formulas/{name}` | Update a formula |
| `DELETE` | `/api/formulas/{name}` | Delete a formula |

### Goal Seek

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/goal-seek` | Perform goal seek calculation |
| `POST` | `/api/goal-seek/evaluate/{formulaName}` | Evaluate formula with values |

## Usage Examples

### 1. Create a Custom Formula

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

### 2. Goal Seek: Find Monthly SIP for Target Corpus

**Question**: What monthly SIP is needed to accumulate ₹10,00,000 in 10 years at 12% annual return?

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

**Response**:
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

### 3. Goal Seek: Find Interest Rate for EMI Constraint

**Question**: At what interest rate will EMI be ₹25,000 for a ₹20,00,000 loan over 20 years?

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

### 4. Goal Seek: Find Required Credit Limit

**Question**: What credit limit is needed to maintain 30% utilization with ₹45,000 used?

```bash
curl -X POST http://localhost:8080/api/goal-seek \
  -H "Content-Type: application/json" \
  -d '{
    "formulaName": "CREDIT_UTILIZATION",
    "knownValues": {
      "UsedCredit": 45000
    },
    "seekVariable": "CreditLimit",
    "targetValue": 30,
    "lowerBound": 50000,
    "upperBound": 500000
  }'
```

### 5. Evaluate a Formula

```bash
curl -X POST http://localhost:8080/api/goal-seek/evaluate/EMI_CALCULATION \
  -H "Content-Type: application/json" \
  -d '{
    "P": 1000000,
    "r": 0.00833,
    "n": 240
  }'
```

## Formula Expression Syntax

The engine uses **exp4j** for expression parsing. Supported operators and functions:

### Operators
- Arithmetic: `+`, `-`, `*`, `/`, `^` (power)
- Parentheses: `(`, `)`

### Built-in Functions
- `sqrt(x)`, `abs(x)`, `log(x)`, `log10(x)`
- `sin(x)`, `cos(x)`, `tan(x)`
- `exp(x)`, `pow(x, y)`
- `min(x, y)`, `max(x, y)`
- `floor(x)`, `ceil(x)`

### Examples
```
# EMI Formula
P * r * (1 + r)^n / ((1 + r)^n - 1)

# Compound Interest
P * (1 + r/n)^(n*t)

# Present Value
CF / (1 + r)^n
```

## Goal Seek Algorithms

The engine employs three numerical methods for finding roots:

1. **Brent's Method** (Primary): Combines bisection, secant, and inverse quadratic interpolation. Most robust and efficient.

2. **Newton-Raphson**: Uses numerical derivatives for fast convergence near the root.

3. **Bisection**: Simple but reliable fallback method.

The engine automatically tries multiple algorithms if the primary method fails.

## Configuration

Configure the goal seek behavior in `application.properties`:

```properties
# Maximum iterations for convergence
goalseek.max-iterations=1000

# Convergence tolerance
goalseek.tolerance=1e-10

# Default search bounds
goalseek.default-lower-bound=-1000000
goalseek.default-upper-bound=1000000
```

## H2 Console

Access the H2 database console at:
```
http://localhost:8080/h2-console
```

Connection details:
- JDBC URL: `jdbc:h2:mem:goalseekdb`
- Username: `sa`
- Password: (empty)

## Project Structure

```
goal-seek-engine/
├── src/main/java/com/fintech/goalseek/
│   ├── GoalSeekEngineApplication.java
│   ├── algorithm/
│   │   ├── GoalSeekAlgorithm.java
│   │   ├── GoalSeekResult.java
│   │   ├── BisectionAlgorithm.java
│   │   ├── NewtonRaphsonAlgorithm.java
│   │   └── BrentAlgorithm.java
│   ├── config/
│   │   ├── DataInitializer.java
│   │   └── OpenApiConfig.java
│   ├── controller/
│   │   ├── FormulaController.java
│   │   └── GoalSeekController.java
│   ├── dto/
│   │   ├── FormulaRequest.java
│   │   ├── FormulaResponse.java
│   │   ├── GoalSeekRequest.java
│   │   ├── GoalSeekResponse.java
│   │   └── ErrorResponse.java
│   ├── entity/
│   │   └── Formula.java
│   ├── exception/
│   │   ├── FormulaNotFoundException.java
│   │   ├── FormulaDuplicateException.java
│   │   ├── FormulaEvaluationException.java
│   │   ├── GoalSeekException.java
│   │   └── GlobalExceptionHandler.java
│   ├── repository/
│   │   └── FormulaRepository.java
│   └── service/
│       ├── FormulaService.java
│       ├── FormulaEvaluator.java
│       └── GoalSeekService.java
├── src/main/resources/
│   └── application.properties
├── pom.xml
└── README.md
```

## License

Apache 2.0
