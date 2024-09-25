package greencity.dto.event;

import greencity.dto.user.PlaceAuthorDto;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventCommentSendEmailDto {
    private String eventTitle;
    private String commentText;
    private String commentAuthor;
    private String commentDate;
    private PlaceAuthorDto author;
    private String secureToken;
    private Long commentId;
    private Long eventId;
    private String commentLink;
}
