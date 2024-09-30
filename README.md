# Currency Exchange and Discount Calculation Application

## Overview
This Spring Boot application integrates with a third-party currency exchange API to retrieve real-time exchange rates and applies a set of discount rules based on user type and other criteria. It exposes an API that allows users to submit a bill in one currency and receive the payable amount in another currency after discounts are applied.

## Prerequisites
Before running the application, ensure you have the following installed:

- **Java 17**
- **Maven 3.6+** or **Gradle**
- A valid API key for a currency exchange service (e.g., ExchangeRate-API or Open Exchange Rates)
- **Postman** (for testing the API)

## Technologies
The following technologies are used in this application:

- **Java 17**: Programming language used to develop the backend
- **Spring Boot**: Framework for building Java-based applications
- **Spring Security**: Used for authentication and JWT token generation
- **JWT (JSON Web Token)**: For stateless user authentication
- **Mockito**: For mocking services and creating unit tests
- **H2 Database**: In-memory database used for development and testing
- **Maven/Gradle**: Build and dependency management

## App Features
- **Real-time currency conversion**: Integrates with a third-party API to fetch live exchange rates.
- **Discount application logic**:
  - 30% discount for employees
  - 10% discount for affiliates
  - 5% discount for customers with tenure over 2 years
  - $5 discount for every $100 spent on non-grocery items
  - A flat discount is applied before any percentage-based discounts
  - No percentage-based discounts for grocery items
- **JWT-based authentication**: Secure the application endpoints by generating and validating JWT tokens.

## Example of APIs and Expected Outputs

### 1. Login API
**Endpoint**: `POST /api/auth/login`

**Request Body**:
```json
{
    "username": "employee1",
    "password": "password123"
}

### 2. Calculate Payable Amount
**Endpoint**: `POST /api/calculate`

**Request Header**:

Authorization: Bearer <token>

**Request Body**:
```json
{
    "billingDetails": {
        "items": [
            {
                "name": "TV",
                "price": 120,
                "isGrocery": false
            },
            {
                "name": "Milk",
                "price": 80,
                "isGrocery": true
            }
        ],
        "originalCurrency": "INR",
        "targetCurrency": "USD"
    }
}
```

### Collection of Users and Products
### Users:
Here are the predefined users with their credentials and roles:

| Username	| Password	| Role	| Tenure |
| ------------- | ------------- | ------------- | ------------- |
| employee1 |password123	| EMPLOYEE | 3 years |
| affiliate	| affiliate1234	| AFFILIATE	| 1 year |
| customer | customer123	| CUSTOMER | 3 years |
| customer2	| customer1234	| CUSTOMER	| 1 year |


Example Products:
| Item Name	| Price	| Category |
| ------------- | ------------- | ------------- |
| A | 120	| Grocery|
| B |80 | non-grocery|
| C | 1000	| Grocery|
| D | 50	| Non-grocery |


## Running the Application

```http
git clone https://github.com/your-repo/currency-exchange-app.git

cd currency-exchange-app

mvn clean install

mvn spring-boot:run
```

### Running Test Cases

```http
  mvn test

```

### Code Coverage Report

```http
  mvn clean test jacoco:report

```
The coverage report will be available in the target/site/jacoco/index.html file.

## UML Diagrams
Refer to the project documentation for UML diagrams showing class interactions, including Controller, Service, Strategy, and Factory components.

UML Diagram - [Link](https://lucid.app/lucidchart/3d3f3ca9-e87d-4772-9b88-d53a39e80c45/edit?viewport_loc=-2906%2C-233%2C3107%2C1352%2C0_0&invitationId=inv_3f876a90-0113-45ed-a891-9f80dc814235)

## Design Patterns
**Strategy Pattern**: Used to apply different discount strategies (EmployeeDiscountStrategy, AffiliateDiscountStrategy, etc.).

**Factory Pattern**: The DiscountStrategyFactory is responsible for creating the appropriate DiscountStrategy based on the user’s details.

## Caching
In-Memory Caching: The application uses Spring’s @Cacheable annotation to cache exchange rates for a base currency. The cache is invalidated after a certain time.


## License

[MIT](https://choosealicense.com/licenses/mit/)
