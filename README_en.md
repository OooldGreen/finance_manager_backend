# Introduction

This project aims to build a personnal financial management system, which includes tracking both current and future incomes, expenses and budgets, adding financial notes, assign tags for notes and tickets.

借鉴原型：https://github.com/hardikSinghBehl/personal-finance-management-system/tree/main

# Main Features

- User's are able to register themselves with the application **(register/login/update-details/change-password)**
- User's are able to manage and track their balance across different **modes**
- User's are able to track their **current** expenses/gains.
- User's are able to track their **upcoming (future)** expenses/gains.
- User's are able to set **financial goals** and track their completion.
- User's are able to set a **monthy spending budgets** and can track it through the month. (calculated automatically at month end using Spring scheduler and cron expressions)
- User's are able to create financial **notes** for their reference.
- User's are able to assign **tags** to their expenses/gains tickets or notes for future reference and quering

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

# Roadmap

| **阶段** | **预计耗时** | **核心任务** |
| --- | --- | --- |
| **基础配置与建模** | 20h | 搭建 Maven 环境，配置数据库，设计 4-5 个核心 Entity (User, Budget, Category等)。 |
| **后端逻辑开发** | 40h | 编写 Controller、Service 和 Repository 层。实现核心计算逻辑（如：超支报警、分类汇总）。 |
| **安全与认证 (JWT)** | 15h | 把你全栈课学到的 JWT 迁移到 Spring Security 中。这部分最折磨人，建议预留时间。 |
| **接口调试 (Postman)** | 10h | 确保每一个 REST API 都能返回正确的数据，处理各种 Exception。 |
| **基础前端对接** | 15h | 用 React 做一个简单的仪表盘 (Dashboard)，展示你的后端数据。 |