package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

    private String email;
    private String password;
    private String name;

    public User withoutEmail() {
        return new User(null, this.password, this.name);
    }

    public Credentials toCredentials() {
        return new Credentials(this.email, this.password);
    }

    @Data
    @AllArgsConstructor
    public static class Credentials {
        private final String email;
        private final String password;
    }
}