package dev.demo.sisepp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base de las pruebas de integración.
 *
 * <p>Levanta un PostgreSQL real en un contenedor y deja que Flyway aplique
 * sobre él las migraciones del proyecto. Es deliberado no usar H2: el sistema
 * depende de funciones que H2 no implementa —{@code pgcrypto} para los hashes
 * de contraseña en las migraciones de usuarios, {@code LEFT JOIN LATERAL} y
 * {@code COUNT(*) FILTER (WHERE ...)} en el cálculo del avance— y probar
 * contra un motor distinto al de producción dejaría escapar justo los errores
 * de SQL que estas pruebas deben atrapar.
 *
 * <p><b>Patrón singleton.</b> El contenedor se arranca una sola vez en el
 * bloque estático y nadie lo detiene. No se usan {@code @Testcontainers} ni
 * {@code @Container} a propósito: esa pareja para el contenedor al terminar
 * cada clase de prueba, de modo que la segunda clase encontraría un
 * PostgreSQL ya muerto y Hikari se quedaría reintentando conexiones cerradas.
 * Al final de la suite lo limpia Ryuk, el contenedor vigilante que levanta
 * Testcontainers por su cuenta.
 *
 * <p>Requiere Docker corriendo en la máquina. En GitHub Actions ya viene.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("sisepp_test")
            .withUsername("sisepp_test")
            .withPassword("sisepp_test");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void datasourceDelContenedor(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
