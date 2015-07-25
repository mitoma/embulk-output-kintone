package org.embulk.output;

import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.Task;

public interface KintoneColumnOption
        extends Task
{
    @Config("type")
    @ConfigDefault("null")
    public String getType();
}
