package greencity.dto.event;

import greencity.dto.user.PlaceAuthorDto;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventSendEmailDto {

    private String eventTitle;

    private String description;

    private String eventType;

    private List<String> eventDayList;

    private int durationInDays; // duration of the event

    private LocalTime eventStartTime;

    private LocalTime eventEndTime;

    private String onlinePlace;

    private String offlinePlace;

    private List<String> imagePath;

    private PlaceAuthorDto author;

    private String secureToken;

}