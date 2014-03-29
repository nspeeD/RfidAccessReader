package com.speed.rfidaccessreader.models;

import com.google.gson.annotations.SerializedName;

public class User {
	
	@SerializedName("codigo")
	private String codigo;
	@SerializedName("nombre")
	private String nombre;
	@SerializedName("apellidos")
	private String apellidos;
	@SerializedName("foto")
	private String foto;
	@SerializedName("nick")
	private String nick;
	
	public User () {}
	
	public User (String id, String name, String surname, String photo, String nick) {
		this.codigo = id;
		this.nombre = name;
		this.nick = nick;
		this.apellidos = surname;
		this.foto = photo;
	}

	public String getId() {
		return codigo;
	}

	public void setId(String id) {
		this.codigo = id;
	}

	public String getName() {
		return nombre;
	}

	public void setName(String name) {
		this.nombre = name;
	}

	public String getSurname() {
		return apellidos;
	}

	public void setSurname(String surname) {
		this.apellidos = surname;
	}

	public String getPhoto() {
		return foto;
	}

	public void setPhoto(String photo) {
		this.foto = photo;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
}