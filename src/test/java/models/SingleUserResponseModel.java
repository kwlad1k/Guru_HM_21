package models;

import lombok.Data;

@Data
public class SingleUserResponseModel {

    private Data data;
    private Support support;

    @lombok.Data
    public class Data {
        String id, email, first_name, last_name, avatar;

    }

    @lombok.Data
    public class Support {
        String url, text;
    }

}
