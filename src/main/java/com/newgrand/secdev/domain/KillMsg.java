package com.newgrand.secdev.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KillMsg
{
    //@JsonProperty("Msg")
    private String Message ;
    private String IpAddress ;
    private String Devicetype ;
    private String UserId ;
    private String SessionID ;
}