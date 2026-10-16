package qa.ap.rest.base;

import qa.ap.rest.client.comments.CommentsClient;
import qa.ap.rest.client.products.ProductsClient;
import qa.ap.rest.client.users.UsersClient;
import qa.ap.rest.config.RequestSpecificationFactory;

public abstract class BaseApiTest {

    protected final ProductsClient productsClient =
            new ProductsClient(RequestSpecificationFactory.create());

    protected final CommentsClient commentsClient =
            new CommentsClient(RequestSpecificationFactory.create());

    protected final UsersClient usersClient =
            new UsersClient(RequestSpecificationFactory.create());
}
