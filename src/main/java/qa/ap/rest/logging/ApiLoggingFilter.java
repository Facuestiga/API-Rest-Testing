package qa.ap.rest.logging;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApiLoggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiLoggingFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpecification,
                           FilterContext context) {
        LOGGER.info("Request: {} {}", request.getMethod(), request.getURI());

        Response response = context.next(request, responseSpecification);

        LOGGER.info("Response: {} {}\n{}",
                response.getStatusCode(),
                response.getStatusLine(),
                response.asPrettyString());
        return response;
    }
}
