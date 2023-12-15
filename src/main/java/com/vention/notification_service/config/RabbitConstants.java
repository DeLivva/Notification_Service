package com.vention.notification_service.config;

public interface RabbitConstants {

    // exchange names
    String DIRECT_EXCHANGE_NAME = "notification_direct_exchange";
    String DIRECT_ERROR_EXCHANGE_NAME = "direct_error_exchange";

    // queue names
    String NOTIFICATION_SERVICE_QUEUE = "notification_service_queue";

    // corresponding error queue names
    String NOTIFICATION_SERVICE_ERROR_QUEUE = "notification_service_error_queue";

    // routing keys
    String NOTIFICATION_SERVICE_ROUTING_KEY = "notification_service_routing_key";


    // corresponding error routing keys
    String NOTIFICATION_SERVICE_ERROR_ROUTING_KEY = "notification_service_error_routing_key";
}
