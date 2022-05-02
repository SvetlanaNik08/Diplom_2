package praktikum;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class UserClient extends RestClient {
    private static final String REGISTER_PATH = "/api/auth/register";
    private static final String AUTH_PATH = "/api/auth/login";
    private static final String UPDATE_USER_PATH = "/api/auth/user";

    @Step("User creation")
    public ValidatableResponse createUser(User user) {
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

    @Step("Delete user")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .when()
                .delete(UPDATE_USER_PATH)
                .then();
    }
}
