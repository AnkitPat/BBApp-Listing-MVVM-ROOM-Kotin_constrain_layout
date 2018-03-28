package com.example.bbim1041.bbstore.model.data;

import com.intrusoft.sectionedrecyclerview.Section;

import java.util.List;

/**
 * Created by BBIM1041 on 20/03/18.
 */
public class SectionHeader implements Section<App> {

    List<App> childList;
    public String sectionText;

    public SectionHeader(List<App> childList, String sectionText) {
        this.childList = childList;
        this.sectionText = sectionText;
    }

    @Override
    public List<App> getChildItems() {
        return childList;
    }

    public String getSectionText() {
        return sectionText;
    }
}
