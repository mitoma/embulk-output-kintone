package org.embulk.output;

import org.embulk.config.CommitReport;
import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigDiff;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.config.TaskSource;
import org.embulk.spi.Exec;
import org.embulk.spi.OutputPlugin;
import org.embulk.spi.Schema;
import org.embulk.spi.TransactionalPageOutput;

import java.util.List;
import java.util.Map;

public class KintoneOutputPlugin
        implements OutputPlugin
{
    @Override
    public ConfigDiff transaction(ConfigSource config, Schema schema,
            int taskCount, OutputPlugin.Control control)
    {
        PluginTask task = config.loadConfig(PluginTask.class);
        // retryable (idempotent) output:
        // return resume(task.dump(), schema, taskCount, control);

        // non-retryable (non-idempotent) output:
        control.run(task.dump());
        return Exec.newConfigDiff();
    }

    @Override
    public ConfigDiff resume(TaskSource taskSource, Schema schema, int taskCount,
            OutputPlugin.Control control)
    {
        throw new UnsupportedOperationException(
                "kintone output plugin does not support resuming");
    }

    @Override
    public void cleanup(TaskSource taskSource, Schema schema, int taskCount,
            List<CommitReport> successCommitReports)
    {
    }

    @Override
    public TransactionalPageOutput open(TaskSource taskSource, Schema schema,
            int taskIndex)
    {
        PluginTask task = taskSource.loadTask(PluginTask.class);
        return new KintonePageOutput(task, schema);
    }

    public interface PluginTask
            extends Task
    {
        @Config("domain")
        public String getDomain();

        @Config("app_id")
        public long getAppId();

        @Config("api_token")
        public String getApiToken();

        @Config("column_options")
        @ConfigDefault("{}")
        public Map<String, KintoneColumnOption> getColumnOptions();

        @Config("mode")
        @ConfigDefault("insert")
        public Mode getMode();
    }
}
