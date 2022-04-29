package praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class CreateUserTest {
    private StellarBurgerClient stellarBurgerClient;
    private User user;
    private int statusCode;
    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    public void setUp() {
        stellarBurgerClient = new StellarBurgerClient();
        user = RandomGenerator.getRandom();
        email = user.getEmail();
        password = user.getPassword();
        name = user.getName();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            accessToken = accessToken.replace("Bearer ", "");
            stellarBurgerClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("User can be created with valid credentials")
    public void testUserCanBeCreated() {
        ValidatableResponse createResponse = stellarBurgerClient.create(user);
        statusCode = createResponse.extract().statusCode();
        accessToken = createResponse.extract().path("accessToken");
        String actualRefreshToken = createResponse.extract().path("refreshToken");
        String actualEmail = createResponse.extract().path("user.email");
        String actualName = createResponse.extract().path("user.name");
        assertThat("Пользователя нельзя создать", statusCode, equalTo(SC_OK));
        assertThat("Email в ответе не совпадает с email пользователя", actualEmail, equalTo(email));
        assertThat("Name в ответе не совпадает с именем пользователя", actualName, equalTo(name));
        assertThat("Поле accessToken пустое или не содержит фрагмент 'Bearer '", accessToken, startsWith("Bearer "));
        assertThat("Поле refreshToken пустое", actualRefreshToken, notNullValue());
    }

    @Test
    @DisplayName("The same user cannot be created")
    public void testTheSameUserCannotBeCreated() {
        ValidatableResponse createResponse = stellarBurgerClient.create(user);
        accessToken = createResponse.extract().path("accessToken");
        createResponse = stellarBurgerClient.create(user);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");
        assertThat("Можно создать двух одинаковых пользователей", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Запрос не возвращает 'User already exists'", actual, equalTo("User already exists"));
    }

    @Test
    @DisplayName("User cannot be created with empty email")
    public void testUserCannotBeCreatedWithEmptyEmail() {
        user.setEmail("");
        ValidatableResponse createResponse = stellarBurgerClient.create(user);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");
        assertThat("Можно создать пользователя с пустым email", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Запрос не возвращает 'Email, password and name are required fields'", actual, equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("User cannot be created with empty password")
    public void testUserCannotBeCreatedWithEmptyPassword() {
        user.setPassword("");
        ValidatableResponse createResponse = stellarBurgerClient.create(user);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");
        assertThat("Можно создать пользователя с пустым паролем", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Запрос не возвращает 'Email, password and name are required fields'", actual, equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("User cannot be created with empty name")
    public void testUserCannotBeCreatedWithEmptyName() {
        user.setName("");
        ValidatableResponse createResponse = stellarBurgerClient.create(user);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");
        assertThat("Можно создать пользователя с пустым именем", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Запрос не возвращает 'Email, password and name are required fields'", actual, equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("User cannot be created with all empty fields")
    public void testUserCannotBeCreatedWithEmptyFields() {
        user.setEmail("");
        user.setPassword("");
        user.setName("");
        ValidatableResponse createResponse = stellarBurgerClient.create(user);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");
        assertThat("Можно создать пользователя со всеми пустыми полями", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Запрос не возвращает 'Email, password and name are required fields'", actual, equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("User cannot be created without email")
    public void testUserCannotBeCreatedWithoutEmail() {
        user.setEmail(null);
        ValidatableResponse createResponse = stellarBurgerClient.create(user);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");
        assertThat("При отсутствии email в JSON - ответ сервера не 403", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Запрос не возвращает 'Email, password and name are required fields'", actual, equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("User cannot be created without password")
    public void testUserCannotBeCreatedWithoutPassword() {
        user.setPassword(null);
        ValidatableResponse createResponse = stellarBurgerClient.create(user);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");
        assertThat("При отсутствии пароля в JSON - ответ севера не 403", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Запрос не возвращает 'Email, password and name are required fields'", actual, equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("User cannot be created without name")
    public void testUserCannotBeCreatedWithoutName() {
        user.setName(null);
        ValidatableResponse createResponse = stellarBurgerClient.create(user);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");
        assertThat("При отсутствии name в JSON - ответ сервера не 403", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Запрос не возвращает 'Email, password and name are required fields'", actual, equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("User cannot be created without all fields")
    public void testUserCannotBeCreatedWithoutAllFields() {
        user.setEmail(null);
        user.setPassword(null);
        user.setName(null);
        ValidatableResponse createResponse = stellarBurgerClient.create(user);
        statusCode = createResponse.extract().statusCode();
        String actual = createResponse.extract().path("message");
        assertThat("При отсутствии в JSON всех полей- ответ сервера не 403", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Запрос не возвращает 'Email, password and name are required fields'", actual, equalTo("Email, password and name are required fields"));
    }

}
