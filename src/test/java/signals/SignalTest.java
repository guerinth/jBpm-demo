package signals;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

public class SignalTest extends JbpmJUnitBaseTestCase {

	public SignalTest() {
		// setup data source, enable persistence
		super(false, false);
	}

	/**
	 * The 2 process instances of fooProcess must catch the single signal sent by MainSignallingProcess.
	 */
	@Test
	public void testBroadcastSignal() {
		// create runtime manager
		Map<String, ResourceType> resources = new HashMap<>();
		resources.put("signals/FooProcess.bpmn2", ResourceType.BPMN2);
		resources.put("signals/MainSignallingProcess.bpmn2", ResourceType.BPMN2);

		createRuntimeManager(resources);

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		// start process
		ProcessInstance fooProcess1 = ksession.startProcess("signals.FooProcess");
		ProcessInstance fooProcess2 = ksession.startProcess("signals.FooProcess");

		// start process
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("itemType", "foo");

		ProcessInstance mainProcess = ksession.startProcess("signals.MainSignallingProcess", parameters);

		try {
			Thread.sleep(5000L); // time for mainProcess to send the signal
		} catch (InterruptedException ignore) {
			// ignore
		}

		// check what nodes have been triggered
		assertNodeTriggered(mainProcess.getId(), "Script Task 2", "Script Task 3", "foo");
		assertNodeTriggered(fooProcess1.getId(), "StartProcess", "Log", "Event Based Gateway 1", "wait for foo", "End Event 1");
		assertNodeTriggered(fooProcess2.getId(), "StartProcess", "Log", "Event Based Gateway 1", "wait for foo", "End Event 1");
	}

	/**
	 * Only one of the 2 process instances of fooProcess must catch the single signal sent by the api.
	 */
	@Test
	public void testFooProcessWithUnicastSignal() {
		// create runtime manager
		Map<String, ResourceType> resources = new HashMap<>();
		resources.put("signals/FooProcess.bpmn2", ResourceType.BPMN2);

		createRuntimeManager(resources);

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		// start process
		ProcessInstance fooProcess1 = ksession.startProcess("signals.FooProcess");
		ProcessInstance fooProcess2 = ksession.startProcess("signals.FooProcess");

		ksession.signalEvent("fooSignal", null, fooProcess1.getId());

		// check what nodes have been triggered
		assertNodeTriggered(fooProcess1.getId(), "End Event 1");
		// check what node is still active
		assertNodeActive(fooProcess2.getId(), ksession, "Timeout 3s");
	}

	@Test
	public void testTimeout() {
		// create runtime manager
		Map<String, ResourceType> resources = new HashMap<>();
		resources.put("signals/FooProcess.bpmn2", ResourceType.BPMN2);

		createRuntimeManager(resources, "pseudo");

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		// start process
		ProcessInstance fooProcess = ksession.startProcess("signals.FooProcess");

		try {
			Thread.sleep(4000L); // time for mainProcess to send the signal
		} catch (InterruptedException ignore) {
			// ignore
		}

		// check what nodes have been triggered
		assertNodeTriggered(fooProcess.getId(), "StartProcess", "Log", "Event Based Gateway 1", "Timeout 3s", "log", "End Event 2");
	}

	@Test
	public void testMainProcessWithoutFooProcess() {
		// create runtime manager
		Map<String, ResourceType> resources = new HashMap<>();
		resources.put("signals/MainSignallingProcess.bpmn2", ResourceType.BPMN2);

		createRuntimeManager(resources);

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("itemType", "bar");

		// start process
		ProcessInstance mainProcess = ksession.startProcess("signals.MainSignallingProcess", parameters);

		// check what nodes have been triggered
		assertNodeTriggered(mainProcess.getId(), "Send other event");
	}

	@Test
	public void testFooProcessWithoutMainProcess() {
		// create runtime manager
		Map<String, ResourceType> resources = new HashMap<>();
		resources.put("signals/FooProcess.bpmn2", ResourceType.BPMN2);

		createRuntimeManager(resources);

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		// start process
		ProcessInstance fooProcess = ksession.startProcess("signals.FooProcess");

		try {
			Thread.sleep(4000L); // time for mainProcess to send the signal
		} catch (InterruptedException ignore) {
			// ignore
		}

		// check what nodes have been triggered
		assertNodeTriggered(fooProcess.getId(), "End Event 2");
	}
}
