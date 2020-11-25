package com.mail.smtp.delivery;

import com.mail.smtp.data.MailAttribute;

import java.util.List;

public interface Delivery
{
    /*ListenableFuture< List<DeliveryResult> > delivery(List< String > to,
                                                           String queuePath,
                                                           MailAttribute mailAttribute);*/

    void delivery(List< String > to,
                                 String queuePath,
                                 MailAttribute mailAttribute);
}
