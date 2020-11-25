package com.mail.smtp.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RequiredArgsConstructor
public class SmtpException extends RuntimeException
{
    @Getter
    @Min(400) @Max(599)
    private final int errorCode;

    @Override
    public String getMessage()
    {
        String message;
        switch( errorCode )
        {
            case 421:
                message = "service is unavailable because the server is shutting down";
                break;
            case 432:
                message = "password transition is needed";
                break;
            case 450:
                message = "requested mail action not taken: mailbox unavailable";
                break;
            case 451:
                message = "requested action aborted: local error in processing";
                break;
            case 452:
                message = "requested action not taken: insufficient system storage";
                break;
            case 454:
                message = "temporary authentication failure";
                break;
            case 458:
                message = "temporary queuing error";
                break;
            case 455:
                message = "server unable to accommodate parameters";
                break;
            case 500:
                message = "syntax error, command unrecognized";
                break;
            case 501:
                message = "syntax error, parameters or arguments";
                break;
            case 503:
                message = "bad sequence of commands";
                break;
            case 504:
                message = "unrecognized authentication type";
                break;
            case 521:
                message = "server does not accept mail";
                break;
            case 523:
                message = "encryption needed";
                break;
            case 530:
                message = "authentication required";
                break;
            case 535:
                message = "authentication credentials invalid";
                break;
            case 550:
                message = "requested action not taken : mailbox not found";
                break;
            case 551:
                message = "user not local; please try <forward-path>";
                break;
            case 555:
                message = "relaying deinied";
                break;
            case 552:
                message = "requested mail action aborted: exceeded storage allocation";
                break;
            case 553:
                message = "requested action not taken: mailbox name not allowed";
                break;
            case 554:
                message = "message too big for system";
                break;
            case 556:
                message = "domain does not accept mail";
                break;
            default:
                message = "intenal error";
                break;
        }

        return message;
    }

    public String getResponse()
    {
        return String.valueOf(errorCode) + " " + getMessage() + "\r\n";
    }
}
