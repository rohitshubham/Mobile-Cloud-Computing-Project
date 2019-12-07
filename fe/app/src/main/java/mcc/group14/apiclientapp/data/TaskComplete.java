package mcc.group14.apiclientapp.data;

public class TaskComplete {
    public String task_id;
    public String project_id;
    public String requester_email;

    public TaskComplete(String task_id, String project_id, String requester_email){
        this.task_id = task_id;
        this.project_id = project_id;
        this.requester_email = requester_email;
    }
}
