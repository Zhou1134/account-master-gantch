package com.edu.bupt.new_account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMS {
    private String content;
    private String phoneNumber;
    private String sendDate;
}
