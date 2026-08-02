package tests;

import client.UserClient;
import generators.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserRegisterTest {

    private final UserClient userClient = new UserClient();
    private String accessTokenToCleanUp;

    @After
    public void tearDown() {
        if (accessTokenToCleanUp != null) {
            userClient.delete(accessTokenToCleanUp);
            accessTokenToCleanUp = null;
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя возвращает успешный ответ")
    public void registerUniqueUserShouldReturnSuccess() {
        User user = UserGenerator.getRandomUser();

        Response response = userClient.register(user);

        response.then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        accessTokenToCleanUp = response.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Повторная регистрация уже существующего пользователя возвращает ошибку 403")
    public void registerAlreadyExistingUserShouldReturnError() {
        User user = UserGenerator.getRandomUser();
        Response firstResponse = userClient.register(user);
        accessTokenToCleanUp = firstResponse.then().extract().path("accessToken");

        Response secondResponse = userClient.register(user);

        secondResponse.then()
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Регистрация без обязательного поля email возвращает ошибку 403")
    public void registerWithoutRequiredFieldShouldReturnError() {
        User userWithoutEmail = UserGenerator.getRandomUser().withoutEmail();

        Response response = userClient.register(userWithoutEmail);

        response.then()
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}