package com.vention.notification_service.config;

public interface RabbitConstants {

    // exchange names
    String DIRECT_EXCHANGE_NAME = "notification_direct_exchange";
    String DIRECT_ERROR_EXCHANGE_NAME = "direct_error_exchange";

    // queue names
    String AUTHORIZATION_SERVICE_QUEUE = "authorization_service_queue";
    String CORE_SERVICE_QUEUE = "core_service_queue";
    String DISPUTE_SERVICE_QUEUE = "dispute_service_queue";

    // corresponding error queue names
    String AUTHORIZATION_SERVICE_ERROR_QUEUE = "authorization_service_error_queue";
    String CORE_SERVICE_ERROR_QUEUE = "core_service_error_queue";
    String DISPUTE_SERVICE_ERROR_QUEUE = "dispute_service_error_queue";

    // routing keys
    String AUTHORIZATION_SERVICE_ROUTING_KEY = "authorization_service_routing_key";
    String CORE_SERVICE_ROUTING_KEY = "core_service_routing_key";
    String DISPUTE_SERVICE_ROUTING_KEY = "dispute_service_routing_key";


    // corresponding error routing keys
    String AUTHORIZATION_SERVICE_ERROR_ROUTING_KEY = "authorization_service_error_routing_key";
    String CORE_SERVICE_ERROR_ROUTING_KEY = "core_service_error_routing_key";
    String DISPUTE_SERVICE_ERROR_ROUTING_KEY = "dispute_service_error_routing_key";
}
