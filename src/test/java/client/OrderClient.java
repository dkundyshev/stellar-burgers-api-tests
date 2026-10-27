package client;

import config.Endpoints;
import io.qameta.allure.restassured.AllureRestAssured;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderClient {

    private RequestSpecification getBaseSpec() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(Endpoints.BASE_URI)
                .contentType("application/json");
    }

    @Step("Получение списка доступных ингредиентов")
    public Response getIngredients() {
        return getBaseSpec()
                .when()
                .get(Endpoints.INGREDIENTS);
    }

    @Step("Получение реальных id первых {count} ингредиентов")
    public List<String> getFirstIngredientIds(int count) {
        return getIngredients()
                .then()
                .extract()
                .jsonPath()
                .getList("data._id", String.class)
                .subList(0, count);
    }

    @Step("Создание заказа с ингредиентами (без авторизации)")
    public Response createOrder(List<String> ingredientIds) {
        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", ingredientIds);
        return getBaseSpec()
                .body(body)
                .when()
                .post(Endpoints.ORDERS);
    }

    @Step("Создание заказа с ингредиентами и авторизацией")
    public Response createOrder(List<String> ingredientIds, String accessToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", ingredientIds);
        return getBaseSpec()
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .post(Endpoints.ORDERS);
    }

    @Step("Создание заказа без ингредиентов (с авторизацией)")
    public Response createOrderWithoutIngredients(String accessToken) {
        Map<String, Object> body = Collections.emptyMap();
        return getBaseSpec()
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .post(Endpoints.ORDERS);
    }
}
