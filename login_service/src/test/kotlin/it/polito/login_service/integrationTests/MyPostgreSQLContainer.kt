package it.polito.login_service.integrationTests

import org.testcontainers.containers.PostgreSQLContainer

class MyPostgreSQLContainer(imageName: String) : PostgreSQLContainer<MyPostgreSQLContainer>(imageName)