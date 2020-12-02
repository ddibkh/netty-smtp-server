package com.mail.smtp.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResult
{
    enum DResult{SUCCESS, FAILURE}
    private DResult result;
    private String envToAddress;
    private String message;

    public String toString()
    {
        return "result = " + result +
                ", to = " + envToAddress +
                ", message = " + message;
    }
}
