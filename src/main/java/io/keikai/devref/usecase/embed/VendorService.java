package io.keikai.devref.usecase.embed;

import java.util.*;

public class VendorService {

    static private List<Vendor> vendorList = new LinkedList<>();

    static {
        vendorList.add(new Vendor("Best Farm", "12345678", "service@bestfarm.com.nz"));
        vendorList.add(new Vendor("Cooca", "33445548", "order@cooca.com"));
        vendorList.add(new Vendor("Fresh Fresh", "87654321", "service@fresh.com.tw"));
        vendorList.add(new Vendor("G&P", "56784677", "buy@gp.com.de"));
        vendorList.add(new Vendor("Natural", "78421561", "contact@natural.com.jp"));
        vendorList.add(new Vendor("Sfway", "55558877", "buy@sfway.com"));
        vendorList.add(new Vendor("WholeF", "55558877", "purchase@wholef.com"));
    }

    static public Vendor query(String name) {
        Vendor vendor = null;
        for (Vendor v : vendorList) {
            if (v.name.equals(name)) {
                vendor = v;
                break;
            }
        }
        return vendor;
    }

    static class Vendor {
        private String name;
        private String tel;
        private String email;

        public Vendor(String name, String tel, String email) {
            this.name = name;
            this.tel = tel;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
