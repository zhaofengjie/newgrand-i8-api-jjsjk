package com.newgrand.secdev.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataInfo {
    private String errorText;
    private String code;
    private String uuid;
    private String version;
    private String status;
}
