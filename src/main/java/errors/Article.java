package errors;

import java.io.Serializable;

public class Article implements Serializable {

	private static final long serialVersionUID = 1L;

	private String eanCode;
	private double quantity;
	private double unitPrice;

	public Article(String eanCode, double quantity, double unitPrice) {
		super();
		this.eanCode = eanCode;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
	}

	public String getEanCode() {
		return eanCode;
	}

	public void setEanCode(String eanCode) {
		this.eanCode = eanCode;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	@Override
	public String toString() {
		return "Article [eanCode=" + eanCode + ", quantity=" + quantity + ", unitPrice=" + unitPrice + "]";
	}

}
