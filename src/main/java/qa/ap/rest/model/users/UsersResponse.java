package qa.ap.rest.model.users;

import java.util.List;

public class UsersResponse {

    private List<User> users;
    private int total;
    private int skip;
    private int limit;

    public List<User> getUsers() {
        return users;
    }

    public int getTotal() {
        return total;
    }

    public int getSkip() {
        return skip;
    }

    public int getLimit() {
        return limit;
    }
}
