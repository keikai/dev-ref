package io.keikai.devref.advanced.db;

/**
 * A trade record
 * @author hawk
 *
 */
public class Trade {

	private Integer id;
	private String type;
	private int sale;
	private String salesPerson;
	
	public Trade(Integer id) {
		this.id = id;
	}
	
	public Trade(String type, String salesPerson, int sale){
		this.type = type;
		this.salesPerson = salesPerson;
		this.sale = sale;
	}
	
	/**
	 * copy constructor
	 * @param trade
	 */
	public Trade(Trade trade){
		this.id = trade.getId();
		this.type = trade.getType();
		this.sale = trade.getSale();
		this.salesPerson = trade.getSalesPerson();
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getSale() {
		return sale;
	}
	public void setSale(int sale) {
		this.sale = sale;
	}
	public String getSalesPerson() {
		return salesPerson;
	}
	public void setSalesPerson(String salesPerson) {
		this.salesPerson = salesPerson;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
}
