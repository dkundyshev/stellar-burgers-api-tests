package generators;

import model.User;

import java.util.UUID;

public class UserGenerator {

    public static User getRandomUser() {
        String uniquePart = UUID.randomUUID().toString().substring(0, 8);
        String email = "test-" + uniquePart + "@yandex.ru";
        String password = "pass" + uniquePart;
        String name = "user" + uniquePart;
        return new User(email, password, name);
    }

    private UserGenerator() {

    }
}
