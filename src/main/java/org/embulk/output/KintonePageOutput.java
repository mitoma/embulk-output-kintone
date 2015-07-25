package org.embulk.output;

import com.cybozu.kintone.database.Connection;
import com.cybozu.kintone.database.Record;
import com.cybozu.kintone.database.exception.DBException;
import org.embulk.config.CommitReport;
import org.embulk.output.KintoneOutputPlugin.PluginTask;
import org.embulk.spi.Column;
import org.embulk.spi.Exec;
import org.embulk.spi.Page;
import org.embulk.spi.PageReader;
import org.embulk.spi.Schema;
import org.embulk.spi.TransactionalPageOutput;

import java.util.ArrayList;
import java.util.List;

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
                // TODO updatePage, upsertPage
            case UPSERT:
                // TODO updatePage, upsertPage
            default:
                throw new UnsupportedOperationException(
                        "kintone output plugin does not support update, upsert");
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
    public CommitReport commit()
    {
        return Exec.newCommitReport();
    }

    private void insertPage(Page page)
    {
        Connection conn = null;
        try {
            conn = getConnection(task);

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
        finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private Connection getConnection(PluginTask task)
    {
        return new Connection(task.getDomain(), task.getApiToken());
    }
}
