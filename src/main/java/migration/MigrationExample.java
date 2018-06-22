package migration;

import java.util.List;

import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.admin.MigrationReportInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.kie.server.client.admin.ProcessAdminServicesClient;

public class MigrationExample {

	public static final String USERNAME = "bpmsAdmin";
	public static final String PASSWORD = "changeMe";
	public static final String SERVER_URL = "http://localhost:8280/kie-server/services/rest/server";
	public static final String SOURCE_CONTAINER = "FILL_CONTAINER1_ID";
	public static final String TARGET_CONTAINER = "FILL_CONTAINER2_ID";
	public static final String PROCESS_ID = "FILL_PROCESS_ID"; // In this example, process ID is same in both, source and target container

	// Container migration1 includes following process Start-> Human Task (assigned to bpmsAdmin)-> Script Task -> End
	// container migration2 includes the process with same id, same structure, but the Script Task has different code
	// After performing the migration and completing the Human Task you should see *different* code from Script Task being executed

	public static void main(String args[]) {

		// configure client

		KieServicesClient client = configure(SERVER_URL, USERNAME, PASSWORD);

		ProcessServicesClient pClient = client.getServicesClient(ProcessServicesClient.class);
		UserTaskServicesClient tClient = client.getServicesClient(UserTaskServicesClient.class);
		QueryServicesClient qClient = client.getServicesClient(QueryServicesClient.class);
		ProcessAdminServicesClient paClient = client.getServicesClient(ProcessAdminServicesClient.class);

		// start the process instance
		Long pid = pClient.startProcess(SOURCE_CONTAINER, PROCESS_ID);

		MigrationReportInstance report = paClient.migrateProcessInstance(SOURCE_CONTAINER, pid, TARGET_CONTAINER, PROCESS_ID);

		if (report.isSuccessful()) {

			System.out.println("Migration was successful");
			for (String log : report.getLogs()) {

				System.out.println(log);
			}

			List<TaskSummary> tasks = tClient.findTasksByStatusByProcessInstanceId(pid, null, 0, 5);
			tClient.completeAutoProgress(TARGET_CONTAINER, tasks.get(0).getId(), USERNAME, null);

			// now check the server log and verify whether you are seeing new output
		}

		pClient.abortProcessInstance(SOURCE_CONTAINER, pid);

	}

	public static KieServicesClient configure(String url, String username, String password) {

		// default marshalling format is JAXB
		KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, username, password);

		/**
		 * JSON XSTREAM JAXB
		 */
		config.setMarshallingFormat(MarshallingFormat.XSTREAM);

		return KieServicesFactory.newKieServicesClient(config);
	}
}
