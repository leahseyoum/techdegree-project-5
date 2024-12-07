package org.example.countrymgr;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.countrymgr.model.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Application {
    // reusable session factory
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String [] args) {
        run();
    }

    private static Country createCountry() throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.printf("%nWhat is the country name: ");
        String name = reader.readLine().trim();

        System.out.printf("%nWhat is the country code (three characters): ");
        String code = reader.readLine().trim();

        System.out.printf("%nWhat is the rate of Internet users: ");
        String internetUsers = reader.readLine().trim();

        System.out.printf("%nWhat is the rate of Adult Literacy: ");
        String adultLiteracy = reader.readLine().trim();

        return new Country.CountryBuilder(name, code)
                .withInternetUsers(Double.parseDouble(internetUsers))
                .withAdultLiteracyRate(Double.parseDouble(adultLiteracy))
                .build();
    }

    private static String add() throws IOException{
        Country country = createCountry();
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        String code = (String) session.save(country);

        session.getTransaction().commit();

        session.close();

        return code;
    }

    private static String promptForCode() throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Select a country code: ");
        return reader.readLine().trim();
    }

    private static Country fetchCountryByCode() throws IOException{
        String code = promptForCode();
        Session session = sessionFactory.openSession();
        Country country = session.get(Country.class, code);
        session.close();
        return country;
    }

    private static Country editCountry() throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Country country = fetchCountryByCode();

        System.out.println("New name: ");
        String newName = reader.readLine().trim();
        country.setName(newName);

        System.out.println("New Internet User rate: ");
        double newRate = Double.parseDouble(reader.readLine().trim());
        country.setInternetUsers(newRate);

        System.out.println("New Adult Literacy rate: ");
        double newLiteracyRate = Double.parseDouble(reader.readLine().trim());
        country.setAdultLiteracyRate(newLiteracyRate);

        return country;
    }

    private static void update() throws IOException{
       Country country = editCountry();
        System.out.println(country);
       Session session = sessionFactory.openSession();
       session.beginTransaction();
       session.update(country);
       session.getTransaction().commit();
       session.close();
    }

    private static void delete() throws IOException{
        Country country = fetchCountryByCode();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(country);
        session.getTransaction().commit();
        session.close();
    }

    private static void displayCountries() {
        List<Country> countries= fetchAllCountries();

        System.out.printf("-------------------------------------------------------------------%n");
        System.out.printf("                            Country Data                        %n");
        System.out.printf("-------------------------------------------------------------------%n");
        System.out.printf("%-8s %-30s %-20s %-5s %n", "Code", "Country", "Internet Users", "Literacy");
        System.out.printf("-------------------------------------------------------------------%n");
        for (Country country : countries) {
            if(country.getInternetUsers() != null && country.getAdultLiteracyRate() != null) {
                System.out.printf(" %-8s %-35s %10.2f %10.2f %n", country.getCode(), country.getName(), country.getInternetUsers(), country.getAdultLiteracyRate());
            } else if (country.getInternetUsers() != null && country.getAdultLiteracyRate() == null) {
                System.out.printf(" %-8s %-35s %10.2f       -- %n", country.getCode(), country.getName(), country.getInternetUsers());
           } else if (country.getInternetUsers() == null && country.getAdultLiteracyRate() == null) {
                System.out.printf(" %-8s %-35s        --        -- %n", country.getCode(), country.getName());
            }
        }
    }


    private static List<Country> fetchAllCountries() {
        Session session = sessionFactory.openSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();

        CriteriaQuery<Country> query = cb.createQuery(Country.class);

        Root<Country> root = query.from(Country.class);

        query.select(root);

        return session.createQuery(query).getResultList();
    }

    private static void statistics () {
        List<Country> countries = fetchAllCountries();

        // find max value for internet users
        Country countryWithMaxInternetUsers = countries.stream()
                .filter(country -> country.getInternetUsers() != null)
                .max(Comparator.comparing(Country::getInternetUsers))
                .orElse(null);

                System.out.printf("%nCountry with the highest percentage of internet users: %s with %.2f %n", countryWithMaxInternetUsers.getName(), countryWithMaxInternetUsers.getInternetUsers());

                //min value for internet users
        Country countryWithMinInternetUsers = countries.stream()
                .filter(country -> country.getInternetUsers() != null)
                .min(Comparator.comparing(Country::getInternetUsers))
                .orElse(null);

        System.out.printf("Country with the lowest percentage of internet users: %s with %.2f %n", countryWithMinInternetUsers.getName(), countryWithMinInternetUsers.getInternetUsers());

        // mean value of internet users
        double meanInternetUsers = countries.stream()
                .filter(country -> country.getInternetUsers() != null)
                .mapToDouble(Country::getInternetUsers)
                .average()
                .orElse(0.0);
        System.out.printf("The average rate of internet usage is %.2f percent %n", meanInternetUsers);

        // max value for literacy
        Country countryWithMaxLiteracy = countries.stream()
                .filter(country -> country.getAdultLiteracyRate() != null)
                .max(Comparator.comparing(Country::getAdultLiteracyRate))
                .orElse(null);

        System.out.printf("Country with the highest percentage of Adult Literacy: %s with %.2f %n", countryWithMaxLiteracy.getName(), countryWithMaxLiteracy.getAdultLiteracyRate());

        // min value for literacy
        Country countryWithMinLiteracy = countries.stream()
                .filter(country -> country.getAdultLiteracyRate() != null)
                .min(Comparator.comparing(Country::getAdultLiteracyRate))
                .orElse(null);

        System.out.printf("Country with the lowest percentage of Adult Literacy: %s with %.2f %n", countryWithMinLiteracy.getName(), countryWithMinLiteracy.getInternetUsers());


        //mean value for literacy
        double meanLiteracy = countries.stream()
                .filter(country -> country.getAdultLiteracyRate() != null)
                .mapToDouble(Country::getAdultLiteracyRate)
                .average()
                .orElse(0.0);
        System.out.printf("The average Adult Literacy rate is %.2f percent %n", meanLiteracy);


    }

    private static String promptAction() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
         Map<String, String> menu = new TreeMap<>();
         menu.put("View", "View all countries data");
         menu.put("Statistics", "View maximum, minimum and mean values for each indicator");
         menu.put("Add", "Add country data");
         menu.put("Edit", "Edit Country data");
         menu.put("Delete", "Delete country data");
         menu.put("Exit", "Exit application");

        System.out.printf("%nMenu%n");
         for (Map.Entry<String, String> entry : menu.entrySet()) {
             System.out.println(entry.getKey() + ": " + entry.getValue());
         }
        System.out.printf("%nSelect an option: ");
        String choice = reader.readLine();
        return choice.trim().toLowerCase();
    }

    private static void run() {
        String choice = "";

        do{
            try {
                choice = promptAction();
                switch (choice) {
                    case "view":
                        displayCountries();
                        break;
                    case "statistics":
                        statistics();
                        break;
                    case "add":
                        add();
                        break;
                    case "edit":
                        displayCountries();
                        update();
                        break;
                    case "delete":
                        displayCountries();
                        delete();
                        break;
                    case "exit":
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.printf("Unknown choice:  '%s'. Try again.  %n%n%n",
                                choice);
                }
            } catch (IOException ioe) {
                System.out.println("Problem with input");
                ioe.printStackTrace();
            }
        } while(!choice.equals("exit"));
    }
}
