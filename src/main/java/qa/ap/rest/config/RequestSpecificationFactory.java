package qa.ap.rest.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import qa.ap.rest.logging.ApiLoggingFilter;

public final class RequestSpecificationFactory {

    private RequestSpecificationFactory() {
    }

    public static RequestSpecification create() {
        return new RequestSpecBuilder()
                .setBaseUri(TestConfig.baseUri())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .addFilter(new ApiLoggingFilter())
                .build();
    }
}
