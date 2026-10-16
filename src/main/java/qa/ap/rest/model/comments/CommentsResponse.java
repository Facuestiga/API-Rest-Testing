package qa.ap.rest.model.comments;

import java.util.List;

public class CommentsResponse {

    private List<Comment> comments;
    private int total;
    private int skip;
    private int limit;

    public List<Comment> getComments() {
        return comments;
    }

    public int getTotal() {
        return total;
    }

    public int getSkip() {
        return skip;
    }

    public int getLimit() {
        return limit;
    }
}
