package models;

import lombok.Data;

@Data
public class RegisterUserResponseModel {
    String email, password, id, createdAt;
}

