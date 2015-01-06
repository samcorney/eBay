package eBay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Scraper {
	
	
	
	static WebDriver driver = new FirefoxDriver();
	
	/*
						THINGS YOU WANT TO CHANGE..... WHEN TO EMAIL AND WHO TO!
	*/
	static final int maxLeftInMillisecondsToEmail = 1200000;
	static final int maxPriceToEmail = 490;
	static final String emailTo = "s.corney89@gmail.com";
	
	//driver.get("http://www.ebay.co.uk/");
	//WebElement searchBox = driver.findElement(By.id("gh-ac"));
	//searchBox.sendKeys(searchTerm);
	//WebElement search = driver.findElement(By.id("gh-btn"));
	//search.click();
	
	
	
	public synchronized static void scrapeListingsPage(String[] listings){
		for(int listingNumber = 1; listingNumber < listings.length; listingNumber++){

			System.out.println("\n\t\t\t" + listingNumber);
			//System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");
			//System.out.println(listings[listingNumber]);
			
			//Link
			String link = listings[listingNumber].split("href=\"")[1].split("\"")[0];
			
			//Title
			String titleSplit = listings[listingNumber].split("class=\"lvtitle\">")[1];
			String title = "";			
			if(titleSplit.contains("<span class=\"newly\">New listing</span>"))
				title = titleSplit.split("</span>")[1].split("</a")[0].trim();
			else
				title = titleSplit.split("\">")[1].split("</a")[0].trim();
			
			//Subtitle
			String subTitle = "";
			if(listings[listingNumber].contains("<div class=\"lvsubtitle\">"))
				subTitle = listings[listingNumber].split("<div class=\"lvsubtitle\">")[1].split("</div>")[0].trim();
			
			//Price
			double price = 0;
			String priceString = listings[listingNumber].split("<span class=\"g-b\">")[1].split("</span>")[0].trim();
			Pattern patternDouble = Pattern.compile("\\d+\\.?\\d*");
			Matcher matcherPrice = patternDouble.matcher(priceString);
			if(matcherPrice.find())
				price = Double.parseDouble(matcherPrice.group(0));
			
			//Format
			Format format = null;
			int bids = 0;
			if(listings[listingNumber].contains("<span title=\"Buy it now\" class=\"logoBin\"></span>"))
				format = Format.BUY_IT_NOW;
			else if(listings[listingNumber].contains("<li class=\"lvformat bids\">")){
				format = Format.AUCTION;
				String bidsString = listings[listingNumber].split("<li class=\"lvformat bids\">")[1].split("</span>")[0].split(">")[1];
				Pattern patternInteger = Pattern.compile("\\d+");
				Matcher matcherBids = patternInteger.matcher(bidsString);
				if(matcherBids.find())
					bids = Integer.parseInt(matcherBids.group(0));
			}
			else if(listings[listingNumber].contains("<span title=\"Buy it now or Best Offer\" class=\"logoBinBo\"></span>"))
				format = Format.BUY_IT_NOW_OR_BEST_OFFER;
			
			//Postage
			PostageFormat postageFormat = null;
			double postage = 0;
			if(listings[listingNumber].contains("<span class=\"grfsp gvfree\">Free Postage</span></span>")){
				postageFormat = PostageFormat.FREE;
				postage = 0;
			}
			else if(listings[listingNumber].contains("Collection only: Free</span></li>")){
				postageFormat = PostageFormat.COLLECTION_ONLY;
				postage = 0;
			}
			else if(listings[listingNumber].contains("<span class=\"fee\">")){
				postageFormat = PostageFormat.FEE;
				String postageSplit = listings[listingNumber].split("<span class=\"ship\">")[1];
				String postageString =  postageSplit.split("\">")[1].split("</span>")[0].trim();
				Matcher matcherPostage = patternDouble.matcher(postageString);
				if(matcherPostage.find())
					postage = Double.parseDouble(matcherPostage.group(0));
			}
			
			driver.get(link);
			
			String listing = driver.getPageSource();
			
			driver.navigate().back();
			
			Date finishDate = new Date();
			
			if(listing.contains("<span class=\"vi-tm-left\">")){
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyykk:mm:ss zzz");
				String finishDateString = listing.split("<span>\\(")[1].split("</span>")[0];
				String finishTimeString = listing.split("<span class=\"endedDate\">")[1].split("\\)</span>")[0];
				try {
					finishDate = simpleDateFormat.parse(finishDateString + finishTimeString);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			System.out.println("\nLink: " + link);
			System.out.println("Title: " + title);
			System.out.println("Subtitle: " + subTitle);
			System.out.println("Price: " + price);
			System.out.println("Format: " + format);
			System.out.println("Bids: " + bids);
			System.out.println("Postage Format: " + postageFormat);
			System.out.println("Postage: " + postage);	
			System.out.println("Finish Date: " + finishDate);
			System.out.println("Time Left: " + (finishDate.getTime() - new Date().getTime()));
			System.out.println("Max Time Left To Email: " + maxLeftInMillisecondsToEmail);
			
			if(price + postage <= maxPriceToEmail && finishDate.getTime() - new Date().getTime() <= maxLeftInMillisecondsToEmail){
				SendEmail.sendEmail(link, emailTo);
			}
		}
	}
	
	
	public synchronized static void scrapeeBay(String searchPage){
		
		driver.get(searchPage);
		String[] listings = null;
		
		while(true){
			
			listings = driver.getPageSource().split("listingid=\"");
			scrapeListingsPage(listings);
			if(listings[listings.length - 1].contains("title=\"Next page of results\"")){
				if(listings[listings.length - 1].split("class=\"pagn-next\">")[1].split("title=\"Next page of results\"")[0].contains("aria-disabled=\"true\""))
					break;
				else{
					WebElement nextPage = driver.findElement(By.xpath("//*[@id=\"Pagination\"]/tbody/tr/td[3]/a"));
					nextPage.click();
				}
			}
			else{
				driver.quit();
				break;
			}
		}
	}
}
