package com.nextep.pelmel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.nextep.pelmel.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cfondacci on 28/09/15.
 */
public abstract class SectionedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = "SECTIONED_ADAPTER";
    private final List<Section> sections = new ArrayList<Section>();
    private final Map<String,Section> sectionsMap = new HashMap<>();
    protected static int TYPE_SECTION_HEADER = 0;
    private final LayoutInflater layoutInflater;

    class Section {
        String caption;
        Adapter adapter;

        Section(String caption, Adapter adapter) {
            this.caption = caption;
            this.adapter = adapter;
        }
    }
    class TextViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextViewHolder(View view) {
            super(view);
            textView = (TextView)view.findViewById(R.id.sectionTitleLabel);
        }
    }

    public SectionedRecyclerAdapter(Context context) {
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

    public Section getSection(String caption) {
        final Section section = sectionsMap.get(caption);
        if(section != null) {
            return section;
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


    @Override
    public int getItemCount() {
        int total = 0;

        for (Section section : this.sections) {
            total += section.adapter.getCount() + 1; // add one for header
        }

        return (total);
    }

    @Override
    public int getItemViewType(int i) {
        int typeOffset = TYPE_SECTION_HEADER + 2; // start counting from here
        int position = i;
        int viewType = -1;
        for (Section section : this.sections) {
            if (position == 0) {
                viewType = TYPE_SECTION_HEADER;
                break;
            }

            int size = section.adapter.getCount() + 1;

            if (position < size) {
                viewType = (typeOffset + section.adapter
                        .getItemViewType(position - 1));
                break;
            }

            position -= size;
            typeOffset += section.adapter.getViewTypeCount();
        }

        Log.d(LOG_TAG,"ViewType: " + viewType + " for index " + i);
        return viewType;
    }

    public int getViewType(Section section, int i) {
        int typeOffset = TYPE_SECTION_HEADER + 2;
        int viewType = -1;
        for(Section s : sections) {
            if(s == section) {
                viewType = typeOffset + section.adapter.getItemViewType(i);
                break;
            }

            typeOffset += section.adapter.getViewTypeCount();
        }

        return viewType;
    }

    /**
     * Returns the header index of this position, if this position is a header, or -1 if not header
     * @param i the index to chekc
     * @return the header index
     */
    public int getHeaderIndex(int i) {
        int sectionIndex = 0;
        int position = i;
        for (Section section : sections) {
            if (position == 0) {
                return sectionIndex;
            }
            int size = section.adapter.getCount() + 1;

            if (position < size) {
                return -1;
            } else {
                position -= size;
                sectionIndex++;
            }
        }
        return -1;
    }

//    protected RecyclerView.ViewHolder createHeaderView(ViewGroup viewGroup, int sectionIndex) {
//        View view =  layoutInflater.inflate(R.layout.section_title, viewGroup, false);
//        final TextView textView = (TextView)view.findViewById(R.id.sectionTitleLabel);
//        return new TextViewHolder(textView);
//    }
//    protected abstract RecyclerView.ViewHolder createView(ViewGroup viewGroup, Adapter adapter, int indexInAdapter);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_SECTION_HEADER) {
            View view =  layoutInflater.inflate(R.layout.section_title, viewGroup, false);
            return new TextViewHolder(view);
        }
        return null;
    }
//        final int headerIndex = getHeaderIndex(i);
//        if(headerIndex >= 0) {
//            return createHeaderView(viewGroup,headerIndex);
//        } else {
//            int position = i;
//            Section currentSection = null;
//            for (Section section : this.sections) {
//                if (position == 0) {
//                    currentSection = section;
//                    break;
//                }
//
//                int size = section.adapter.getCount() + 1;
//
//                if (position < size) {
//                    // Removing the header
//                    position --;
//                    currentSection = section;
//                    break;
//                }
//
//                position -= size;
//            }
//            return createView(viewGroup, currentSection.adapter, position);
//        }
//    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final int headerIndex = getHeaderIndex(i);
        if(headerIndex >= 0) {
            bindHeaderViewHolder(viewHolder,headerIndex);
        } else {
            int position = i;
            int sectionIndex = 0;
            Section currentSection = this.sections.get(0);
            for (Section section : this.sections) {
                if (position == 0) {
                    currentSection = section;
                    break;
                }

                int size = section.adapter.getCount() + 1;

                if (position < size) {
                    // Removing the header
                    position --;
                    currentSection = section;
                    break;
                }
                sectionIndex++;
                position -= size;
            }
            Log.d(LOG_TAG, "I: " + i + " - sections[" + sections.size() + "] Section: " + sectionIndex + " - Index: " + position + " (" + viewHolder.getClass().toString() + ")");
            bindViewHolder(viewHolder,currentSection.adapter,sectionIndex,position);
        }
    }

    protected abstract void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int sectionIndex);
    protected abstract void bindViewHolder(RecyclerView.ViewHolder viewHolder,Adapter adapter, int sectionIndex, int adapterPosition);

    public List<Section> getSections() {
        return sections;
    }
}
