package client;

import config.Endpoints;
import io.qameta.allure.restassured.AllureRestAssured;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.User;

public class UserClient {

        private RequestSpecification getBaseSpec() {
            return RestAssured.given()
                    .filter(new AllureRestAssured())
                    .baseUri(Endpoints.BASE_URI)
                    .contentType("application/json");
        }

        @Step("Регистрация нового пользователя")
        public Response register(User user) {
            return getBaseSpec()
                    .body(user)
                    .when()
                    .post(Endpoints.REGISTER);
        }

        @Step("Логин пользователя")
        public Response login(User.Credentials credentials) {
            return getBaseSpec()
                    .body(credentials)
                    .when()
                    .post(Endpoints.LOGIN);
        }

        @Step("Удаление пользователя по токену")
        public Response delete(String accessToken) {
            return getBaseSpec()
                    .header("Authorization", accessToken)
                    .when()
                    .delete(Endpoints.DELETE_USER);
        }
    }

