package com.vagm.vagmdroid.dto;

import android.content.Context;

import com.vagm.vagmdroid.R;

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
    private Group group1;
    private Group group2;
    private Group group3;
    private Group group4;

    /**
     * constructor.
     * @param context
     */
    public LabelDTO(Context context) {
        title = context.getString(R.string.group1);
        Group[] groups = getDefaultGroups(context);
        group1 = groups[0];
        group2 = groups[1];
        group3 = groups[2];
        group4 = groups[3];
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
        return new Group[] { group1, group2, group3, group4 };
    }
    
    /**
     * setGroup.
     * @param groupNumber
     * @param group
     */
    public void setGroup(int groupNumber, Group group) {
        switch (groupNumber) {
            case 1:
                group1 = group;
                break;
    
            case 2:
                group2 = group;
                break;
    
            case 3:
                group3 = group;
                break;
    
            case 4:
                group4 = group;
                break;
    
            default:
                throw new IllegalArgumentException("Group number " + groupNumber + " must be <= 4");
        }
    }

    private Group[] getDefaultGroups(Context context) {
        group1 = new Group(context.getString(R.string.block1), "", "");
        group2 = new Group(context.getString(R.string.block2), "", "");
        group3 = new Group(context.getString(R.string.block3), "", "");
        group4 = new Group(context.getString(R.string.block4), "", "");
        return new Group[] { group1, group2, group3, group4 };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "LabelDTO [title=" + title + ", group1=" + group1 + ", group2=" + group2 + ", group3=" + group3 + ", group4=" + group4 + "]";
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
