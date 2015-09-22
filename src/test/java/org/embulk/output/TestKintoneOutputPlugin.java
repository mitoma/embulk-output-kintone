package org.embulk.output;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.embulk.EmbulkEmbed;
import org.embulk.config.ConfigLoader;
import org.embulk.config.ConfigSource;
import org.embulk.exec.ExecutionResult;
import org.embulk.spi.OutputPlugin;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.embulk.plugin.InjectedPluginSource.registerPluginTo;

public class TestKintoneOutputPlugin
{
    @Ignore("test is local only.")
    @Test
    public void testRunTest()
            throws IOException
    {
        EmbulkEmbed.Bootstrap bootstrap = new EmbulkEmbed.Bootstrap();
        bootstrap.addModules(new KintoneModule());
        EmbulkEmbed embulkEmbed = bootstrap.initializeCloseable();
        ConfigLoader configLoader = embulkEmbed.newConfigLoader();
        ConfigSource configSource = configLoader.fromYamlFile(new File("src/test/resources/config.yml"));
        ExecutionResult result = embulkEmbed.run(configSource);
    }

    static class KintoneModule
            implements Module
    {

        @Override
        public void configure(Binder binder)
        {
            // output plugins
            registerPluginTo(binder, OutputPlugin.class, "kintone", KintoneOutputPlugin.class);
        }
    }
}
