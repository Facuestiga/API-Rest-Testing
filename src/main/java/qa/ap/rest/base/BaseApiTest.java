package qa.ap.rest.base;

import qa.ap.rest.client.ProductsClient;
import qa.ap.rest.config.RequestSpecificationFactory;

public abstract class BaseApiTest {

    protected final ProductsClient productsClient =
            new ProductsClient(RequestSpecificationFactory.create());
}
