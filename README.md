# CurrencyExchange

An educational project for currency conversion. This project implements a simple currency exchange functionality using Java and other technologies.

## Table of Contents
- [Overview](#overview)
- [Technologies](#Technologies)
- [Rest API methods](#Rest-API-methods)

## Overview

REST API for describing currencies and exchange rates. Allows you to view and edit lists of currencies and exchange rates, and calculate conversions of any amount from one currency to another.

Project Goals:
- Familiarity with the MVC pattern. 
- Understanding REST API design principles. 
- Hands-on experience with SQL and database integration. 
- Deploying a Java application to a Linux-based server.


## Technologies

- Java Servlets
- Patterns of MVC
- Maven
- REST API integration
- JDBC
- PostgreSQL

## Rest API methods
### **Currencies**
#### **GET `/currencies`**
Returns list of all currencies. Example of response:
```sql
[
    {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },   
    {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    }
]
```

#### **GET `/currency/USD`**
Returns particular currency. The currency code is specified in the query address Example of response:
```sql
[
  {
    "id": 0,
    "name": "United States dollar",
    "code": "USD",
    "sign": "$"
  }
]
```

#### **POST `/currencies`**
Adding a new currency to the database. Data is passed in the body of request in the x-www-form-urlencoded. The form fields are name, code, symbol. Example of response (inserted record):
```sql
[
  {
    "id": 1,
    "name": "Kyrgyz som",
    "code": "KGS",
    "sign": "C"
  }
]
```

### **Exchange rates**
#### **GET `/exchangeRates`**
Returns list of all exchange rates. Example of response:
```sql
[
  {
    "id": 0,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 1,
      "name": "Euro",
      "code": "EUR",
      "sign": "€"
    },
    "rate": 0.93
  },
  {
    "id": 1,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 2,
      "name": "Kyrgyz som",
      "code": "KGS",
      "sign": "C"
    },
    "rate": 88.23
  },
  "..."
]
```

#### **POST `/exchangeRates`**
Adding a new exchange rate to the database. Data is passed in the body of request in the x-www-form-urlencoded. The form fields are baseCurrencyCode, targetCurrencyCode, rate. Example of response (inserted record):
```sql
[
  {
    "id": 2,
    "baseCurrency": {
      "id": 1,
      "name": "Euro",
      "code": "EUR",
      "sign": "€"
    },
    "targetCurrency": {
      "id": 2,
      "name": "Kyrgyz som",
      "code": "KGS",
      "sign": "C"
    },
    "rate": 92.34
  }
]
```

#### **GET `/exchangeRate/USDKGS`**
Adding a new exchange rate to the database. Data is passed in the body of request in the x-www-form-urlencoded. The form fields are baseCurrencyCode, targetCurrencyCode, rate. Example of response (inserted record):
```sql
[
  {
    "id": 1,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 2,
      "name": "Kyrgyz som",
      "code": "KGS",
      "sign": "C"
    },
    "rate": 88.23
  }
]
```

#### **PATCH `/exchangeRate/USDKGS`**
Updates the existing exchange rate in the database. The currency pair is specified by consecutive currency codes in the query address. The data is passed in the body of the request in the x-www-form-urlencoded. The only form field is rate. Example of response (inserted record):
```sql
[
  {
    "id": 1,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 2,
      "name": "Kyrgyz som",
      "code": "KGS",
      "sign": "C"
    },
    "rate": 80.04
  }
]
```

### **Currency Exchange**
#### **GET `/exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT`**
Calculate the conversion of a particular amount of money from one currency to another. The currency pair and amount is specified in the query address. Example of request - GET `/exchange?from=USD&to=KGS&amount=10`.

Example of request:
```sql
{
  "baseCurrency": {
    "id": 0,
    "name": "United States dollar",
    "code": "USD",
    "sign": "$"
  },
  "targetCurrency": {
    "id": 2,
    "name": "Kyrgyz som",
    "code": "KGS",
    "sign": "C"
  },
  "rate": 80.04,
  "amount": 10.00,
  "convertedAmount": 800.4
}
```

Receiving an exchange rate can follow one of three scenarios. Let's say we make a transfer from currency A to currency B:
1. There is a currency pair AB in the `ExchangeRates` table - take its rate
2. There is a currency pair BA in the `ExchangeRates` table - take its rate and calculate the reverse to get AB
3. In the ExchangeRates table there are currency pairs C-A and C-B or A-C and B-c - we can calculate the AB rate from these rates


  
