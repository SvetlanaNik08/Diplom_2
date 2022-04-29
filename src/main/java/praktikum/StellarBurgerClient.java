package praktikum;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class StellarBurgerClient extends RestClient {
    private static final String REGISTER_PATH = "/api/auth/register";
    private static final String AUTH_PATH = "/api/auth/login";
    private static final String UPDATE_USER_PATH = "/api/auth/user";
    private static final String ORDER_PATH = "/api/orders";

    @Step("User creation")
    public ValidatableResponse create(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(REGISTER_PATH)
                .then();
    }

    @Step("User login")
    public ValidatableResponse login(UserCredentials credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(AUTH_PATH)
                .then();
    }

    @Step("Change user data")
    public ValidatableResponse changeUser(String accessToken, User user) {
        if(accessToken != null) {
            return given()
                    .spec(getBaseSpec())
                    .auth().oauth2(accessToken)
                    .and()
                    .body(user)
                    .when()
                    .patch(UPDATE_USER_PATH)
                    .then();
        } else { return given()
                .spec(getBaseSpec())
                .and()
                .body(user)
                .when()
                .patch(UPDATE_USER_PATH)
                .then();
        }
    }

    @Step("Create order")
    public ValidatableResponse createOrder(String accessToken, Order order) {
        if(accessToken != null) {
            return given()
                    .spec(getBaseSpec())
                    .auth().oauth2(accessToken)
                    .and()
                    .body(order)
                    .when()
                    .post(ORDER_PATH)
                    .then();
        } else { return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
        }
    }

    @Step("Get user orders")
    public ValidatableResponse getUserOrders(String accessToken) {
        if(accessToken != null) {
            return given()
                    .spec(getBaseSpec())
                    .auth().oauth2(accessToken)
                    .when()
                    .get(ORDER_PATH)
                    .then();
        } else { return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then();
        }
    }

    @Step("Delete user")
    public ValidatableResponse delete(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .when()
                .delete(UPDATE_USER_PATH)
                .then();
    }

}
