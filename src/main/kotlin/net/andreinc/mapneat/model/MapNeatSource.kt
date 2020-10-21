package net.andreinc.mapneat.model

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.XML

class MapNeatSource {

    lateinit var content : String

    companion object {

        fun fromXml(xmlValue: String) : MapNeatSource {
            return MapNeatSource().apply {
                this.content = XML.toJSONObject(xmlValue).toString()
            }
        }

        fun fromJson(jsonValue: String) : MapNeatSource {
            return MapNeatSource().apply {
                this.content = jsonValue
            }
        }

        fun fromObject(obj: Any) : MapNeatSource {
            val map = ObjectMapper().convertValue(obj, Map::class.java)
            val json = ObjectMapper().writer().writeValueAsString(map)
            return fromJson(json)
        }
    }

    fun getStringContent() : String {
        return this.content
    }
}
