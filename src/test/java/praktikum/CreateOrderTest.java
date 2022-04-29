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


public class CreateOrderTest {
    private StellarBurgerClient stellarBurgerClient;
    private User user;
    private int statusCode;
    private String accessToken;
    ValidatableResponse createUserResponse;
    ValidatableResponse createOrderResponse;


    @Before
    public void setUp() {
        stellarBurgerClient = new StellarBurgerClient();
        user = RandomGenerator.getRandom();
        createUserResponse = stellarBurgerClient.create(user);
        accessToken = createUserResponse.extract().path("accessToken");
        accessToken = accessToken.replace("Bearer ", "");
    }

    @After
    public void tearDown() {
        stellarBurgerClient.delete(accessToken);
    }

    @Test
    @DisplayName("Create order with ingredients")
    public void testCreateOrderWithIngredients() {
        createOrderResponse = stellarBurgerClient.createOrder(accessToken, new Order(List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f")));
        statusCode = createOrderResponse.extract().statusCode();
        int actualOrderNumber = createOrderResponse.extract().path("order.number");
        assertThat("Заказ не может быть создан", statusCode, equalTo(SC_OK));
        assertThat("Некорректный номер заказа", actualOrderNumber, is(not(0)));
    }

    @Test
    @DisplayName("Create order without ingredients")
    public void testCreateOrderWithoutIngredients() {
        createOrderResponse = stellarBurgerClient.createOrder(accessToken, new Order(List.of()));
        statusCode = createOrderResponse.extract().statusCode();
        String actual = createOrderResponse.extract().path("message");
        assertThat("Заказ не может быть создан", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Нет сообщения 'Ingredient ids must be provided'", actual, equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order with incorrect hash")
    public void testCreateOrderWithIncorrectHash() {
        createOrderResponse = stellarBurgerClient.createOrder(accessToken, new Order(List.of("Э61c", "61c0c5a71d1f82001bdaaa6f")));
        statusCode = createOrderResponse.extract().statusCode();
        assertThat("Заказ не может быть создан", statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }

    @Test
    @DisplayName("Create order without authorization")
    public void testCreateOrderWithoutAuthorization() {
        createOrderResponse = stellarBurgerClient.createOrder(null, new Order(List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f")));
        statusCode = createOrderResponse.extract().statusCode();
        int actualOrderNumber = createOrderResponse.extract().path("order.number");
        assertThat("Заказ не может быть создан без авторизации", statusCode, equalTo(SC_OK));
        assertThat("Некорректный номер заказа", actualOrderNumber, is(not(0)));
    }

}
