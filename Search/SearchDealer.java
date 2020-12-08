package search;

import dao.Dealer;
import dto.DataPersistence;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.*;

public class SearchDealer {
    public static final List<Dealer> ALL_DEALERS = new DataPersistence().getAllDealers();

    /**
     *
     * @param zipcode
     * @return List of dealers sorted by distance to the input zipcode
     * @throws MalformedURLException
     */
    public static List<Dealer> searchByZipCode(String zipcode) {
        //List<Dealer> dealerList = new ArrayList<>();
        String zipcodes = ALL_DEALERS.stream()
                .map(dealer -> dealer.getDealerAddress().getZipCode())
                .collect(Collectors.joining(","));

        Map<String, Double> zipcodeToDistance = queryDistances(zipcode, zipcodes);

        //set distances for all dealers
        for(Dealer dealer: ALL_DEALERS){
            dealer.setDistanceInMiles(zipcodeToDistance.get(dealer.getDealerAddress().getZipCode()));
        }
        ALL_DEALERS.sort(new Comparator<Dealer>() {
            @Override
            public int compare(Dealer o1, Dealer o2) {
                if(o1.getDistanceInMiles() > o2.getDistanceInMiles()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        return ALL_DEALERS;
    }

    /**
     *
     * @param zipcode
     * @param dealerZipcodes
     * @return Map from dealer zipcode to Distance to zipcode
     * @throws MalformedURLException
     */
    private static Map<String, Double> queryDistances(String zipcode, String dealerZipcodes) {
        String url = String.format("https://www.zipcodeapi.com/rest/f22LF85IQxGZqCQZKNxtuM9Qw0KH488I8qY5fgdo9liaTg9GeWydRuBKVeRh1uxy/multi-distance.csv/%s/%s/miles",zipcode,dealerZipcodes);

        Map<String, Double> map = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), "UTF-8"))) {
            reader.readLine();
            for (String line; (line = reader.readLine()) != null;) {
                String[] zipcodeInfo = line.split(",");
                String zip = zipcodeInfo[0];
                String dis = zipcodeInfo[1];
                double distance = Double.parseDouble(dis);
                map.put(zip,distance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     *
     * @param dealerName
     */
    public static List<Dealer> searchByName(String dealerName) {
        List<Dealer> dealerList = new ArrayList<>();

//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
//            String input = reader.readLine();
            for(Dealer dealers: ALL_DEALERS){
                if(dealers.getDealerName().contains(dealerName)){
                    dealerList.add(dealers);
                }
             }

//        }catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return dealerList;

    }

    public static void main(String[] args) {
        //SearchDealer.searchByZipCode("98107");

        System.out.println(SearchDealer.searchByName("aj"));
        //SearchDealer.ALL_DEALERS.forEach(out::println);
    }
}

