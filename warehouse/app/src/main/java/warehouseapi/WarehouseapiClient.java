/*
 * Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package warehouseapi;

import java.util.*;



@com.amazonaws.mobileconnectors.apigateway.annotation.Service(endpoint = "https://3u2yuqwjej.execute-api.eu-west-2.amazonaws.com/staging")
public interface WarehouseapiClient {


    /**
     * A generic invoker to invoke any API Gateway endpoint.
     * @param request
     * @return ApiResponse
     */
    com.amazonaws.mobileconnectors.apigateway.ApiResponse execute(com.amazonaws.mobileconnectors.apigateway.ApiRequest request);
    
    /**
     * 
     * 
     * @return void
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/crm/deal/create", method = "OPTIONS")
    void crmDealCreateOptions();
    
    /**
     * 
     * 
     * @return void
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/crm/deal/create/{proxy+}", method = "OPTIONS")
    void crmDealCreateProxyOptions();
    
    /**
     * 
     * 
     * @return void
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/lot/{lotid}/sold", method = "OPTIONS")
    void lotLotidSoldOptions();
    
    /**
     * 
     * 
     * @return void
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/lot/{lotid}/sold/{proxy+}", method = "OPTIONS")
    void lotLotidSoldProxyOptions();
    
    /**
     * 
     * 
     * @return void
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/telegrampublish", method = "OPTIONS")
    void telegrampublishOptions();
    
    /**
     * 
     * 
     * @return void
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/telegrampublish/{proxy+}", method = "OPTIONS")
    void telegrampublishProxyOptions();
    
}

