package io.keikai.devref.usecase.vendor.persistence;

import org.apache.commons.lang3.ArrayUtils;

public class PersistenceUtil {

	private static VendorMap[] allVendors;
	static {
		allVendors = new VendorMap[] {};
	}

	public static void addVendor(VendorMap newvendor) {
		for (VendorMap vendor : allVendors) {
			if (vendor.getInternalName().equals(newvendor.getInternalName()))
				deleteVendor(vendor.getInternalName());
		}
		allVendors = ArrayUtils.add(allVendors, newvendor);
	}

	public static VendorMap[] getAllVendors() {
		return allVendors;
	}

	public static boolean vendorNameExists(String internalName) {
		for (VendorMap vendor : allVendors) {
			if (vendor.getInternalName().equals(internalName))
				return true;
		}
		return false;
	}

	public static boolean checkVendor(String vendorName, String vendorPassword) {
		for (VendorMap vendor : allVendors) {
			if (vendor.getInternalName().equals(vendorName))
				return true;
		}
		return false;
	}

	public static void deleteVendor(String vendorName) {
		VendorMap[] updatedArray = allVendors;
		for (VendorMap vendor : allVendors) {
			if (vendor.getInternalName().equals(vendorName)) {
				updatedArray = ArrayUtils.removeElement(allVendors, vendor);
			}
		}
		allVendors = updatedArray;
	}

	public static VendorMap getVendorByName(String name) {
		for (VendorMap vendor : allVendors) {
			if (vendor.getInternalName().equals(name)) {
				return vendor;
			}
		}
		return null;
	}
}
