package com.hcl.ecomm.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Magento API's Service Configuration")
public @interface MagentoServiceConfig {
    String domainNameDefaultValue = "localhost/magento2/rest";
    String servicePathDefaultValue = "/us/V1/products";
    String searchFieldDefaultValue = "store_id";
    String searchFieldValueDefaultValue = "2";
    String servicePathCartFetchItems = "/us/V1/guest-carts/";

    @AttributeDefinition(name = "DOMAIN NAME", description = "This is domain name", defaultValue = domainNameDefaultValue, type = AttributeType.STRING)
    String productService_domainName() default domainNameDefaultValue;

    @AttributeDefinition(name = "All Products Service Path", description = "This is the API path for getting all products for a store", defaultValue = servicePathDefaultValue, type = AttributeType.STRING)
    String productService_servicePath() default servicePathDefaultValue;

    @AttributeDefinition(name = "Product Product Search Criteria Field", description = "This is the field for search criteria", defaultValue = searchFieldDefaultValue, type = AttributeType.STRING)
    String productService_searchCriteriaField() default searchFieldDefaultValue;

    @AttributeDefinition(name = "Search Criteria Value", description = "This is the value for search criteria", defaultValue = searchFieldValueDefaultValue, type = AttributeType.STRING)
    String productService_searchCriteriaValue() default searchFieldValueDefaultValue;

    @AttributeDefinition(name = "Get Cart Items Service Path", description = "This is the API path for getting cart item details", defaultValue = {servicePathCartFetchItems} )
    String cartFetch_servicePath_string() default servicePathCartFetchItems;

}

