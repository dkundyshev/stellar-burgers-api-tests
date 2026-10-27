package tests;

import client.OrderClient;
import client.UserClient;
import generators.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderCreateTest {

    private final OrderClient orderClient = new OrderClient();
    private final UserClient userClient = new UserClient();
    private String accessTokenToCleanUp;

    @After
    public void tearDown() {
        if (accessTokenToCleanUp != null) {
            userClient.delete(accessTokenToCleanUp);
            accessTokenToCleanUp = null;
        }
    }

    private String registerUserAndGetToken() {
        User user = UserGenerator.getRandomUser();
        Response response = userClient.register(user);
        return response.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами возвращает успешный ответ")
    public void createOrderWithAuthAndIngredientsShouldReturnSuccess() {
        accessTokenToCleanUp = registerUserAndGetToken();
        List<String> ingredientIds = orderClient.getFirstIngredientIds(2);

        Response response = orderClient.createOrder(ingredientIds, accessTokenToCleanUp);

        response.then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации возвращает успешный ответ")
    public void createOrderWithoutAuthShouldReturnSuccess() {
        List<String> ingredientIds = orderClient.getFirstIngredientIds(2);

        Response response = orderClient.createOrder(ingredientIds);

        response.then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов возвращает ошибку 400")
    public void createOrderWithoutIngredientsShouldReturnError() {
        accessTokenToCleanUp = registerUserAndGetToken();

        Response response = orderClient.createOrderWithoutIngredients(accessTokenToCleanUp);

        response.then()
                .assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента возвращает ошибку 500")
    public void createOrderWithInvalidIngredientHashShouldReturnError() {
        accessTokenToCleanUp = registerUserAndGetToken();
        List<String> invalidIngredientIds = Arrays.asList("invalid-hash-123456");

        Response response = orderClient.createOrder(invalidIngredientIds, accessTokenToCleanUp);

        response.then()
                .assertThat()
                .statusCode(500);
    }
}