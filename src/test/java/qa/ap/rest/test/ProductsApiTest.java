package qa.ap.rest.test;

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
import qa.ap.rest.model.Product;
import qa.ap.rest.model.ProductsResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("DummyJSON API")
@Feature("Products")
@DisplayName("Products API")
class ProductsApiTest extends BaseApiTest {

    @Test
    @Story("List products")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("1. Returns the default product page")
    @Description("Validates status, pagination metadata and the basic product contract.")
    void shouldReturnDefaultProductPage() {
        Response response = productsClient.getAll();
        ProductsResponse body = response.then().statusCode(200).extract().as(ProductsResponse.class);

        assertAll(
                () -> assertEquals(30, body.getProducts().size()),
                () -> assertEquals(0, body.getSkip()),
                () -> assertEquals(30, body.getLimit()),
                () -> assertTrue(body.getTotal() >= body.getProducts().size()),
                () -> assertNotNull(body.getProducts().get(0).getTitle()),
                () -> assertNotNull(body.getProducts().get(0).getPrice())
        );
    }

    @Test
    @Story("Get product detail")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("2. Returns an existing product by ID")
    void shouldReturnExistingProductById() {
        Product product = productsClient.getById(1).then().statusCode(200).extract().as(Product.class);

        assertAll(
                () -> assertEquals(1, product.getId()),
                () -> assertFalse(product.getTitle().isBlank()),
                () -> assertFalse(product.getCategory().isBlank()),
                () -> assertTrue(product.getPrice().compareTo(BigDecimal.ZERO) > 0)
        );
    }

    @Test
    @Story("Paginate products")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("3. Applies limit, skip and field projection")
    void shouldApplyPaginationAndProjection() {
        ProductsResponse body = productsClient.getAll(Map.of(
                        "limit", 5,
                        "skip", 10,
                        "select", "title,price"
                ))
                .then().statusCode(200).extract().as(ProductsResponse.class);

        assertAll(
                () -> assertEquals(5, body.getProducts().size()),
                () -> assertEquals(10, body.getSkip()),
                () -> assertEquals(5, body.getLimit()),
                () -> assertTrue(body.getProducts().stream().allMatch(product -> product.getId() > 0)),
                () -> assertTrue(body.getProducts().stream().allMatch(product -> product.getTitle() != null)),
                () -> assertTrue(body.getProducts().stream().allMatch(product -> product.getPrice() != null))
        );
    }

    @Test
    @Story("Search products")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4. Searches products by text")
    void shouldSearchProductsByText() {
        ProductsResponse body = productsClient.search("phone")
                .then().statusCode(200).extract().as(ProductsResponse.class);

        assertAll(
                () -> assertFalse(body.getProducts().isEmpty()),
                () -> assertTrue(body.getProducts().stream().allMatch(this::containsPhone))
        );
    }

    @Test
    @Story("Filter products")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("5. Returns products for an existing category")
    void shouldReturnProductsByCategory() {
        ProductsResponse body = productsClient.getByCategory("smartphones")
                .then().statusCode(200).extract().as(ProductsResponse.class);

        assertAll(
                () -> assertFalse(body.getProducts().isEmpty()),
                () -> assertTrue(body.getProducts().stream()
                        .allMatch(product -> "smartphones".equals(product.getCategory())))
        );
    }

    @Test
    @Story("Create product")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("6. Simulates creating a valid product")
    void shouldCreateProduct() {
        Map<String, Object> request = Map.of(
                "title", "Automation Test Product",
                "description", "Created by the REST Assured test suite",
                "category", "test-automation",
                "price", 99.99
        );
        Product product = productsClient.create(request)
                .then().statusCode(201).extract().as(Product.class);

        assertAll(
                () -> assertTrue(product.getId() > 0),
                () -> assertEquals(request.get("title"), product.getTitle()),
                () -> assertEquals(request.get("description"), product.getDescription()),
                () -> assertEquals(request.get("category"), product.getCategory()),
                () -> assertEquals(0, product.getPrice().compareTo(BigDecimal.valueOf(99.99)))
        );
    }

    @Test
    @Story("Update product")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("7. Simulates updating an existing product")
    void shouldUpdateExistingProduct() {
        Product product = productsClient.update(1, Map.of("title", "Updated Automation Product"))
                .then().statusCode(200).extract().as(Product.class);

        assertAll(
                () -> assertEquals(1, product.getId()),
                () -> assertEquals("Updated Automation Product", product.getTitle())
        );
    }

    @Test
    @Story("Get product detail")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("8. Returns 404 for a non-existent product ID")
    void shouldReturnNotFoundForUnknownProductId() {
        Response response = productsClient.getById(999999);

        response.then().statusCode(404);
        assertTrue(response.jsonPath().getString("message").toLowerCase().contains("not found"));
    }

    @Test
    @Story("Search products")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("9. Returns an empty page when the search has no matches")
    void shouldReturnEmptyPageForUnknownSearchTerm() {
        ProductsResponse body = productsClient.search("term-that-cannot-match-123456789")
                .then().statusCode(200).extract().as(ProductsResponse.class);

        assertAll(
                () -> assertEquals(0, body.getTotal()),
                () -> assertTrue(body.getProducts().isEmpty())
        );
    }

    @Test
    @Story("Filter products")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("10. Returns an empty page for a non-existent category")
    void shouldReturnEmptyPageForUnknownCategory() {
        ProductsResponse body = productsClient.getByCategory("category-that-does-not-exist")
                .then().statusCode(200).extract().as(ProductsResponse.class);

        assertAll(
                () -> assertEquals(0, body.getTotal()),
                () -> assertTrue(body.getProducts().isEmpty())
        );
    }

    private boolean containsPhone(Product product) {
        List<String> searchableFields = List.of(
                product.getTitle() == null ? "" : product.getTitle(),
                product.getDescription() == null ? "" : product.getDescription(),
                product.getCategory() == null ? "" : product.getCategory()
        );
        return searchableFields.stream().anyMatch(value -> value.toLowerCase().contains("phone"));
    }
}
