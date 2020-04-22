package io.keikai.devref.usecase.vendor.persistence;

import java.util.Map;

public class VendorMap {

	private String internalName;
	
	private Map<String, Object> vendorData;

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	public Map<String, Object> getVendorData() {
		return vendorData;
	}

	public void setVendorData(Map<String, Object> vendorData) {
		this.vendorData = vendorData;
	}

	public VendorMap(String internalName, Map<String, Object> vendorData) {
		super();
		this.internalName = internalName;
		this.vendorData = vendorData;
	}
	
}