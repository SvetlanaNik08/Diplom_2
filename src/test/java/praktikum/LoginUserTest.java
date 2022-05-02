package praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LoginUserTest {
    private UserClient userClient;
    private User user;
    private UserCredentials credentials;
    ValidatableResponse createResponse;
    private int statusCode;
    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = RandomGenerator.getRandom();
        email = user.getEmail();
        password = user.getPassword();
        name = user.getName();
        createResponse= userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        accessToken = accessToken.replace("Bearer ", "");
        credentials = new UserCredentials(email, password);
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("User can login with valid credentials")
    public void testUserCanLoginWithValidCredentials() {
        ValidatableResponse loginResponse = userClient.login(credentials);
        statusCode = loginResponse.extract().statusCode();
        String actualEmail = loginResponse.extract().path("user.email");
        String actualName = loginResponse.extract().path("user.name");
        assertThat("Пользователь не может залогиниться", statusCode, equalTo(SC_OK));
        assertThat("Email в ответе не совпадает с email пользователя", actualEmail, equalTo(email));
        assertThat("Name в ответе не совпадает с именем пользователя", actualName, equalTo(name));
    }

    @Test
    @DisplayName("User cannot login with non-existent email")
    public void testUserCannotLoginWithNonExistentEmail() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(email.substring(0, 8) + "@yandex.ru", password));
        statusCode = loginResponse.extract().statusCode();
        String actual = loginResponse.extract().path("message");
        assertThat("Пользователь может залогиниться с несуществующим логином", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Нет сообщения 'email or password are incorrect'", actual, equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("User cannot login with non-existent password")
    public void testUserCannotLoginWithNonExistentPassword() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(email, password.substring(0, 8)));
        statusCode = loginResponse.extract().statusCode();
        String actual = loginResponse.extract().path("message");
        assertThat("Пользователь может залогиниться с несуществующим паролем", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Нет сообщения 'email or password are incorrect'", actual, equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("User cannot login with non-existent credentials")
    public void testUserCannotLoginWithNonExistentCredentials() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(email.substring(0, 8) + "@yandex.ru", password.substring(0, 8)));
        statusCode = loginResponse.extract().statusCode();
        String actual = loginResponse.extract().path("message");
        assertThat("Пользователь может залогиниться с несуществующими email и паролем", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Нет сообщения 'email or password are incorrect'", actual, equalTo("email or password are incorrect"));
    }
}
