package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class SectionedAdapter extends BaseAdapter {
    abstract protected View getHeaderView(String caption, int index,
                                          View convertView, ViewGroup parent);

    private final List<Section> sections = new ArrayList<Section>();
    private final Map<String,Section> sectionsMap = new HashMap<>();
    private static int TYPE_SECTION_HEADER = 0;
    private final LayoutInflater layoutInflater;

    public SectionedAdapter(Context context) {
        super();
        layoutInflater = LayoutInflater.from(context);
    }

    public void addSection(String caption, Adapter adapter) {
        insertSection(sections.size(), caption, adapter);
        notifyDataSetChanged();
    }
    public void insertSection(int index, String caption, Adapter adapter) {
        final Section section = new Section(caption, adapter);
        sections.add(index,section);
        sectionsMap.put(caption, section);
        notifyDataSetChanged();
    }

    public Adapter getSection(String caption) {
        final Section section = sectionsMap.get(caption);
        if(section != null) {
            return section.adapter;
        }
        return null;
    }
    public void removeSection(String caption) {
        Section section = sectionsMap.get(caption);
        if(section != null) {
            sections.remove(section);
            sectionsMap.remove(caption);
            notifyDataSetChanged();
        }
    }

    public void replaceSection(String caption, Adapter newAdapter) {

        // Retrieving section
        final Section section = sectionsMap.get(caption);
        if(section != null) {

            // Getting position and removing current section
            final int index = sections.indexOf(section);
            sections.remove(index);

            // Adding new section
            insertSection(index, caption, newAdapter);
            notifyDataSetChanged();
        }
    }
    public Object getItem(int position) {
        for (Section section : this.sections) {
            if (position == 0) {
                return (section);
            }

            int size = section.adapter.getCount() + 1;

            if (position < size) {
                return (section.adapter.getItem(position - 1));
            }

            position -= size;
        }

        return (null);
    }

    public int getCount() {
        int total = 0;

        for (Section section : this.sections) {
            total += section.adapter.getCount() + 1; // add one for header
        }

        return (total);
    }

    @Override
    public int getViewTypeCount() {
        int total = 1; // one for the header, plus those from sections

        for (Section section : this.sections) {
            total += section.adapter.getViewTypeCount();
        }

        return (total);
    }
    protected View getSectionTitleConvertView(View convertView, ViewGroup parent, int titleResource) {
        if (convertView == null || convertView.getTag() == null) {
            convertView = layoutInflater.inflate(R.layout.section_title, parent, false);
            convertView.setTag(convertView.findViewById(R.id.sectionTitleLabel));
        }
        final TextView textView = (TextView) convertView.getTag();
        textView.setText(Strings.getText(titleResource));
        return convertView;
    }
    @Override
    public int getItemViewType(int position) {
        int typeOffset = TYPE_SECTION_HEADER + 1; // start counting from here

        for (Section section : this.sections) {
            if (position == 0) {
                return (TYPE_SECTION_HEADER);
            }

            int size = section.adapter.getCount() + 1;

            if (position < size) {
                return (typeOffset + section.adapter
                        .getItemViewType(position - 1));
            }

            position -= size;
            typeOffset += section.adapter.getViewTypeCount();
        }

        return (-1);
    }

    public boolean areAllItemsSelectable() {
        return (false);
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionIndex = 0;

        for (Section section : this.sections) {
            if (position == 0) {
                return (getHeaderView(section.caption, sectionIndex,
                        convertView, parent));
            }

            int size = section.adapter.getCount() + 1;

            if (position < size) {
                return (section.adapter.getView(position - 1, convertView,
                        parent));
            }

            position -= size;
            sectionIndex++;
        }

        return (null);
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    class Section {
        String caption;
        Adapter adapter;

        Section(String caption, Adapter adapter) {
            this.caption = caption;
            this.adapter = adapter;
        }
    }

    public List<Section> getSections() {
        return sections;
    }
}