package qa.ap.rest.model.comments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

    private int id;
    private String body;
    private int postId;
    private int likes;
    private CommentUser user;

    public int getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public int getPostId() {
        return postId;
    }

    public int getLikes() {
        return likes;
    }

    public CommentUser getUser() {
        return user;
    }
}
