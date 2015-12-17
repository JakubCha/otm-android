package org.azavea.otm.fields;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.Button;

import org.azavea.helpers.Logger;
import org.azavea.otm.App;
import org.azavea.otm.R;
import org.azavea.otm.data.Model;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateField extends ButtonField {

    DateField(JSONObject fieldDef) {
        super(fieldDef);
    }

    /**
     * Format the value with any units, if provided in the definition
     */
    @Override
    protected String formatValue(Object value) {
        return formatTimestampForDisplay((String) value);
    }

    @Override
    protected void setupButton(final Button choiceButton, Object value, Model model, Activity activity) {
        if (!JSONObject.NULL.equals(value)) {
            final String timestamp = (String) value;
            final String formattedDate = formatTimestampForDisplay(timestamp);
            choiceButton.setText(formattedDate);
            choiceButton.setTag(R.id.choice_button_value_tag, timestamp);
        } else {
            choiceButton.setText(R.string.unspecified_field_value);
        }
        choiceButton.setOnClickListener(v -> {
            final String setTimestamp = (String) choiceButton.getTag(R.id.choice_button_value_tag);
            final Calendar cal = getCalendarForTimestamp(activity, setTimestamp);
            new DatePickerDialog(activity, (view, year, month, day) -> {
                final String updatedTimestamp = getTimestamp(activity, year, month, day);
                final String displayDate = formatTimestampForDisplay(updatedTimestamp);

                choiceButton.setText(displayDate);
                choiceButton.setTag(R.id.choice_button_value_tag, updatedTimestamp);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    public static Calendar getCalendarForTimestamp(Context context, String setTimestamp) {
        final Calendar cal = new GregorianCalendar();
        final SimpleDateFormat timestampFormatter =
                new SimpleDateFormat(context.getString(R.string.server_date_format));

        if (setTimestamp != null) {

            try {
                cal.setTime(timestampFormatter.parse(setTimestamp));
            } catch (ParseException e) {
                Logger.error("Error parsing date stored on tag.", e);
            }
        }
        return cal;
    }

    public static String getTimestamp(Context context, int year, int month, int day) {
        final SimpleDateFormat timestampFormatter =
                new SimpleDateFormat(context.getString(R.string.server_date_format));
        final Calendar updatedCal = new GregorianCalendar();
        updatedCal.set(Calendar.YEAR, year);
        updatedCal.set(Calendar.MONTH, month);
        updatedCal.set(Calendar.DAY_OF_MONTH, day);

        return timestampFormatter.format(updatedCal.getTime());
    }

    public static String formatTimestampForDisplay(String timestamp) {
        final String displayPattern = App.getCurrentInstance().getShortDateFormat();
        final String serverPattern = App.getAppInstance().getString(R.string.server_date_format);

        final SimpleDateFormat timestampFormatter = new SimpleDateFormat(serverPattern);
        final SimpleDateFormat displayFormatter = new SimpleDateFormat(displayPattern);
        try {
            final Date date = timestampFormatter.parse(timestamp);
            return displayFormatter.format(date);
        } catch (ParseException e) {
            Logger.warning("Problem parsing date", e);
            return App.getAppInstance().getResources().getString(R.string.unspecified_field_value);
        }
    }
}