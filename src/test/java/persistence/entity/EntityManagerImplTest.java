package persistence.entity;

import domain.Order;
import domain.OrderItem;
import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.sql.ddl.h2.H2DeleteQueryBuilder;
import persistence.sql.ddl.h2.H2InsertQueryBuilder;
import persistence.sql.ddl.h2.H2SelectQueryBuilder;
import persistence.sql.ddl.h2.H2UpdateQueryBuilder;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class EntityManagerImplTest extends DatabaseTest {
    private QueryBuilder queryBuilder;

    @BeforeEach
    public void beforeEach() throws SQLException {
        super.beforeEach();
        queryBuilder = new QueryBuilder(new H2SelectQueryBuilder(), new H2DeleteQueryBuilder(), new H2InsertQueryBuilder(), new H2UpdateQueryBuilder(), jdbcTemplate);
    }

    @Test
    void find() throws SQLException {
        insertDb();
        EntityManager entityManager = new EntityManagerImpl(queryBuilder);

        Person actual = entityManager.find(Person.class, 1L);

        assertNotNull(actual);
    }

    @Test
    void find_hasJoin() throws SQLException {
        insertDb();
        EntityManager entityManager = new EntityManagerImpl(queryBuilder);

        Order actual = entityManager.find(Order.class, 1L);

        assertAll(
                () -> assertNotNull(actual),
                () -> assertEquals(actual.orderItemCount(), 2)
        );
    }

    @Test
    void find_order_items() {
        insertDb();
        EntityManager entityManager = new EntityManagerImpl(queryBuilder);

    }

    @Test
    void persist() throws IllegalAccessException {
        EntityManager entityManager = new EntityManagerImpl(queryBuilder);
        Person person = new Person(1L, "slow", 20, "email@email.com", 1);

        entityManager.persist(person);

        assertNotNull(entityManager.find(Person.class, 1L));
    }

    @Test
    void remove() throws IllegalAccessException {
        EntityManager entityManager = new EntityManagerImpl(queryBuilder);
        Person person = new Person(1L, "slow", 20, "email@email.com", 1);
        entityManager.persist(person);

        entityManager.remove(person);

        assertFalse(entityManager.contains(person));
    }

}
