package qa.ap.rest.client.users;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UsersClient {

    private static final String USERS = "/users";
    private final RequestSpecification requestSpecification;

    public UsersClient(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public Response getAll() {
        return given().spec(requestSpecification).when().get(USERS);
    }

    public Response getAll(Map<String, ?> queryParameters) {
        return given().spec(requestSpecification).queryParams(queryParameters)
                .when().get(USERS);
    }

    public Response getById(Object userId) {
        return given().spec(requestSpecification).pathParam("id", userId)
                .when().get(USERS + "/{id}");
    }

    public Response search(String query) {
        return given().spec(requestSpecification).queryParam("q", query)
                .when().get(USERS + "/search");
    }

    public Response filter(String key, Object value) {
        return given().spec(requestSpecification)
                .queryParam("key", key)
                .queryParam("value", value)
                .when().get(USERS + "/filter");
    }

    public Response create(Map<String, ?> user) {
        return given().spec(requestSpecification).body(user)
                .when().post(USERS + "/add");
    }

    public Response update(int userId, Map<String, ?> fields) {
        return given().spec(requestSpecification).pathParam("id", userId).body(fields)
                .when().patch(USERS + "/{id}");
    }
}
