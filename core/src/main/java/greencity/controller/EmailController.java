package greencity.controller;

import greencity.constant.ErrorMessage;
import greencity.constant.HttpStatuses;
import greencity.dto.econews.EcoNewsForSendEmailDto;
import greencity.dto.event.EventCommentSendEmailDto;
import greencity.dto.event.EventSendEmailDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.violation.UserViolationMailDto;
import greencity.exception.exceptions.BadVerifyEmailTokenException;
import greencity.message.SendChangePlaceStatusEmailMessage;
import greencity.message.SendHabitNotification;
import greencity.message.SendReportEmailMessage;
import greencity.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController {
    @Autowired
    private final EmailService emailService;

    /**
     * Method for sending news for users who subscribed for updates.
     *
     * @param message - object with all necessary data for sending email
     * @author Taras Kavkalo
     */
    @PostMapping("/addEcoNews")
    public ResponseEntity<Object> addEcoNews(@RequestBody EcoNewsForSendEmailDto message) {
        emailService.sendCreatedNewsForAuthor(message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for sending event creation notification to user.
     *
     * @param message - object with all necessary data for sending email about the created event.
     */
    @PostMapping("/addEvent")
    public ResponseEntity<Object> addEvent(@RequestBody EventSendEmailDto message) {
        emailService.sendCreatedEventForAuthor(message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for sending notification to user to whose Event,comment was added
     *
     * @param message - object with all necessary data for sending email
     * @author Nazar Vavrushchak
     */
    @PostMapping("/addEventComment")
    public ResponseEntity<Object> addEventComment(@RequestBody EventCommentSendEmailDto message) {
        emailService.sendNotificationToTheOrganizerAboutTheComment(message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for sending notification to userss who subscribed for updates about
     * added new places.
     *
     * @param message - object with all necessary data for sending email
     * @author Taras Kavkalo
     */
    @PostMapping("/sendReport")
    public ResponseEntity<Object> sendReport(@RequestBody SendReportEmailMessage message) {
        emailService.sendAddedNewPlacesReportEmail(message.getSubscribers(), message.getCategoriesDtoWithPlacesDtoMap(),
                message.getEmailNotification());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for sending simple notification to {@code User} about status change.
     *
     * @param message - object with all necessary data for sending email
     * @author Taras Kavkalo
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @PostMapping("/changePlaceStatus")
    public ResponseEntity<Object> changePlaceStatus(@Valid @RequestBody SendChangePlaceStatusEmailMessage message, @ApiIgnore Principal principal) {

        if (principal == null) {
            throw new BadVerifyEmailTokenException(ErrorMessage.USER_IS_UNAUTHORIZED);
        }

        emailService.sendChangePlaceStatusEmail(
                message.getAuthorFirstName(),
                message.getPlaceName(),
                message.getPlaceStatus(),
                message.getAuthorEmail()
        );
        return ResponseEntity.ok().build();
    public ResponseEntity<Object> changePlaceStatus(@RequestBody SendChangePlaceStatusEmailMessage message) {
        emailService.sendChangePlaceStatusEmail(message.getAuthorFirstName(), message.getPlaceName(),
                message.getPlaceStatus(), message.getAuthorEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Sends email notification about not marked habits during 3 last days.
     *
     * @param sendHabitNotification - object with all necessary data for sending
     *                              email
     * @author Taras Kavkalo
     */
    @PostMapping("/sendHabitNotification")
    public ResponseEntity<Object> sendHabitNotification(@Valid @RequestBody SendHabitNotification sendHabitNotification) {
        emailService.sendHabitNotification(sendHabitNotification.getName(), sendHabitNotification.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Sends email notification about violation to user on email.
     *
     * @param dto {@link UserViolationMailDto} - object with all necessary data for
     *            sending email.
     * @author Zakhar Veremchuk
     */
    @PostMapping("/sendUserViolation")
    public ResponseEntity<Object> sendUserViolation(@RequestBody UserViolationMailDto dto) {
        emailService.sendUserViolationEmail(dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Sends notification to user on email.
     *
     * @param notification {@link NotificationDto} - object with all necessary data
     *                     for sending notification via email.
     * @param email        {@link String} - user's email.
     * @author Ann Sakhno
     */
    @Operation(summary = "Send notification to user via email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PostMapping("/notification")
    public ResponseEntity<Object> sendUserNotification(@RequestBody NotificationDto notification,
                                                       @RequestParam("email") String email) {
        emailService.sendNotificationByEmail(notification, email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
