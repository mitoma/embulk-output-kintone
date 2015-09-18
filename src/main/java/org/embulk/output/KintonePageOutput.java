package org.embulk.output;

import com.cybozu.kintone.database.Connection;
import com.cybozu.kintone.database.Record;
import com.cybozu.kintone.database.exception.DBException;
import org.embulk.config.TaskReport;
import org.embulk.output.KintoneOutputPlugin.PluginTask;
import org.embulk.spi.Column;
import org.embulk.spi.Exec;
import org.embulk.spi.Page;
import org.embulk.spi.PageReader;
import org.embulk.spi.Schema;
import org.embulk.spi.TransactionalPageOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KintonePageOutput
        implements TransactionalPageOutput
{

    private PageReader pageReader;
    private PluginTask task;

    public KintonePageOutput(PluginTask task, Schema schema)
    {
        this.pageReader = new PageReader(schema);
        this.task = task;
    }

    @Override
    public void add(Page page)
    {
        switch (task.getMode()) {
            case INSERT:
                insertPage(page);
                break;
            case UPDATE:
                updatePage(page);
                break;
            case UPSERT:
                // TODO upsertPage
            default:
                throw new UnsupportedOperationException(
                        "kintone output plugin does not support upsert");
        }
    }

    @Override
    public void finish()
    {
        // noop
    }

    @Override
    public void close()
    {
        // noop
    }

    @Override
    public void abort()
    {
        // noop
    }

    @Override
    public TaskReport commit()
    {
        return Exec.newTaskReport();
    }

    private void execute(Consumer<Connection> operation)
    {
        Connection conn = null;
        try {
            conn = new Connection(task.getDomain(), task.getApiToken());
            operation.accept(conn);
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private void insertPage(Page page)
    {
        execute((conn) -> {
            try {
                List<Record> records = new ArrayList<>();
                pageReader.setPage(page);
                KintoneColumnVisitor visitor = new KintoneColumnVisitor(pageReader,
                        task.getColumnOptions());
                while (pageReader.nextRecord()) {
                    Record record = new Record();
                    visitor.setRecord(record);
                    for (Column column : pageReader.getSchema().getColumns()) {
                        column.visit(visitor);
                    }
                    records.add(record);
                    if (records.size() == 100) {
                        conn.insert(task.getAppId(), records);
                        records.clear();
                    }
                }
                if (records.size() > 0) {
                    conn.insert(task.getAppId(), records);
                }
            }
            catch (DBException e) {
                throw new RuntimeException("kintone throw exception", e);
            }
        });
    }

    private void updatePage(Page page)
    {
        execute((conn) -> {
            try {
                List<Record> records = new ArrayList<>();
                pageReader.setPage(page);
                KintoneColumnVisitor visitor = new KintoneColumnVisitor(pageReader,
                        task.getColumnOptions());
                while (pageReader.nextRecord()) {
                    Record record = new Record();
                    visitor.setRecord(record);
                    for (Column column : pageReader.getSchema().getColumns()) {
                        column.visit(visitor);
                    }
                    records.add(record);
                    if (records.size() == 100) {
                        conn.updateByRecords(task.getAppId(), records);
                        records.clear();
                    }
                }
                if (records.size() > 0) {
                    conn.updateByRecords(task.getAppId(), records);
                }
            }
            catch (DBException e) {
                throw new RuntimeException("kintone throw exception", e);
            }
        });
    }
}
