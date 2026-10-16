package qa.ap.rest.test.users;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.ap.rest.base.BaseApiTest;
import qa.ap.rest.model.users.User;
import qa.ap.rest.model.users.UsersResponse;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("DummyJSON API")
@Feature("Users")
@DisplayName("Users API")
class UsersApiTest extends BaseApiTest {

    private static final String USER_SCHEMA = "jsonSchemas/users/userResponseSchema.json";
    private static final String USERS_SCHEMA = "jsonSchemas/users/usersResponseSchema.json";
    private static final String CREATED_USER_SCHEMA = "jsonSchemas/users/createdUserResponseSchema.json";
    private static final String ERROR_SCHEMA = "jsonSchemas/errorResponseSchema.json";

    @Test
    @Story("List users")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("1. Returns the default user page")
    @Description("Validates the collection schema, pagination metadata and default page size.")
    void shouldReturnDefaultUserPage() {
        UsersResponse body = usersClient.getAll().then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(USERS_SCHEMA))
                .extract().as(UsersResponse.class);

        assertAll(
                () -> assertEquals(30, body.getUsers().size()),
                () -> assertEquals(0, body.getSkip()),
                () -> assertEquals(30, body.getLimit()),
                () -> assertTrue(body.getTotal() >= body.getUsers().size()),
                () -> assertFalse(body.getUsers().get(0).getUsername().isBlank())
        );
    }

    @Test
    @Story("Get user detail")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("2. Returns an existing user by ID")
    void shouldReturnExistingUserById() {
        User user = usersClient.getById(1).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(USER_SCHEMA))
                .extract().as(User.class);

        assertAll(
                () -> assertEquals(1, user.getId()),
                () -> assertFalse(user.getFirstName().isBlank()),
                () -> assertFalse(user.getLastName().isBlank()),
                () -> assertFalse(user.getEmail().isBlank()),
                () -> assertTrue(user.getAge() > 0)
        );
    }

    @Test
    @Story("Paginate users")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("3. Applies limit and skip")
    void shouldApplyPagination() {
        UsersResponse body = usersClient.getAll(Map.of("limit", 5, "skip", 10)).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(USERS_SCHEMA))
                .extract().as(UsersResponse.class);

        assertAll(
                () -> assertEquals(5, body.getUsers().size()),
                () -> assertEquals(10, body.getSkip()),
                () -> assertEquals(5, body.getLimit()),
                () -> assertEquals(11, body.getUsers().get(0).getId())
        );
    }

    @Test
    @Story("Search users")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4. Searches users by text")
    void shouldSearchUsersByText() {
        String query = "John";
        UsersResponse body = usersClient.search(query).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(USERS_SCHEMA))
                .extract().as(UsersResponse.class);

        assertAll(
                () -> assertFalse(body.getUsers().isEmpty()),
                () -> assertTrue(body.getUsers().stream().allMatch(user -> containsQuery(user, query)))
        );
    }

    @Test
    @Story("Filter users")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("5. Filters users by gender")
    void shouldFilterUsersByGender() {
        UsersResponse body = usersClient.filter("gender", "female").then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(USERS_SCHEMA))
                .extract().as(UsersResponse.class);

        assertAll(
                () -> assertFalse(body.getUsers().isEmpty()),
                () -> assertTrue(body.getUsers().stream()
                        .allMatch(user -> "female".equals(user.getGender())))
        );
    }

    @Test
    @Story("Sort users")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6. Sorts users by first name in ascending order")
    void shouldSortUsersByFirstNameAscending() {
        UsersResponse body = usersClient.getAll(Map.of("sortBy", "firstName", "order", "asc")).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(USERS_SCHEMA))
                .extract().as(UsersResponse.class);

        List<User> users = body.getUsers();
        assertFalse(users.isEmpty());
        assertTrue(IntStream.range(0, users.size() - 1)
                .allMatch(index -> users.get(index).getFirstName()
                        .compareToIgnoreCase(users.get(index + 1).getFirstName()) <= 0));
    }

    @Test
    @Story("Create user")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("7. Simulates creating a valid user")
    void shouldCreateUser() {
        Map<String, Object> request = Map.of(
                "firstName", "Automation",
                "lastName", "Tester",
                "age", 30
        );

        User user = usersClient.create(request).then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath(CREATED_USER_SCHEMA))
                .extract().as(User.class);

        assertAll(
                () -> assertTrue(user.getId() > 0),
                () -> assertEquals(request.get("firstName"), user.getFirstName()),
                () -> assertEquals(request.get("lastName"), user.getLastName()),
                () -> assertEquals(request.get("age"), user.getAge())
        );
    }

    @Test
    @Story("Update user")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("8. Simulates updating an existing user")
    void shouldUpdateExistingUser() {
        String updatedLastName = "Automation";
        User user = usersClient.update(1, Map.of("lastName", updatedLastName)).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(USER_SCHEMA))
                .extract().as(User.class);

        assertAll(
                () -> assertEquals(1, user.getId()),
                () -> assertEquals(updatedLastName, user.getLastName())
        );
    }

    @Test
    @Story("Get user detail")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("9. Returns 404 for a non-existent user ID")
    void shouldReturnNotFoundForUnknownUserId() {
        Response response = usersClient.getById(999999);

        response.then()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath(ERROR_SCHEMA));
        assertTrue(response.jsonPath().getString("message").toLowerCase().contains("not found"));
    }

    @Test
    @Story("Get user detail")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("10. Returns 400 for a non-numeric user ID")
    void shouldReturnBadRequestForNonNumericUserId() {
        Response response = usersClient.getById("invalid-id");

        response.then()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath(ERROR_SCHEMA));
        assertTrue(response.jsonPath().getString("message").toLowerCase().contains("invalid"));
    }

    private boolean containsQuery(User user, String query) {
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        return List.of(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getMaidenName(),
                        user.getUsername(),
                        user.getEmail()
                ).stream()
                .filter(value -> value != null)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(normalizedQuery));
    }
}
