package qa.ap.rest.client.comments;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class CommentsClient {

    private static final String COMMENTS = "/comments";
    private final RequestSpecification requestSpecification;

    public CommentsClient(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public Response getAll() {
        return given().spec(requestSpecification).when().get(COMMENTS);
    }

    public Response getAll(Map<String, ?> queryParameters) {
        return given().spec(requestSpecification).queryParams(queryParameters)
                .when().get(COMMENTS);
    }

    public Response getById(Object commentId) {
        return given().spec(requestSpecification).pathParam("id", commentId)
                .when().get(COMMENTS + "/{id}");
    }

    public Response getByPostId(Object postId) {
        return given().spec(requestSpecification).pathParam("postId", postId)
                .when().get(COMMENTS + "/post/{postId}");
    }

    public Response create(Map<String, ?> comment) {
        return given().spec(requestSpecification).body(comment)
                .when().post(COMMENTS + "/add");
    }

    public Response update(int commentId, Map<String, ?> fields) {
        return given().spec(requestSpecification).pathParam("id", commentId).body(fields)
                .when().patch(COMMENTS + "/{id}");
    }
}
