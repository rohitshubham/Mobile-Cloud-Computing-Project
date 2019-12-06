package mcc.group14.apiclientapp.views.projects.dashboard;

public class DataModel {
    String name;
    String version;
    int id_;


    public DataModel(String name, String version, int id_) {
        this.name = name;
        this.version = version;
        this.id_ = id_;

    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }



    public int getId() {
        return id_;
    }
}
