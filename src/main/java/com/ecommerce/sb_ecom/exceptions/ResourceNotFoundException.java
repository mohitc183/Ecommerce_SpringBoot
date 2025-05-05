package com.ecommerce.sb_ecom.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    String resourceName;
    String field;
    String fieldName;
    Long fieldId;


    public ResourceNotFoundException(){

    }

    public ResourceNotFoundException(Long fieldId, String field, String resourceName) {

        super(String.format("%s not found with %s : %d",resourceName,field,fieldId));
        this.fieldId = fieldId;
        this.field = field;
        this.resourceName = resourceName;
    }

    public ResourceNotFoundException(String resourceName, String field, String fieldName) {

        super(String.format("%s not found with %s : %s",resourceName,field,fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

}
