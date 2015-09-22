package org.embulk.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.embulk.config.ConfigException;

import java.util.Locale;

public enum Mode
{

    INSERT;//, UPDATE, UPSERT;

    @JsonCreator
    public static Mode fromString(String value)
    {
        switch (value) {
            case "insert":
                return INSERT;
//            case "update":
//                return UPDATE;
//            case "upsert":
//                return UPSERT;
            default:
                throw new ConfigException(String.format(
                        "Unknown mode '%s'. Supported modes are insert",
                        value));
        }
    }

    @JsonValue
    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
