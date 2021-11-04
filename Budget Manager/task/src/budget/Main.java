package budget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

import static budget.Main.ItemType.*;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toList;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private float balance = 0;
    private final Map<ItemType, List<Item>> purchases = new HashMap<>();
    private final String fileName = "purchases.txt";

    public static void main(String[] args) {
        // write your code here

        Main main = new Main();
        main.menu();

        System.out.println("Bye!");
    }

    public Main() {
        clear();
    }

    private void clear() {
        balance = 0;
        purchases.clear();
        purchases.put(Food, new ArrayList<>());
        purchases.put(Clothes, new ArrayList<>());
        purchases.put(Entertainment, new ArrayList<>());
        purchases.put(Other, new ArrayList<>());
    }

    private void menu() {
        while (true) {
            System.out.println("Choose your action:\n" +
                    "1) Add income\n" +
                    "2) Add purchase\n" +
                    "3) Show list of purchases\n" +
                    "4) Balance\n" +
                    "5) Save\n" +
                    "6) Load\n" +
                    "7) Analyze (Sort)\n" +
                    "0) Exit");

            int choice = scanner.nextInt();
            System.out.println();
            switch (choice) {
                case 1:
                    addIncome();
                    break;
                case 2:
                    addPurchase();
                    break;
                case 3:
                    showList();
                    break;
                case 4:
                    showBalance();
                    break;
                case 5:
                    save();
                    break;
                case 6:
                    load();
                    break;
                case 7:
                    analyze();
                    break;
                case 0:
                    return;
            }
        }
    }

    private void analyze() {
        while (true) {
            System.out.println("How do you want to sort?\n" +
                    "1) Sort all purchases\n" +
                    "2) Sort by type\n" +
                    "3) Sort certain type\n" +
                    "4) Back");

            int choice = scanner.nextInt();
            System.out.println();
            switch (choice) {
                case 1:
                    sortAll();
                    break;
                case 2:
                    sortGrouped();
                    break;
                case 3:
                    sortFiltered();
                    break;
                case 4:
                    return;
            }
        }
    }

    private void sortFiltered() {
        ItemType type;
        List<Item> items;
        do {
            System.out.println("Choose the type of purchase\n" +
                    "1) Food\n" +
                    "2) Clothes\n" +
                    "3) Entertainment\n" +
                    "4) Other");
            int choice = scanner.nextInt();
            System.out.println();
            if (choice == 6) {
                return;
            }
            type = ItemType.parseInt(choice);
            items = purchases.get(type);
        } while (type == null);

        if (items.isEmpty()) {
            System.out.println("The purchase list is empty\n");
        } else {
            items.stream()
                    .sorted(comparing(Item::getPrice, reverseOrder()))
                    .forEach(System.out::println);
            double total = items.stream()
                    .mapToDouble(Item::getPrice)
                    .sum();
            System.out.printf("Total sum: $%.2f%n%n", total);
        }
    }

    private void sortGrouped() {
        List<SimpleEntry<ItemType, Double>> types = purchases.entrySet().stream()
                .map(typeItems -> new SimpleEntry<>(typeItems.getKey(), typeItems.getValue().stream()
                        .mapToDouble(Item::getPrice)
                        .sum()))
                .sorted(comparingByValue(reverseOrder()))
                .collect(toList());

        if (types.isEmpty()) {
            System.out.println("The purchase list is empty\n");
        } else {
            System.out.println("Types:");
            types.forEach(type -> System.out.printf("%s - $%.2f%n", type.getKey(), type.getValue()));
            double total = types.stream()
                    .mapToDouble(SimpleEntry::getValue)
                    .sum();
            System.out.printf("Total sum: $%.2f%n%n", total);
        }
    }

    private void sortAll() {
        List<Item> items = purchases.values().stream()
                .flatMap(Collection::stream)
                .sorted(comparing(Item::getPrice, reverseOrder()))
                .collect(toList());

        if (items.isEmpty()) {
            System.out.println("The purchase list is empty\n");
        } else {
            items.forEach(System.out::println);
            double total = items.stream()
                    .mapToDouble(Item::getPrice)
                    .sum();
            System.out.printf("Total sum: $%.2f%n%n", total);
        }
    }

    private void load() {
        File file = new File(fileName);
        if (!file.exists() || !file.isFile()) {
            System.out.println("Invalid path: " + fileName);
            return;
        }
        clear();
        try (Scanner reader = new Scanner(file)) {
            String line = reader.nextLine();
            balance = DecimalFormat.getNumberInstance().parse(line).floatValue();
            while (reader.hasNextLine()) {
                line = reader.nextLine();
                String[] s = line.split(";", 3);
                ItemType itemType = valueOf(s[0]);
                float price = DecimalFormat.getNumberInstance().parse(s[1]).floatValue();
                String name = s[2];
                purchases.get(itemType).add(new Item(name, price));
            }
            System.out.println("Purchases were loaded!\n");
        } catch (FileNotFoundException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append(String.format("%.2f%n", balance));
            purchases.forEach(
                    (itemType, items) -> items.forEach(item -> {
                        try {
                            writer.append(String.format("%s;%.2f;%s%n",
                                    itemType, item.getPrice(), item.getName()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
            );

            System.out.println("Purchases were saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addIncome() {
        System.out.println("Enter income:");
        balance += scanner.nextFloat();
        System.out.println("Income was added!\n");
    }

    private void addPurchase() {
        while (true) {
            ItemType type;
            do {
                System.out.println("Choose the type of purchase\n" +
                        "1) Food\n" +
                        "2) Clothes\n" +
                        "3) Entertainment\n" +
                        "4) Other\n" +
                        "5) Back");
                int choice = scanner.nextInt();
                System.out.println();
                if (choice == 5) {
                    return;
                }
                type = ItemType.parseInt(choice);
            } while (type == null);

            System.out.println("Enter purchase name:");
            String purchaseName;
            do {
                purchaseName = scanner.nextLine();
            } while (purchaseName.isEmpty() && scanner.hasNextLine());
            System.out.println("Enter its price:");
            float price = scanner.nextFloat();
            purchases.get(type)
                    .add(new Item(purchaseName, price));
//        purchases.add(String.format("%s $%.2f", purchaseName, price));
            balance -= price;
            if (balance < 0) {
                balance = 0;
            }
            System.out.println("Purchase was added!\n");
        }
    }

    private void showList() {
        while (true) {
            ItemType type;
            List<Item> items;
            do {
                System.out.println("Choose the type of purchase\n" +
                        "1) Food\n" +
                        "2) Clothes\n" +
                        "3) Entertainment\n" +
                        "4) Other\n" +
                        "5) All\n" +
                        "6) Back");
                int choice = scanner.nextInt();
                System.out.println();
                if (choice == 6) {
                    return;
                }
                if (choice == 5) {
                    items = purchases.values().stream()
                            .flatMap(List::stream)
                            .collect(toList());
                    break;
                }
                type = ItemType.parseInt(choice);
                items = purchases.get(type);
            } while (type == null);

            if (items.isEmpty()) {
                System.out.println("The purchase list is empty\n");
            } else {
                items.forEach(System.out::println);
                double total = items.stream()
                        .mapToDouble(Item::getPrice)
                        .sum();
                System.out.printf("Total sum: $%.2f%n%n", total);
            }
        }
    }

    private void showBalance() {
        System.out.printf("Balance: $%.2f%n%n", balance);
    }

    private static class Item {
        private final String name;
        private final float price;

        public Item(String name, float price) {
            this.price = price;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public float getPrice() {
            return price;
        }

        @Override
        public String toString() {
            return String.format("%s $%.2f", name, price);
        }
    }

    enum ItemType {
        Food,
        Clothes,
        Entertainment,
        Other;

        public static ItemType parseInt(int i) {
            switch (i) {
                case 1:
                    return Food;
                case 2:
                    return Clothes;
                case 3:
                    return Entertainment;
                case 4:
                    return Other;
                default:
                    return null;
            }
        }
    }
}
