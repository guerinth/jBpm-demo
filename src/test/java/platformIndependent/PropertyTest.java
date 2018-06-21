package platformIndependent;

import javax.naming.NamingException;

import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;

public class PropertyTest extends JbpmJUnitBaseTestCase {

	public PropertyTest() {
		// don't setup data source and enable persistence
		super(false, false);
	}

	@Test
	// @Ignore
	public void testGetUrl() throws NamingException {

		// create runtime manager with single process
		createRuntimeManager("platformIndependent/getUrls.bpmn2");

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		// Register a test handler for "Rest"
		WorkItemHandler testHandler = new SendTaskHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Rest", testHandler);

		// start process
		ProcessInstance processInstance = ksession.startProcess("platformIndependent.getUrls");

		// check what nodes have been triggered
		assertNodeTriggered(processInstance.getId(), "Demande d'appel", "Appel REST", "Appel REST réalisé");
	}
}
