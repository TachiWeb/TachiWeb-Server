package xyz.nulldev.ts.api.http.task;

import org.json.JSONObject;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.api.task.Task;
import xyz.nulldev.ts.api.task.TaskManager;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.util.LeniantParser;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 27/08/16
 */
public class TaskStatusRoute extends TachiWebRoute {

    public static final String KEY_COMPELTE = "complete";
    public static final String KEY_DETAILS = "details";

    private TaskManager taskManager;

    public TaskStatusRoute(Library library, TaskManager taskManager) {
        super(library);
        this.taskManager = taskManager;
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        //Parse arguments
        Long taskId = LeniantParser.parseLong(request.params(":taskId"));
        if (taskId == null) {
            return error("TaskID must be specified!");
        }
        //Get task
        Task task = taskManager.getTask(taskId);
        if(task == null) {
            return error("No task found with the specified task ID!");
        }
        JSONObject result = success();
        result.put(KEY_COMPELTE, task.isComplete());
        result.put(KEY_DETAILS, task.getTaskStatus());
        return result;
    }
}
