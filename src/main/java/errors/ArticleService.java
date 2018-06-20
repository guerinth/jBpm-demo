package errors;

public class ArticleService {

	public boolean verifierDispo(Article art) {
		System.out.println("verifierDispo " + art);
		return true;
	}

	public boolean commanderArticle(Article art) {
		System.out.println("Méthode ArticleService.commanderArticle avec param = " + art);
		throw new IllegalStateException("Oups");
	}

}
