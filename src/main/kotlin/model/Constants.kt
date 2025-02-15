package model

/** Specifies the different API URL links or endpoints that can be used for requests.
 */
enum class ApiUrlLink (val value : String, ){
    STANDART_VENUE("https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/"),
    STATIC_API("static"),
    DYNAMIC_API("dynamic")
}

/**
 * Defines the fields used in distance range objects to specify distance thresholds and
 * corresponding delivery fee parameters.
 */
enum class DistanceRangeObjectFields { min, max, a, b }