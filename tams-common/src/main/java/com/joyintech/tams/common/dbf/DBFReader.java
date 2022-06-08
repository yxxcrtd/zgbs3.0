package com.joyintech.tams.common.dbf;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 何阳阳
 * DBF文件读取
 */
public final class DBFReader {

    private static Logger logger = LoggerFactory.getLogger(DBFReader.class);

    private Converter converter;

    private Filter filter;

    private Charset charset;

    private File file;

    private DBFReader(String file) {
        this(new File(file));
    }

    private DBFReader(File file) {
        if(null == file || !file.exists()) {
            //throw new RuntimeException(String.format("文件%s不存在", file.getPath()));
            throw new RuntimeException("文件不存在");
        }
        this.file = file;
    }

    public static DBFReader build(String file) {
        return new DBFReader(file);
    }

    public static DBFReader build(File file) {
        return new DBFReader(file);
    }

    public DBFReader convert(Converter converter) {
        this.converter = converter;
        return this;
    }

    public DBFReader filter(Filter filter) {
        this.filter = filter;
        return this;
    }

    public DBFReader charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public List<DBFRow> read() {
        long start = System.currentTimeMillis();

        List<DBFRow> list = null;

        com.linuxense.javadbf.DBFReader reader = null;

        try {
            FileInputStream inputStream = new FileInputStream(file);
            reader = new com.linuxense.javadbf.DBFReader(inputStream, charset, false);

            int numberOfFields = reader.getFieldCount();

            list = new ArrayList<>(reader.getRecordCount());

            Object[] row;

            while ((row = reader.nextRecord()) != null) {

                DBFRow dbfRow = new DBFRow(row.length);

                for (int i = 0; i < numberOfFields && i < row.length; i++) {

                    DBFField field = reader.getField(i);

                    Object value = row[i];

                    if(value instanceof String) {
                        value = ((String) value).trim();
                    }

                    if(null == converter) {
                        dbfRow.put(field.getName(), new Field(field.getName(), field.getLength(), value));
                    }
                    else {
                        dbfRow.put(field.getName(), new Field(field.getName(), field.getLength(), converter.converter(field.getName(), value)));
                    }

                }

                if(null == filter || filter.accept(dbfRow)) {
                    list.add(dbfRow);
                }
            }

        } catch (DBFException | FileNotFoundException e) {
            logger.error("DBF文件" + file.getName() + "文件解析异常", e);
            throw new RuntimeException(e);
        }
        finally {
            DBFUtils.close(reader);
        }
        if(logger.isDebugEnabled()) {
            logger.debug("DBF文件{}解析时间：{}毫秒", file.getName(), System.currentTimeMillis() - start);
        }
        return list;
    }
}
