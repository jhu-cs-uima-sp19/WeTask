import com.example.wetask.TaskItem;

import java.util.Comparator;
import java.util.Date;

public class TaskComparator implements Comparator<TaskItem> {
    @Override
    public int compare(TaskItem task_1, TaskItem task_2){
        Date date1 = task_1.getDate();
        Date date2 = task_2.getDate();
        return date1.compareTo(date2);
    }
}
