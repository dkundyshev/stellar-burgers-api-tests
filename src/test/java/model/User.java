package model;

public class User {

    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getEmail() {return email;}

    public String getPassword() {return password;}

    public String getName() {return name;}

    public User withoutEmail() {
        return new User(null, this.password, this.name);
    }

    public static class Credentials {
        private final String email;
        private final String password;

        public Credentials(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }

    public Credentials toCredentials() {
        return new Credentials(this.email, this.password);
    }
}
