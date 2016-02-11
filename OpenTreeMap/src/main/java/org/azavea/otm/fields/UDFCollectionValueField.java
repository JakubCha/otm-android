package org.azavea.otm.fields;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;

import org.azavea.otm.App;
import org.azavea.otm.R;
import org.azavea.otm.data.Plot;
import org.azavea.otm.data.UDFCollectionDefinition;
import org.azavea.otm.fields.FieldGroup.DisplayMode;
import org.azavea.otm.ui.TreeEditDisplay;
import org.azavea.otm.ui.UDFCollectionEditActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static org.azavea.helpers.DateButtonListener.formatTimestampForDisplay;

public class UDFCollectionValueField extends Field implements Comparable<UDFCollectionValueField> {
    private static final int DEFAULT_DIGITS = 2;
    private static final AtomicInteger seq = new AtomicInteger(0);

    // Every UDF needs a unique tag, so that edits can be associated with the correct UDF
    // This tag is returned by getTag()
    private final int tag = seq.incrementAndGet();

    private final HashMap<String, JSONObject> subFieldDefinitionsByName;
    private final String sortKey;
    private final JSONObject value;
    private final UDFCollectionDefinition udfDef;

    public UDFCollectionValueField(UDFCollectionDefinition udfDefinition, String sortKey, JSONObject value) {
        super(udfDefinition.getCollectionKey(), udfDefinition.getLabel());
        this.sortKey = sortKey;
        this.value = value;
        this.udfDef = udfDefinition;

        this.subFieldDefinitionsByName = udfDefinition.groupTypesByName();
    }

    @Override
    public View renderForDisplay(LayoutInflater inflater, Plot plot, Activity activity, ViewGroup parent)
            throws JSONException {
        return render(inflater, activity, parent, DisplayMode.VIEW);
    }

    @Override
    public View renderForEdit(LayoutInflater inflater, Plot plot, Activity activity, ViewGroup parent) {
        return render(inflater, activity, parent, DisplayMode.EDIT);
    }

    public View renderForDisplay(LayoutInflater inflater, Activity activity, ViewGroup parent)
            throws JSONException {
        return render(inflater, activity, parent, DisplayMode.VIEW);
    }

    public View renderForEdit(LayoutInflater inflater, Activity activity, ViewGroup parent) {
        return render(inflater, activity, parent, DisplayMode.EDIT);
    }

    private View render(LayoutInflater inflater, Activity activity, ViewGroup parent, DisplayMode mode) {
        View container = inflater.inflate(R.layout.collection_udf_element_row, parent, false);
        TextView labelView = (TextView) container.findViewById(R.id.primary_text);
        TextView secondaryTextView = (TextView) container.findViewById(R.id.secondary_text);
        TextView sortTextView = (TextView) container.findViewById(R.id.sort_key_field);

        labelView.setText(label);

        List<String> secondaryText = new ArrayList<>();
        for (String key : subFieldDefinitionsByName.keySet()) {
            String formattedValue = formatSubValue(key);
            if (sortKey.equals(key)) {
                sortTextView.setText(formattedValue);
            } else {
                secondaryText.add(formattedValue);
            }
        }
        secondaryTextView.setText(Joiner.on('\n').join(secondaryText));

        View chevron = container.findViewById(R.id.chevron);
        if (udfDef.isEditable() && mode == DisplayMode.EDIT) {
            chevron.setVisibility(View.VISIBLE);
            container.setOnClickListener(v -> {
                Intent intent = new Intent(activity, UDFCollectionEditActivity.class);
                // Edit UDF shares code with Create UDF (which handles multiple UDFs), so we pass list of definitions
                intent.putParcelableArrayListExtra(UDFCollectionEditActivity.UDF_DEFINITIONS, newArrayList(udfDef));
                intent.putExtra(UDFCollectionEditActivity.INITIAL_VALUE, value.toString());
                intent.putExtra(UDFCollectionEditActivity.TAG, tag);
                activity.startActivityForResult(intent, TreeEditDisplay.FIELD_ACTIVITY_REQUEST_CODE);
            });
        }

        return container;
    }

    @Override
    protected Object getEditedValue() {
        return value;
    }

    private String formatSubValue(String key) {
        Object subValue = value.opt(key);
        JSONObject typeDef = subFieldDefinitionsByName.get(key);
        String type = typeDef.optString("type");

        if (JSONObject.NULL.equals(subValue)) {
            return App.getAppInstance().getString(R.string.unspecified_field_value);
        } else if ("date".equals(type)) {
            return formatTimestampForDisplay((String) subValue);
        } else if ("float".equals(type)) {
            return TextField.formatWithDigits(subValue, DEFAULT_DIGITS);
        }

        return String.valueOf(value.opt(key));
    }

    @Override
    public int compareTo(UDFCollectionValueField another) {
        String sortKeyType = subFieldDefinitionsByName.get(sortKey).optString("type");

        if ("date".equals(sortKeyType)) {
            // Dates are serialized in ISO format, which allows us to sort them lexicographically
            return nullToEmpty(another.value.optString(sortKey)).compareTo(nullToEmpty(value.optString(sortKey)));
        } else if ("choice".equals(sortKey) || "string".equals(sortKey)) {
            // Natural String sorting
            return ComparisonChain.start()
                    .compare(this, another,
                            Ordering.natural()
                                    .nullsFirst()
                                    .reverse()
                                    .onResultOf(v -> v.value.optString(sortKey))
                    )
                    .result();
        } else {
            return Doubles.compare(another.value.optDouble(sortKey, Double.MIN_VALUE), value.optDouble(sortKey, Double.MIN_VALUE));
        }
    }

    public int getTag() {
        return tag;
    }
}
