package com.survey.mvc.web.model;

import java.util.ArrayList;
import java.util.List;

public class Responses {
	
	private String ClientName;
	private List<ClientResponse> clientResponses = new ArrayList();
	public Responses() {
		super();
	
	}


	public List<ClientResponse> getClientResponses() {
		return clientResponses;
	}

	public void setClientResponses(List<ClientResponse> clientResponses) {
		this.clientResponses = clientResponses;
	}

	public String getClientName() {
		return ClientName;
	}
	public void setClientName(String clientName) {
		ClientName = clientName;
	}
	
}
