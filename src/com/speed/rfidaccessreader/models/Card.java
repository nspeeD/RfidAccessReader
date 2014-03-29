package com.speed.rfidaccessreader.models;

public class Card {
	
	private int id;
	private String cardid;
	private String userid;
	
	public Card(int i, String cardid, String userid){
		this.id=i;
		this.cardid=cardid;
		this.userid=userid;
	}
	public Card(){
		
	}
	protected int getId() {
		return id;
	}
	protected void setId(int id) {
		this.id = id;
	}
	protected String getCardid() {
		return cardid;
	}
	protected void setCardid(String cardid) {
		this.cardid = cardid;
	}
	protected String getUserid() {
		return userid;
	}
	protected void setUserid(String userid) {
		this.userid = userid;
	}
}
