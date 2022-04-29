package praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetOrdersTest {
    private StellarBurgerClient stellarBurgerClient;
    private User user;
    private int statusCode;
    private int orderNumber;
    private String accessToken;
    ValidatableResponse createUserResponse;
    ValidatableResponse getOrdersResponse;
    ValidatableResponse createOrder;

    @Before
    public void setUp() {
        stellarBurgerClient = new StellarBurgerClient();
        user = RandomGenerator.getRandom();
        createUserResponse = stellarBurgerClient.create(user);
        accessToken = createUserResponse.extract().path("accessToken");
        accessToken = accessToken.replace("Bearer ", "");
        createOrder = stellarBurgerClient.createOrder(accessToken, new Order(List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f")));
        orderNumber = createOrder.extract().path("order.number");
    }

    @After
    public void tearDown() {
        stellarBurgerClient.delete(accessToken);
    }

    @Test
    @DisplayName("Get user orders with authorization")
    public void testGetOrdersWithAuthorization() {
        getOrdersResponse = stellarBurgerClient.getUserOrders(accessToken);
        statusCode = getOrdersResponse.extract().statusCode();
        int actual = getOrdersResponse.extract().path("orders.number.get(0)");
        assertThat("Нельзя получить список заказов авторизованного пользователя", statusCode, equalTo(SC_OK));
        assertThat("Некорректный номер заказа", actual, equalTo(orderNumber));
    }

    @Test
    @DisplayName("Get user orders without authorization")
    public void testGetOrdersWithoutAuthorization() {
        getOrdersResponse = stellarBurgerClient.getUserOrders(null);
        statusCode = getOrdersResponse.extract().statusCode();
        String actual = getOrdersResponse.extract().path("message");
        assertThat("Можно получить список заказов неавторизованного пользователя", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Нет сообщения 'You should be authorised'", actual, equalTo("You should be authorised"));
    }
}
