package praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ChangeUserTest {
    private UserClient userClient;
    private User user;
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
        createResponse = userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        accessToken = accessToken.replace("Bearer ", "");
    }

    @After
    public void tearDown() {
        if (statusCode != SC_UNAUTHORIZED) {
            UserCredentials credentials = new UserCredentials(email, password);
            ValidatableResponse loginResponse = userClient.login(credentials);
            accessToken = loginResponse.extract().path("accessToken");
            accessToken = accessToken.replace("Bearer ", "");
        }
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Authorized user can change email")
    public void testAuthorizedUserCanChangeEmail() {
        email = email.substring(0, 8) + "@yandex.ru";
        user = new User(email, password, name);
        ValidatableResponse changeResponse = userClient.changeUser(accessToken, user);
        statusCode = changeResponse.extract().statusCode();
        String actualEmail = changeResponse.extract().path("user.email");
        assertThat("Пользователь не изменен", statusCode, equalTo(SC_OK));
        assertThat("Email в ответе не совпадает с новым email пользователя", actualEmail, equalTo(email));
    }

    @Test
    @DisplayName("Authorized user can change password")
    public void testAuthorizedUserCanChangePassword() {
        email = email.substring(0, 8) + "@yandex.ru";
        password = password.substring(0, 8);
        user = new User(email, password, name);
        ValidatableResponse changeResponse = userClient.changeUser(accessToken, user);
        statusCode = changeResponse.extract().statusCode();
        assertThat("Пользователь не изменен", statusCode, equalTo(SC_OK));
    }

    @Test
    @DisplayName("Authorized user can change name")
    public void testAuthorizedUserCanChangeName() {
        email = email.substring(0, 8) + "@yandex.ru";
        name = name.substring(0, 8);
        user = new User(email, password, name);
        ValidatableResponse changeResponse = userClient.changeUser(accessToken, user);
        statusCode = changeResponse.extract().statusCode();
        String actualName = changeResponse.extract().path("user.name");
        assertThat("Пользователь не изменен", statusCode, equalTo(SC_OK));
        assertThat("Name в ответе не совпадает с новым именем пользователя", actualName, equalTo(name));
    }

    @Test
    @DisplayName("Unauthorized user cannot be changed")
    public void testUnauthorizedUserCannotBeChanged() {
        email = email.substring(0, 8) + "@yandex.ru";
        password = password.substring(0, 8);
        name = name.substring(0, 8);
        user = new User(email, password, name);
        ValidatableResponse changeResponse = userClient.changeUser(null, user);
        statusCode = changeResponse.extract().statusCode();
        String actual = changeResponse.extract().path("message");
        assertThat("При изменении пользователя без авторизации - ответ сервера не 401", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Нет сообщения 'You should be authorised'", actual, equalTo("You should be authorised"));
    }
}
