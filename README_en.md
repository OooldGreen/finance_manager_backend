# Introduction

This project aims to build a personnal financial management system, which includes tracking both current and future incomes, expenses and budgets, adding financial notes, assign tags for notes and tickets.

借鉴原型：https://github.com/hardikSinghBehl/personal-finance-management-system/tree/main

# Main Features

- Users are able to register themselves with the application **(register/login/update-details/change-password)**
- Users are able to manage and track their balance across different **modes**
- Users are able to track their **current** expenses/gains.
- Users are able to track their **upcoming (future)** expenses/gains.
- Users are able to set **financial goals** and track their completion.
- Users are able to set a **monthy spending budgets** and can track it through the month. (calculated automatically at month end using Spring scheduler and cron expressions)
- Users are able to assign **tags** to their expenses/gains tickets or notes for future reference and quering
- Users are able to **search** transactions by key words

# Tech Stack

- Maven
- Java
- Spring Boot
- Spring Security (JWT Based Authentication and Authorization)
- Spring Data JPA/Hibernate
- PostgreSQL
- Open-API (Swagger-UI)
- Lombok
- Figma

# Physical data model
![Physical data model.png](/Physical%20data%20model_end.png)
