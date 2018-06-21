package escalade;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

import errors.Article;

public class CommandeTest extends JbpmJUnitBaseTestCase {

	public CommandeTest() {
		// setup data source, enable persistence
		super(true, true);
	}

	@Before
	public void setupKie() {
		// create runtime manager with single process
		createRuntimeManager("escalade/commande.bpmn2");
	}

	@Test
	public void testProcessSansAppro() {

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		Map<String, Object> parameters = new HashMap<>();
		Article art = new Article("31212", 1.0, 3.0);
		parameters.put("article", art);

		// start process
		ProcessInstance processInstance = ksession.startProcess("escalade.commande", parameters);

		// check whether the process instance has completed successfully
		assertProcessInstanceCompleted(processInstance.getId());

		// check what nodes have been triggered
		assertNodeTriggered(processInstance.getId(), "Commande reçue", "Vérifier disponibilité dans le stock", "Réaliser le règlement", "Envoyer l'article",
				"Commande traitée");
	}

	@Test
	public void testProcessAvecApproEtDelai() {

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		Map<String, Object> parameters = new HashMap<>();
		Article art = new Article("31212", 100.0, 3.0);
		parameters.put("article", art);

		// start process
		ProcessInstance processInstance = ksession.startProcess("escalade.commande", parameters);

		// process is waiting for the signal "Signal_delivery"
		assertProcessInstanceActive(processInstance.getId());

		// Inform the process : the item is received
		ksession.signalEvent("Signal_delivery", art, processInstance.getId());

		// check whether the process instance has completed successfully
		assertProcessInstanceCompleted(processInstance.getId());

		// check what nodes have been triggered
		assertNodeTriggered(processInstance.getId(), "Commande reçue", "Vérifier disponibilité dans le stock", "Vérifier disponibilité chez le fournissseur",
				"Retard prévu", "Commander auprès du fournisseur", "Article reçu", "Article en stock", "Retard signalé", "Prévenir le client du retard",
				"Réaliser le règlement", "Envoyer l'article", "Commande traitée");
	}

	@Test
	public void testProcessAvecApproSansDelai() {

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		Map<String, Object> parameters = new HashMap<>();
		Article art = new Article("31212", 5.0, 3.0);
		parameters.put("article", art);

		// start process
		ProcessInstance processInstance = ksession.startProcess("escalade.commande", parameters);

		// process is waiting for the signal "Signal_delivery"
		assertProcessInstanceActive(processInstance.getId());

		// Inform the process : the item is received
		ksession.signalEvent("Signal_delivery", art, processInstance.getId());

		// check whether the process instance has completed successfully
		assertProcessInstanceCompleted(processInstance.getId());

		// check what nodes have been triggered
		assertNodeTriggered(processInstance.getId(), "Commande reçue", "Vérifier disponibilité dans le stock", "Vérifier disponibilité chez le fournissseur",
				"Commander auprès du fournisseur", "Article reçu", "Article en stock", "Réaliser le règlement", "Envoyer l'article", "Commande traitée");
	}

	@Test
	public void testProcessAvecArticleIndisponible() {

		// take RuntimeManager to work with process engine
		RuntimeEngine runtimeEngine = getRuntimeEngine();

		// get access to KieSession instance
		KieSession ksession = runtimeEngine.getKieSession();

		Map<String, Object> parameters = new HashMap<>();
		Article art = new Article("31212", -1.0, 3.0);
		parameters.put("article", art);

		// start process
		ProcessInstance processInstance = ksession.startProcess("escalade.commande", parameters);

		// check whether the process instance has completed successfully
		assertProcessInstanceCompleted(processInstance.getId());

		// check what nodes have been triggered
		assertNodeTriggered(processInstance.getId(), "Commande reçue", "Vérifier disponibilité dans le stock", "Vérifier disponibilité chez le fournissseur",
				"Article non livrable", "Informer le client du problème", "Enlever l'article du catalogue", "Commande annulée");
	}
}
