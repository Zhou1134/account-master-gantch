package com.edu.bupt.new_account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserMobile {
    private Integer id;

    private String name;

    private String username;

    private String password;

    private String phone;

    private String email;

    private String openid;
}
