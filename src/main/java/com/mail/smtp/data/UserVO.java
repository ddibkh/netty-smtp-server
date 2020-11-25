package com.mail.smtp.data;

import lombok.Data;

@Data
public class UserVO
{
    private boolean local;
    private String address;
    private String addressId;
    private String addressDomain;
    private String addressPerson;
    private int domainIndex;
    private int userIndex;

    public UserVO()
    {
        init();
    }

    public void init()
    {
        local = false;
        address = "";
        addressId = "";
        addressDomain = "";
        addressPerson = "";
        domainIndex = -1;
        userIndex = -1;
    }
}
