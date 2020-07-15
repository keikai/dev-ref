package io.keikai.devref.usecase.invoice;

import java.util.*;

public class CustomerService {

    static String[][] customers =
    {
        {"Debra", "338-8777",	"debra@yahoo.com", "First Order",	"USA",	"New York",	"Thome Ave." },
        {"Kasha", "335-4667",	"kasha@aol.com", "Empire Co.",	"USA",	"Campbell",	"Vine Street" },
        {"Tameka", "454-4632",	"tameka@gmail.com", "Umbrella Inc.",	"USA",	"Orchard Park",	"Pearl Lane" },
        {"Lyndsey", "381-6000",	"lyndesy@hotmail.com", "BioHazard Inc.",	"New Zealand",	"Buffalo",	"Brown Street" },
        {"Pamelia", "577-3559",	"pamelia@msn.com", "Stark International", 	"Taiwan",	"Tainan",	"Renher Road" }
    };

    static List list = new LinkedList<>();
    static{
        for (String[] c : customers) {
            list.add(c);
        }
    }
    static public List getCustomerList(){
        return list;
    }
}
