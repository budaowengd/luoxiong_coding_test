package com.lx.test.http

import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc: 对Gson序列化进行容错
 */
internal class IntTypeAdapter : TypeAdapter<Number?>() {
    override fun write(out: JsonWriter, value: Number?) {
        out.value(value)
    }

    override fun read(reader: JsonReader): Number? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        return try {
            val result = reader.nextString()
            if ("" == result) {
                null
            } else result.toInt()
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }
}

internal class LongTypeAdapter : TypeAdapter<Number?>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Number?) {
        out.value(value)
    }

    override fun read(reader: JsonReader): Number? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        return try {
            val result = reader.nextString()
            if ("" == result) {
                null
            } else result.toLong()
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }
}