package spotify_chat.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession
import org.springframework.session.web.http.CookieSerializer
import org.springframework.session.web.http.DefaultCookieSerializer
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@EnableJdbcHttpSession
class SessionConfig {

    @Value("\${spotify_chat.domainName}")
    private lateinit var domainName: String


    @Bean
    fun dataSource(): EmbeddedDatabase =
        EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("org/springframework/session/jdbc/schema-h2.sql")
            .build()

    @Bean
    fun transactionManager(dataSource: DataSource): PlatformTransactionManager =
            DataSourceTransactionManager(dataSource)


    @Bean
    fun cookieSerializer(): CookieSerializer {
            val serializer = DefaultCookieSerializer()
            serializer.setDomainName(domainName)
            serializer.setUseHttpOnlyCookie(true)
            serializer.setSameSite(null)
            return serializer
        }
}