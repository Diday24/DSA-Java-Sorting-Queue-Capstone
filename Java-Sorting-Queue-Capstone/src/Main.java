import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Main {
    // Parallel ArrayLists: the same index in every list represents one participant.
    static ArrayList<String> names = new ArrayList<>();
    static ArrayList<String> studentIds = new ArrayList<>();
    static ArrayList<String> emails = new ArrayList<>();
    static ArrayList<String> contacts = new ArrayList<>();
    static ArrayList<String> courses = new ArrayList<>();
    static ArrayList<String> eventTypes = new ArrayList<>();
    static ArrayList<String> registrationDates = new ArrayList<>();
    static ArrayList<String> statuses = new ArrayList<>();

    // The queue stores participant IDs, so sorting the records will not change FIFO order.
    static Queue<String> checkInQueue = new LinkedList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            displayMenu();
            int choice = readInt("Enter your choice: ", 1, 10);

            switch (choice) {
                case 1:
                    registerParticipant();
                    break;
                case 2:
                    addExistingParticipantToQueue();
                    break;
                case 3:
                    processNextParticipant();
                    break;
                case 4:
                    peekNextParticipant();
                    break;
                case 5:
                    displayQueue();
                    break;
                case 6:
                    bubbleSortByName();
                    break;
                case 7:
                    selectionSortByRegistrationDate();
                    break;
                case 8:
                    searchParticipant();
                    break;
                case 9:
                    displayRecords();
                    break;
                case 10:
                    running = false;
                    System.out.println("Thank you for using the Event Registration and Check-In System!");
                    break;
                default:
                    // readInt prevents invalid values from reaching this point.
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    static void displayMenu() {
        System.out.println("\n==============================================");
        System.out.println(" EVENT REGISTRATION AND CHECK-IN SYSTEM");
        System.out.println("==============================================");
        System.out.println("1. Register Participant (also enqueue)");
        System.out.println("2. Add Existing Participant to Check-In Queue");
        System.out.println("3. Process Next Participant (Dequeue)");
        System.out.println("4. Peek at Next Participant");
        System.out.println("5. Display Check-In Queue");
        System.out.println("6. Sort Records by Name (Bubble Sort)");
        System.out.println("7. Sort Records by Registration Date (Selection Sort)");
        System.out.println("8. Search Participant");
        System.out.println("9. Display All Participant Records");
        System.out.println("10. Exit");
        System.out.println("==============================================");
    }

    static void registerParticipant() {
        System.out.println("\n--- Register Participant ---");
        String name = readRequiredText("Full name: ");
        String id;

        while (true) {
            id = readRequiredText("Student/participant ID: ");
            if (findParticipantIndex(id) != -1) {
                System.out.println("That ID is already registered. Enter a unique ID.");
            } else {
                break;
            }
        }

        String email = readRequiredText("Email address: ");
        String contact = readRequiredText("Contact number: ");
        String course = readRequiredText("Course/department: ");
        String event = readRequiredText("Event name/type: ");
        String date = readDate("Registration date (YYYY-MM-DD): ");

        names.add(name);
        studentIds.add(id);
        emails.add(email);
        contacts.add(contact);
        courses.add(course);
        eventTypes.add(event);
        registrationDates.add(date);
        statuses.add("Waiting");

        checkInQueue.offer(id);
        System.out.println("Participant registered successfully.");
        System.out.println("Participant was added to the end of the check-in queue.");
    }

    static void addExistingParticipantToQueue() {
        if (studentIds.isEmpty()) {
            System.out.println("No participant records are available. Register a participant first.");
            return;
        }

        String id = readRequiredText("Enter participant ID to enqueue: ");
        int index = findParticipantIndex(id);

        if (index == -1) {
            System.out.println("Participant not found. No one was added to the queue.");
            return;
        }
        if ("Checked In".equals(statuses.get(index))) {
            System.out.println("This participant has already checked in and cannot be queued again.");
            return;
        }
        if (checkInQueue.contains(id)) {
            System.out.println("This participant is already in the queue.");
            return;
        }

        checkInQueue.offer(id);
        statuses.set(index, "Waiting");
        System.out.println(names.get(index) + " was added to the end of the queue.");
    }

    static void processNextParticipant() {
        if (checkInQueue.isEmpty()) {
            System.out.println("The check-in queue is empty. There is no participant to process.");
            return;
        }

        String id = checkInQueue.poll();
        int index = findParticipantIndex(id);

        if (index == -1) {
            System.out.println("Queue entry has no matching participant record.");
            return;
        }

        statuses.set(index, "Checked In");
        System.out.println("Now checking in: " + names.get(index) + " (ID: " + id + ")");
        System.out.println("FIFO confirmed: the participant at the front of the queue was processed first.");
    }

    static void peekNextParticipant() {
        String id = checkInQueue.peek();

        if (id == null) {
            System.out.println("The check-in queue is empty. There is no next participant.");
            return;
        }

        int index = findParticipantIndex(id);
        if (index == -1) {
            System.out.println("The next queue entry has no matching participant record.");
        } else {
            System.out.println("Next participant: " + names.get(index) + " (ID: " + id + ")");
            System.out.println("Peek does not remove the participant from the queue.");
        }
    }

    static void displayQueue() {
        if (checkInQueue.isEmpty()) {
            System.out.println("The check-in queue is empty.");
            return;
        }

        System.out.println("\n--- Check-In Queue (Front to Rear) ---");
        int position = 1;
        for (String id : checkInQueue) {
            int index = findParticipantIndex(id);
            if (index != -1) {
                System.out.println(position + ". " + names.get(index) + " | ID: " + id
                        + " | Status: " + statuses.get(index));
            } else {
                System.out.println(position + ". ID: " + id + " | Record not found");
            }
            position++;
        }
    }

    // Bubble Sort: compares neighboring names and swaps complete records.
    static void bubbleSortByName() {
        if (names.isEmpty()) {
            System.out.println("There are no records to sort.");
            return;
        }

        System.out.println("\n--- Records Before Bubble Sort (Name A-Z) ---");
        displayRecords();

        int n = names.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (names.get(j).compareToIgnoreCase(names.get(j + 1)) > 0) {
                    swapRecords(j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }

        System.out.println("\n--- Records After Bubble Sort (Name A-Z) ---");
        displayRecords();
    }

    // Selection Sort: selects the earliest ISO-format date for each position.
    static void selectionSortByRegistrationDate() {
        if (names.isEmpty()) {
            System.out.println("There are no records to sort.");
            return;
        }

        System.out.println("\n--- Records Before Selection Sort (Date Earliest-Latest) ---");
        displayRecords();

        int n = names.size();
        for (int i = 0; i < n - 1; i++) {
            int earliestIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (registrationDates.get(j).compareTo(registrationDates.get(earliestIndex)) < 0) {
                    earliestIndex = j;
                }
            }
            if (earliestIndex != i) {
                swapRecords(i, earliestIndex);
            }
        }

        System.out.println("\n--- Records After Selection Sort (Date Earliest-Latest) ---");
        displayRecords();
    }

    // Swaps every parallel list at the same indexes to keep each participant's data together.
    static void swapRecords(int first, int second) {
        swapStringList(names, first, second);
        swapStringList(studentIds, first, second);
        swapStringList(emails, first, second);
        swapStringList(contacts, first, second);
        swapStringList(courses, first, second);
        swapStringList(eventTypes, first, second);
        swapStringList(registrationDates, first, second);
        swapStringList(statuses, first, second);
    }

    static void swapStringList(ArrayList<String> list, int first, int second) {
        String temp = list.get(first);
        list.set(first, list.get(second));
        list.set(second, temp);
    }

    static void searchParticipant() {
        if (studentIds.isEmpty()) {
            System.out.println("No participant records are available to search.");
            return;
        }

        String query = readRequiredText("Enter full/partial name or participant ID: ").toLowerCase();
        boolean found = false;

        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).toLowerCase().contains(query)
                    || studentIds.get(i).toLowerCase().contains(query)) {
                if (!found) {
                    System.out.println("\n--- Search Results ---");
                }
                printRecord(i);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No participant matched your search.");
        }
    }

    static void displayRecords() {
        if (names.isEmpty()) {
            System.out.println("No participant records have been registered yet.");
            return;
        }

        System.out.println("\n--- Participant Records ---");
        System.out.printf("%-4s %-22s %-14s %-22s %-16s %-12s %-12s%n",
                "#", "Name", "Participant ID", "Course/Department", "Event", "Reg. Date", "Status");
        System.out.println("---------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < names.size(); i++) {
            System.out.printf("%-4d %-22s %-14s %-22s %-16s %-12s %-12s%n",
                    i + 1,
                    shorten(names.get(i), 21),
                    shorten(studentIds.get(i), 13),
                    shorten(courses.get(i), 21),
                    shorten(eventTypes.get(i), 15),
                    registrationDates.get(i),
                    statuses.get(i));
        }
        System.out.println("Total participants: " + names.size());
    }

    static void printRecord(int i) {
        System.out.println("Name: " + names.get(i));
        System.out.println("Participant ID: " + studentIds.get(i));
        System.out.println("Email: " + emails.get(i));
        System.out.println("Contact: " + contacts.get(i));
        System.out.println("Course/Department: " + courses.get(i));
        System.out.println("Event: " + eventTypes.get(i));
        System.out.println("Registration date: " + registrationDates.get(i));
        System.out.println("Status: " + statuses.get(i));
        System.out.println("----------------------------------------");
    }

    static int findParticipantIndex(String id) {
        for (int i = 0; i < studentIds.size(); i++) {
            if (studentIds.get(i).equalsIgnoreCase(id)) {
                return i;
            }
        }
        return -1;
    }

    static String readRequiredText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    static int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= minimum && value <= maximum) {
                    return value;
                }
                System.out.println("Please enter a number from " + minimum + " to " + maximum + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    // Validates both the YYYY-MM-DD format and whether the date exists.
    static String readDate(String prompt) {
        while (true) {
            String date = readRequiredText(prompt);
            if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                try {
                    java.time.LocalDate.parse(date);
                    return date;
                } catch (java.time.DateTimeException e) {
                    System.out.println("Invalid calendar date. Use YYYY-MM-DD (example: 2026-10-27).");
                }
            } else {
                System.out.println("Invalid date format. Use YYYY-MM-DD (example: 2026-10-27).");
            }
        }
    }

    static String shorten(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}
