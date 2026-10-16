package qa.ap.rest.client.products;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ProductsClient {

    private static final String PRODUCTS = "/products";
    private final RequestSpecification requestSpecification;

    public ProductsClient(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public Response getAll() {
        return given().spec(requestSpecification).when().get(PRODUCTS);
    }

    public Response getById(Object productId) {
        return given().spec(requestSpecification).pathParam("id", productId)
                .when().get(PRODUCTS + "/{id}");
    }

    public Response getAll(Map<String, ?> queryParameters) {
        return given().spec(requestSpecification).queryParams(queryParameters)
                .when().get(PRODUCTS);
    }

    public Response search(String query) {
        return given().spec(requestSpecification).queryParam("q", query)
                .when().get(PRODUCTS + "/search");
    }

    public Response getByCategory(String category) {
        return given().spec(requestSpecification).pathParam("category", category)
                .when().get(PRODUCTS + "/category/{category}");
    }

    public Response create(Map<String, ?> product) {
        return given().spec(requestSpecification).body(product)
                .when().post(PRODUCTS + "/add");
    }

    public Response update(int productId, Map<String, ?> fields) {
        return given().spec(requestSpecification).pathParam("id", productId).body(fields)
                .when().patch(PRODUCTS + "/{id}");
    }
}
