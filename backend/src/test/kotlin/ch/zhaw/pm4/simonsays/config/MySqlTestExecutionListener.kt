package ch.zhaw.pm4.simonsays.config

import ch.zhaw.pm4.simonsays.IntegrationTest
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.utility.DockerImageName

/**
 * Extension to start MySQL in integration tests. Register with annotation or META-INF/spring.factories.
 *
 * Remarks:
 * - If Docker (or alternative) is not available, will not do anything.
 * - This is a workaround, because reuse between JDBC and R2DBC connection using connection string is not supported by
 *   testcontainers (origin: https://vcs.rch.cloud/sl-account-management/arrangement-service).
 */
class MySqlTestExecutionListener : TestExecutionListener {

    override fun beforeTestClass(testContext: TestContext) {
        if (IntegrationTest::class.java.isAssignableFrom(testContext.testClass)) {
            if (DockerClientFactory.instance().isDockerAvailable) {
                val mysql: MySQLContainer<*> = MySQLContainer(DockerImageName.parse("mysql:8.2"))
                    .withDatabaseName("simonsays")
                    .withCommand("mysqld", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci")
                    .waitingFor(HostPortWaitStrategy())

                mysql.start()
                System.setProperty(
                    "spring.datasource.url",
                    "jdbc:mysql://${mysql.host}:${mysql.getMappedPort(MySQLContainer.MYSQL_PORT)}/${mysql.databaseName}"
                )
                System.setProperty("spring.datasource.username", mysql.username)
                System.setProperty("spring.datasource.password", mysql.password)
                System.setProperty("spring.datasource.url", mysql.jdbcUrl)
                System.setProperty("spring.datasource.user", mysql.username)
                System.setProperty("spring.datasource.password", mysql.password)
                System.setProperty("driver-class-name", "com.mysql.cj.jdbc.Driver")
            } else {
                println("Docker (or alternative) is not available; assuming test db is already set up.")
            }
        }
    }
}