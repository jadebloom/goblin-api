[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)

![Version](https://img.shields.io/github/v/tag/jadebloom/goblin-api?label=Version)

# Goblin API

Goblin API - is a REST API application for expense tracking and analyzing. Dedicated for Goblin desktop application, built on Spring Boot. 


## Features

- Secure authentication and authorization.
- Create and manage custom expenses and expense categories.
- Create and manage custom currencies.
- Manage your account.
## Main Stack

- Spring Boot
- Spring Data JPA
- Spring Security
- PostgreSQL
- ModelMapper
- Hybernate Validator

## Environment Variables

To run this project, you will need to add the following environment variables to your .env file

`DATASOURCE_URL`

`DATASOURCE_DRIVER_CLASSNAME`

`DATASOURCE_USERNAME`

`DATASOURCE_PASSWORD`

`JWT_SECRET`

## Run Locally

Clone the project

```bash
  git clone https://github.com/jadebloom/goblin-api
```

Go to the project directory

```bash
  cd goblin-api
```

Run it using maven:

```bash
  mvn clean spring-boot:run
```


## Running Tests

To run tests, run the following command

```bash
  mvn clean test
```


## API Reference

The reference is generated automatically by Swagger. The config can be found in `/src/main/resources/public/openapi`.


## Authors

- [@jadebloom](https://www.github.com/jadebloom)


## Acknowledgements

- [Great README services](https://readme.so/editor)
- [Great CHANGELOG template](https://keepachangelog.com/en/1.1.0/)


## License

[MIT](https://choosealicense.com/licenses/mit/)

