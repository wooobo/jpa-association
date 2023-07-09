package persistence.entity;

import domain.Order;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomJoinTableTest {

    @Test
    void test() {
        String actual = CustomJoinTable.getJoinTable(Order.class);

        assertThat(actual).isEqualTo("order_items");
    }

}
