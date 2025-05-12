package utils;
import java.util.Random;
import java.util.UUID;

public class TestData {
    private Random random = new Random();

    public String getRandomFirstName() {
        String[] firstNames = {"Jean", "Marie", "Pierre", "Sophie", "Lucas", "Camille"};
        return firstNames[random.nextInt(firstNames.length)];
    }

    public String getRandomLastName() {
        String[] lastNames = {"Dupont", "Martin", "Bernard", "Petit", "Durand", "Leroy"};
        return lastNames[random.nextInt(lastNames.length)];
    }

    public String getRandomEmail() {
        return "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    public String getRandomInvalidEmail() {
        return "invalid-email-format";
    }

    public String getRandomPhone() {
        return "06" + String.format("%08d", random.nextInt(100000000));
    }

    public String getRandomInvalidPhone() {
        return "123"; // Too short
    }

    public String getRandomAddress() {
        return random.nextInt(100) + " Rue de " + getRandomStreetName();
    }

    private String getRandomStreetName() {
        String[] streets = {"Paris", "Lyon", "Marseille", "Bordeaux", "Lille", "Nantes"};
        return streets[random.nextInt(streets.length)];
    }

    public String getRandomPostalCode() {
        return String.format("%05d", random.nextInt(100000));
    }

    public String getRandomInvalidPostalCode() {
        return "123"; // Too short for French postal code
    }

    public String getRandomCity() {
        String[] cities = {"Paris", "Lyon", "Marseille", "Bordeaux", "Lille", "Nantes"};
        return cities[random.nextInt(cities.length)];
    }
}
