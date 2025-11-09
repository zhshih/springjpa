package com.example.springjpa.health;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

public class DatabaseHealthIndicatorTest {

    @Test
    void healthShouldBeUpWhenConnectionIsValid() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(true);

        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(dataSource);
        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertTrue(health.getDetails().containsKey("Database"));

        verify(connection).isValid(1);
        verify(connection).close();
    }

    @Test
    void healthShouldBeDownWhenConnectionIsInvalid() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(false);

        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(dataSource);
        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().containsKey("Database"));

        verify(connection).isValid(1);
        verify(connection).close();
    }

    @Test
    void healthShouldBeDownWhenSQLExceptionThrown() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenThrow(new SQLException("DB error"));

        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(dataSource);
        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().containsKey("error"));
    }
}
