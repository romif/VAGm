package com.vagm.vagmdroid.dto;

import java.util.Arrays;

import com.vagm.vagmdroid.R;

import android.content.Context;

/**
 * The Class LabelDTO.
 * 
 * @author Roman_Konovalov
 */
public class LabelDTO {

    /**
     * title.
     */
    private String title;

    /**
     * group.
     */
    private Group[] group;

    /**
     * constructor.
     * @param context
     */
    public LabelDTO(Context context) {
        title = context.getString(R.string.group1);
        group = getDefaultGroups(context);
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title != null ? title : "";
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @return the group
     */
    public Group[] getGroup() {
        return group;
    }

    /**
     * @param group
     *            the group to set
     */
    public void setGroup(final Group[] group) {
        this.group = group;
    }

    private static Group[] getDefaultGroups(Context context) {
        Group group1 = new Group(context.getString(R.string.block1), "", "");
        Group group2 = new Group(context.getString(R.string.block2), "", "");
        Group group3 = new Group(context.getString(R.string.block3), "", "");
        Group group4 = new Group(context.getString(R.string.block4), "", "");
        return new Group[] { group1, group2, group3, group4 };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "LabelDTO [title=" + title + ", group=" + Arrays.toString(group) + "]";
    }

    /**
     * The Class Group.
     * 
     * @author Roman_Konovalov
     */
    public static class Group {

        /**
         * title.
         */
        private String title;

        /**
         * description.
         */
        private String description;

        /**
         * specification.
         */
        private String specification;

        /**
         * constructor.
         * 
         * @param title
         *            title
         * @param description
         *            description
         * @param specification
         *            specification
         */
        public Group(final String title, final String description, final String specification) {
            super();
            this.title = title;
            this.description = description;
            this.specification = specification;
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title != null ? title : "";
        }

        /**
         * @param title
         *            the title to set
         */
        public void setTitle(final String title) {
            this.title = title;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description != null ? description : "";
        }

        /**
         * @param description
         *            the description to set
         */
        public void setDescription(final String description) {
            this.description = description;
        }

        /**
         * @return the specification
         */
        public String getSpecification() {
            return specification != null ? specification : "";
        }

        /**
         * @param specification
         *            the specification to set
         */
        public void setSpecification(final String specification) {
            this.specification = specification;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Group [title=" + title + ", description=" + description + ", specification=" + specification + "]";
        }

    }

}
