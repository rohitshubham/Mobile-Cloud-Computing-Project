package mcc.group14.apiclientapp.data;

public class TaskDetails {

    String name;
    String id;
    boolean isSelected;
    String status;

    public TaskDetails (String name, String price, boolean isSelected, String status) {
        this.name = name;
        this.id = price;
        this.isSelected = isSelected;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}