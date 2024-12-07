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

import java.util.List;

public class Application {
    // reusable session factory
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String [] args) {
      //fetchAllCountries().forEach(System.out::println);
        displayCountries();
    }

    private static void displayCountries() {
        List<Country> countries= fetchAllCountries();

        System.out.printf("----------------------------------------------------------------%n");
        System.out.printf("                            Country Data                        %n");
        System.out.printf("----------------------------------------------------------------%n");
        System.out.printf("%-8s %-25s %-20s %-5s %n", "Code", "Country", "Internet Users", "Literacy");
        System.out.printf("----------------------------------------------------------------%n");
        for (Country country : countries) {
            if(country.getInternetUsers() != null && country.getAdultLiteracyRate() != null) {
                System.out.printf(" %-8s %-25s %.4g %.4g %n", country.getCode(), country.getName(), country.getInternetUsers(), country.getAdultLiteracyRate());
            } else if (country.getInternetUsers() != null && country.getAdultLiteracyRate() == null) {
                System.out.printf(" %-8s %-25s %.4g -- %n", country.getCode(), country.getName(), country.getInternetUsers());
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
}
