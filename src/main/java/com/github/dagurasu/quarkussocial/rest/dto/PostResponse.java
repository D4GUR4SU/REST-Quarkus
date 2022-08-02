package com.github.dagurasu.quarkussocial.rest.dto;

import com.github.dagurasu.quarkussocial.domain.model.Post;
import lombok.Data;
import org.jboss.logging.annotations.Pos;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Data
public class PostResponse {

    private String text;
    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post) {
        var response = new PostResponse();
        response.setText(post.getText());
        response.setDateTime(post.getDataTime());

        return response;
    }
}
