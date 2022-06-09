package com.joyintech.background.component.common.pack.common.editor;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.util.StringUtils;

/**
 * @author 何阳阳
 * @version 1.0 2018年1月16日
 * @see DateEditor
 * @since 1.0
 */
public class DateEditor extends CustomDateEditor {

    private final DateFormat dateFormat;

    private final boolean allowEmpty;

    private final int exactDateLength;

    private final DateFormat otherDateFormat;

    public DateEditor(DateFormat dateFormat, DateFormat otherDateFormat, boolean allowEmpty) {
        super(dateFormat, allowEmpty);
        this.dateFormat = dateFormat;
        this.allowEmpty = allowEmpty;
        this.otherDateFormat = otherDateFormat;
        this.exactDateLength = -1;
    }

    @Override
    public void setAsText(String text)
        throws IllegalArgumentException {
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            setValue(null);
        }
        else if (text != null && this.exactDateLength >= 0
                 && text.length() != this.exactDateLength) {
            throw new IllegalArgumentException("Could not parse date: it is not exactly"
                                               + this.exactDateLength + "characters long");
        }
        else {
            if(text != null){
                try {
                    if (text.matches("^\\d{13}$")) {
                        setValue(new Date(Long.parseLong(text)));
                    }
                    else {
                        setValue(this.dateFormat.parse(text));
                    }
                }
                catch (ParseException ex) {
                    try {
                        setValue(this.otherDateFormat.parse(text));
                    }
                    catch (ParseException e) {
                        throw new IllegalArgumentException("Could not parse date: " + e.getMessage(),
                                e);
                    }
                }
            }
        }
    }

    /**
     * Format the Date as String, using the specified DateFormat.
     */
    @Override
    public String getAsText() {
        Date value = (Date)getValue();
        return (value != null ? this.dateFormat.format(value) : "");
    }
}