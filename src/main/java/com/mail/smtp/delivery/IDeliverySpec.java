package com.mail.smtp.delivery;

import com.mail.smtp.data.MailAttribute;

@FunctionalInterface
public interface IDeliverySpec
{
    DeliveryResult delivery(String toAddress,
                            String emlPath,
                            MailAttribute mailAttribute);
}
