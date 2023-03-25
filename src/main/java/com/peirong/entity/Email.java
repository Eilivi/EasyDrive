package com.peirong.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class Email implements Serializable {
    private String sendTo;
    private String subject;
    private String text;
    private String filePath;
}