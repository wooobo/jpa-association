package persistence.entity;

import domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.sql.ddl.h2.H2DeleteQueryBuilder;
import persistence.sql.ddl.h2.H2InsertQueryBuilder;
import persistence.sql.ddl.h2.H2SelectQueryBuilder;
import persistence.sql.ddl.h2.H2UpdateQueryBuilder;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class ProxyEntityTest extends DatabaseTest {
    private QueryBuilder queryBuilder;

    @BeforeEach
    public void beforeEach() throws SQLException {
        super.beforeEach();
        queryBuilder = new QueryBuilder(new H2SelectQueryBuilder(), new H2DeleteQueryBuilder(), new H2InsertQueryBuilder(), new H2UpdateQueryBuilder(), jdbcTemplate);
    }

    @Test
    void test() throws NoSuchFieldException, IllegalAccessException {
        Order order = new Order(1L, "order11");
//        Field oneToManyField = order.getClass().getDeclaredField("orderItems");
        Order proxy = (Order) ProxyEntity.createProxy(
                "orderItems",
                order,
                queryBuilder
        );

        String name = proxy.getName();
//
        System.out.println(name);
        int actual = proxy.orderItemCount();
        assertThat(actual).isEqualTo(1);
        assertThat(proxy.orderItemCount()).isEqualTo(1);
        assertThat(proxy.orderItemCount()).isEqualTo(1);
    }

}
