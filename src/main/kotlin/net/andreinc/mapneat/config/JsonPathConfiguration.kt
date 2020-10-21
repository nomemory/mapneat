package net.andreinc.mapneat.config

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.spi.json.JacksonJsonProvider

/**
 * MapNeat is using for the moment the JacksonJSONProvider as the json provider for the library.
 *
 * Changing the implementation to something else (e.g.): gson will change the behavior of the
 * Shift Operation and everything using JsonPath functionality
 *
 */
object JsonPathConfiguration {
    val mapNeatConfiguration : Configuration = Configuration
                                                .builder()
                                                .jsonProvider(JacksonJsonProvider())
                                                .build()
}