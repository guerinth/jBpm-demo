package compensation;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

public class CompensationTest extends JbpmJUnitBaseTestCase {

	public CompensationTest() {
		// setup data source, enable persistence
		super(true, true);
	}

	@Before
	public void setupKie() {
		// create runtime manager with single process
		Map<String, ResourceType> resources = new HashMap<>();
		resources.put("compensation/Compensation.bpmn2", ResourceType.BPMN2);

		createRuntimeManager(resources);
	}

	@Test
	public void testReservation() {
		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		// Register a test handler for "Email":
		ServiceTaskHandler testHandler = new ServiceTaskHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Service Task", testHandler);

		Map<String, Object> parameters = new HashMap<>();
		BookingRequest booking = new BookingRequest("them@home.org");
		booking.getElements().add("HOTEL");
		parameters.put("bookingRequest", booking);
		parameters.put("paiement", Boolean.TRUE);

		// start process
		ProcessInstance processInstance = ksession.startProcess("fr.voyage.Compensation", parameters);

		// check whether the process instance has completed successfully
		assertProcessInstanceCompleted(processInstance.getId());

		// check what nodes have been triggered
		assertNodeTriggered(processInstance.getId(), "Demande de voyage", "Réserver hôtel", "Voyage organisé");
	}

	@Test
	public void testCompensation() {
		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		// Register a test handler for "Email":
		ServiceTaskHandler testHandler = new ServiceTaskHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Service Task", testHandler);

		Map<String, Object> parameters = new HashMap<>();
		BookingRequest booking = new BookingRequest("them@home.org");
		booking.getElements().add("VOL");
		booking.getElements().add("HOTEL");

		parameters.put("bookingRequest", booking);
		parameters.put("paiement", Boolean.FALSE);

		// start process
		ProcessInstance processInstance = ksession.startProcess("fr.voyage.Compensation", parameters);

		// check whether the process instance has completed successfully
		assertProcessInstanceAborted(processInstance.getId());

		// check what nodes have been triggered
		assertNodeTriggered(processInstance.getId(), "Demande de voyage", "Réserver hôtel", "Réservation en échec", "Annuler hôtel");
	}
}
