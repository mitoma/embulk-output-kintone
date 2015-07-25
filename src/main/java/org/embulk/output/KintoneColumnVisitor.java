package org.embulk.output;

import com.cybozu.kintone.database.Field;
import com.cybozu.kintone.database.FieldType;
import com.cybozu.kintone.database.Record;
import org.embulk.spi.Column;
import org.embulk.spi.ColumnVisitor;
import org.embulk.spi.PageReader;

import java.util.Map;

public class KintoneColumnVisitor implements ColumnVisitor {

  private PageReader                       pageReader;
  private Record                           record;
  private Map<String, KintoneColumnOption> columnOptions;

  public KintoneColumnVisitor(PageReader pageReader,
      Map<String, KintoneColumnOption> columnOptions) {
    this.pageReader = pageReader;
    this.columnOptions = columnOptions;
  }

  public void setRecord(Record record) {
    this.record = record;
  }

  private void setValue(Column column, Object value) {
    if (value == null) {
      return;
    }

    KintoneColumnOption option = columnOptions.get(column.getName());
    if (option == null) {
      return;
    }

    FieldType fieldType = FieldType.valueOf(option.getType());

    Field field = new Field(option.getFieldCode(), fieldType, value);
    record.addField(column.getName(), field);
  }

  @Override
  public void booleanColumn(Column column) {
    setValue(column, pageReader.getBoolean(column));
  }

  @Override
  public void longColumn(Column column) {
    setValue(column, pageReader.getLong(column));
  }

  @Override
  public void doubleColumn(Column column) {
    setValue(column, pageReader.getDouble(column));
  }

  @Override
  public void stringColumn(Column column) {
    setValue(column, pageReader.getString(column));
  }

  @Override
  public void timestampColumn(Column column) {
    setValue(column, pageReader.getTimestamp(column));
  }
}
