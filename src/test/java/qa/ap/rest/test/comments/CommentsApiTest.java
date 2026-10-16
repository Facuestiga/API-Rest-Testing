package qa.ap.rest.test.comments;

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
import qa.ap.rest.model.comments.Comment;
import qa.ap.rest.model.comments.CommentsResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("DummyJSON API")
@Feature("Comments")
@DisplayName("Comments API")
class CommentsApiTest extends BaseApiTest {

    private static final String COMMENT_SCHEMA = "jsonSchemas/comments/commentResponseSchema.json";
    private static final String COMMENTS_SCHEMA = "jsonSchemas/comments/commentsResponseSchema.json";
    private static final String CREATED_COMMENT_SCHEMA = "jsonSchemas/comments/createdCommentResponseSchema.json";
    private static final String ERROR_SCHEMA = "jsonSchemas/errorResponseSchema.json";

    @Test
    @Story("List comments")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("1. Returns the default comment page")
    @Description("Validates the collection schema, pagination metadata and default page size.")
    void shouldReturnDefaultCommentPage() {
        CommentsResponse body = commentsClient.getAll().then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(COMMENTS_SCHEMA))
                .extract().as(CommentsResponse.class);

        assertAll(
                () -> assertEquals(30, body.getComments().size()),
                () -> assertEquals(0, body.getSkip()),
                () -> assertEquals(30, body.getLimit()),
                () -> assertTrue(body.getTotal() >= body.getComments().size()),
                () -> assertNotNull(body.getComments().get(0).getUser())
        );
    }

    @Test
    @Story("Get comment detail")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("2. Returns an existing comment by ID")
    void shouldReturnExistingCommentById() {
        Comment comment = commentsClient.getById(1).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(COMMENT_SCHEMA))
                .extract().as(Comment.class);

        assertAll(
                () -> assertEquals(1, comment.getId()),
                () -> assertFalse(comment.getBody().isBlank()),
                () -> assertTrue(comment.getPostId() > 0),
                () -> assertFalse(comment.getUser().getUsername().isBlank()),
                () -> assertFalse(comment.getUser().getFullName().isBlank())
        );
    }

    @Test
    @Story("Paginate comments")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("3. Applies limit and skip")
    void shouldApplyPagination() {
        CommentsResponse body = commentsClient.getAll(Map.of("limit", 5, "skip", 10)).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(COMMENTS_SCHEMA))
                .extract().as(CommentsResponse.class);

        assertAll(
                () -> assertEquals(5, body.getComments().size()),
                () -> assertEquals(10, body.getSkip()),
                () -> assertEquals(5, body.getLimit()),
                () -> assertEquals(11, body.getComments().get(0).getId())
        );
    }

    @Test
    @Story("Sort comments")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4. Sorts comments by likes in descending order")
    void shouldSortCommentsByLikesDescending() {
        CommentsResponse body = commentsClient.getAll(Map.of("sortBy", "likes", "order", "desc")).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(COMMENTS_SCHEMA))
                .extract().as(CommentsResponse.class);

        List<Comment> comments = body.getComments();
        assertFalse(comments.isEmpty());
        assertTrue(IntStream.range(0, comments.size() - 1)
                .allMatch(index -> comments.get(index).getLikes() >= comments.get(index + 1).getLikes()));
    }

    @Test
    @Story("Filter comments")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("5. Returns comments for an existing post")
    void shouldReturnCommentsByPostId() {
        CommentsResponse body = commentsClient.getByPostId(6).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(COMMENTS_SCHEMA))
                .extract().as(CommentsResponse.class);

        assertAll(
                () -> assertFalse(body.getComments().isEmpty()),
                () -> assertEquals(body.getComments().size(), body.getTotal()),
                () -> assertTrue(body.getComments().stream().allMatch(comment -> comment.getPostId() == 6))
        );
    }

    @Test
    @Story("Create comment")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("6. Simulates creating a valid comment")
    void shouldCreateComment() {
        Map<String, Object> request = Map.of(
                "body", "Comment created by the REST Assured test suite",
                "postId", 3,
                "userId", 5
        );

        Comment comment = commentsClient.create(request).then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath(CREATED_COMMENT_SCHEMA))
                .extract().as(Comment.class);

        assertAll(
                () -> assertTrue(comment.getId() > 0),
                () -> assertEquals(request.get("body"), comment.getBody()),
                () -> assertEquals(request.get("postId"), comment.getPostId()),
                () -> assertEquals(request.get("userId"), comment.getUser().getId())
        );
    }

    @Test
    @Story("Update comment")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("7. Simulates updating an existing comment")
    void shouldUpdateExistingComment() {
        String updatedBody = "Updated by the REST Assured test suite";
        Comment comment = commentsClient.update(1, Map.of("body", updatedBody)).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(COMMENT_SCHEMA))
                .extract().as(Comment.class);

        assertAll(
                () -> assertEquals(1, comment.getId()),
                () -> assertEquals(updatedBody, comment.getBody())
        );
    }

    @Test
    @Story("Get comment detail")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("8. Returns 404 for a non-existent comment ID")
    void shouldReturnNotFoundForUnknownCommentId() {
        Response response = commentsClient.getById(999999);

        response.then()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath(ERROR_SCHEMA));
        assertTrue(response.jsonPath().getString("message").toLowerCase().contains("not found"));
    }

    @Test
    @Story("Get comment detail")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("9. Returns 404 for a non-numeric comment ID")
    void shouldReturnNotFoundForNonNumericCommentId() {
        Response response = commentsClient.getById("invalid-id");

        response.then()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath(ERROR_SCHEMA));
        assertTrue(response.jsonPath().getString("message").toLowerCase().contains("not found"));
    }

    @Test
    @Story("Filter comments")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("10. Returns 404 for a non-existent post")
    void shouldReturnNotFoundForUnknownPostId() {
        Response response = commentsClient.getByPostId(999999);

        response.then()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath(ERROR_SCHEMA));
        assertTrue(response.jsonPath().getString("message").toLowerCase().contains("not found"));
    }
}
