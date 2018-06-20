package jndi;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

public class JndiTest extends JbpmJUnitBaseTestCase {

	public JndiTest() {
		// don't setup data source and enable persistence
		super(false, false);
	}

	@Test
	public void testGetUrl() {
		// create runtime manager with single process
		createRuntimeManager("jndi/getUrlsFromJndi.bpmn2");

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		// start process
		ProcessInstance processInstance = ksession.startProcess("jndi.getUrlsFromJndi");

		// check what nodes have been triggered
		assertNodeTriggered(processInstance.getId(), "Valeur JNDI lue");
	}
}
