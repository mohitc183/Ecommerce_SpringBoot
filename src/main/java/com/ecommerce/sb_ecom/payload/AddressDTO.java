package com.ecommerce.sb_ecom.payload;

import com.ecommerce.sb_ecom.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {


    private Long addressId;
    private String street;
    private String buildingName;
    private String city;
    private String state;
    private String country;
    private String pincode;

    private List<User> users = new ArrayList<>();
}
