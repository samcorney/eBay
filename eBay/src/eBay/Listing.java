package eBay;

import java.util.Date;

public class Listing {
	
	private String link;
	private String title;
	private String subtitle;
	private double price;
	private Format format;
	private int bids;
	private double postageCost;
	private Date time;
	
	public Listing(String link, String title, String subtitle, double price, Format format, double postageCost, int bids){
		this.link = link;
		this.title = title;
		this.subtitle = subtitle;
		this.price = price;
		this.format = format;
		this.bids = bids;
		this.postageCost = postageCost;
		this.time = new Date();
	}
	
	public String getTitle() {
		return title;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public double getPrice() {
		return price;
	}
	public Format getFormat() {
		return format;
	}
	public double getPostageCost() {
		return postageCost;
	}
	public double getTotalPriceIncludingShipping(){
		return price + postageCost;
	}

}