package io.keikai.devref.usecase.embed;

import io.keikai.devref.util.BookUtil;

import java.io.FileNotFoundException;
import java.util.*;

public class ProductService {

    static final List<List<String>> rawString = readCsv();
    private List<Product> productList = new LinkedList<>();

    public ProductService() {
        convertRawToProduct();
    }

    private void convertRawToProduct() {
        rawString.stream().forEach(stringList -> {
            Product product = new Product();
            product.setId(stringList.get(0));
            product.setCategory(stringList.get(1));
            product.setName(stringList.get(2));
            product.setVendor(stringList.get(3));
            product.setQuantity(Integer.parseInt(stringList.get(4)));
            product.setPrice(Integer.parseInt(stringList.get(5).trim().replace("$", "")));
            productList.add(product);
        });
    }

    public List<Product> query(ProductFilterComposer.FilterCriteria criteria) {
        List<Product> result = new LinkedList<>();
        for (Product p :productList){
            if (p.getCategory().equals(criteria.getCategory())
                && p.getQuantity() >= criteria.getMin()
                && p.getQuantity() <= criteria.getMax()){
                result.add(p);
            }
        }
        Collections.sort(result, (p1, p2) -> {return p1.getVendor().compareTo(p2.getVendor());});
        return result;
    }

    static private List<List<String>> readCsv() {
        List<List<String>> lines = new LinkedList<>();
        try {
            try (Scanner scanner = new Scanner(BookUtil.getFile("product.csv"))) {
                while (scanner.hasNextLine()) {
                    lines.add(Arrays.asList(scanner.nextLine().split(",")));
                }
            }
            lines.remove(0); //remove the header
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return lines;
    }

}
