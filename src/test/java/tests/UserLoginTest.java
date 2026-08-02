package tests;

import client.UserClient;
import generators.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;


public class UserLoginTest {
    private final UserClient userClient = new UserClient();
    private User registeredUser;
    private String accessTokenToCleanUp;

    @Before
    public void setUp(){
        registeredUser = UserGenerator.getRandomUser();
        Response response = userClient.register(registeredUser);
        accessTokenToCleanUp = response.then().extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessTokenToCleanUp != null) {
            userClient.delete(accessTokenToCleanUp);
            accessTokenToCleanUp = null;
        }
    }

    @Test
    @DisplayName("Вход под существующим пользователем возвращает успешный ответ")
    public void loginWithExistingUserShouldReturnSuccess() {
        Response response = userClient.login(registeredUser.toCredentials());

        response.then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(registeredUser.getEmail()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем возвращает ошибку 401")
    public void loginWithWrongCredentialsShouldReturnError() {
        User.Credentials wrongCredentials = new User.Credentials(registeredUser.getEmail(), "wrong-password");

        Response response = userClient.login(wrongCredentials);

        response.then()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
