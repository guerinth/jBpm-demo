package decorators;

import org.jbpm.bpmn2.handler.LoggingTaskHandlerDecorator;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

public class DecoratorTest extends JbpmJUnitBaseTestCase {

	public DecoratorTest() {
		// setup data source, enable persistence
		super(true, true);
	}

	@Test
	public void testLoggingTaskHandlerDecorator() {
		// create runtime manager with single process
		createRuntimeManager("errors/Decorators.bpmn2");

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		LoggingTaskHandlerDecorator loggingTaskHandlerDecorator = new LoggingTaskHandlerDecorator(ServiceTaskHandler.class);

		ksession.getWorkItemManager().registerWorkItemHandler("Service Task", loggingTaskHandlerDecorator);

		// start process
		ProcessInstance processInstance = ksession.startProcess("errors.decorators");

		// check whether the process instance is active
		assertProcessInstanceActive(processInstance.getId());

		// check what nodes have been triggered
		assertNodeTriggered(processInstance.getId(), "IllegalStateException");
	}

	@Test
	public void testSignallingTaskHandlerDecorator() {
		// create runtime manager with single process
		createRuntimeManager("errors/Decorators.bpmn2");

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		String eventType = "Error_90977";
		SignallingTaskHandlerDecorator signallingTaskWrapper = new SignallingTaskHandlerDecorator(ServiceTaskHandler.class, eventType);
		signallingTaskWrapper.setWorkItemExceptionParameterName("monException");

		ksession.getWorkItemManager().registerWorkItemHandler("Service Task", signallingTaskWrapper);

		ProcessInstance processInstance = ksession.startProcess("errors.decorators");

		// check whether the process instance is aborted
		assertProcessInstanceAborted(processInstance.getId());

		// check what nodes have been triggered
		assertNodeTriggered(processInstance.getId(), "Log");

	}
}
