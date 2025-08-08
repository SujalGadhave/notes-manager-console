package notes_manager;

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotesManager {
	private static final String NOTES_FILE = "notes.txt";
	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		System.out.println("=== Welcome to Notes Manager ===");
		System.out.println("A simple file-based notes application");
		System.out.println();

		while (true) {
			displayMenu();
			int choice = getChoice();

			switch (choice) {
			case 1:
				addNote();
				break;
			case 2:
				viewAllNotes();
				break;
			case 3:
				searchNotes();
				break;
			case 4:
				clearAllNotes();
				break;
			case 5:
				System.out.println("Thank you for using Notes Manager!");
				return;
			default:
				System.out.println("Invalid choice. Please try again.");
			}

			System.out.println("\nPress Enter to continue...");
			scanner.nextLine();
		}
	}

	private static void displayMenu() {
		System.out.println("\n=== NOTES MANAGER ===");
		System.out.println("1. Add New Note");
		System.out.println("2. View All Notes");
		System.out.println("3. Search Notes");
		System.out.println("4. Clear All Notes");
		System.out.println("5. Exit");
		System.out.print("Enter your choice (1-5): ");
	}

	private static int getChoice() {
		try {
			int choice = Integer.parseInt(scanner.nextLine());
			return choice;
		} catch (NumberFormatException e) {
			return -1; // Invalid choice
		}
	}

	private static void addNote() {
		System.out.println("\n=== Add New Note ===");
		System.out.print("Enter your note: ");
		String noteContent = scanner.nextLine();

		if (noteContent.trim().isEmpty()) {
			System.out.println("Note cannot be empty!");
			return;
		}

		try (FileWriter writer = new FileWriter(NOTES_FILE, true)) {
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			String formattedNote = String.format("[%s] %s%n", timestamp, noteContent);

			writer.write(formattedNote);
			writer.flush();

			System.out.println("✓ Note added successfully!");

		} catch (IOException e) {
			System.err.println("❌ Error writing to file: " + e.getMessage());
			logException(e);
		}
	}

	private static void viewAllNotes() {
		System.out.println("\n=== All Notes ===");

		File file = new File(NOTES_FILE);
		if (!file.exists()) {
			System.out.println("No notes found. Create your first note!");
			return;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(NOTES_FILE))) {
			String line;
			int noteCount = 0;

			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				noteCount++;
			}

			if (noteCount == 0) {
				System.out.println("No notes found in the file.");
			} else {
				System.out.println("\n📝 Total notes: " + noteCount);
			}

		} catch (IOException e) {
			System.err.println("❌ Error reading file: " + e.getMessage());
			logException(e);
		}
	}

	private static void searchNotes() {
		System.out.println("\n=== Search Notes ===");
		System.out.print("Enter search keyword: ");
		String keyword = scanner.nextLine().toLowerCase();

		if (keyword.trim().isEmpty()) {
			System.out.println("Search keyword cannot be empty!");
			return;
		}

		File file = new File(NOTES_FILE);
		if (!file.exists()) {
			System.out.println("No notes file found.");
			return;
		}

		List<String> matchingNotes = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(NOTES_FILE))) {
			String line;

			while ((line = reader.readLine()) != null) {
				if (line.toLowerCase().contains(keyword)) {
					matchingNotes.add(line);
				}
			}

			if (matchingNotes.isEmpty()) {
				System.out.println("No notes found containing: " + keyword);
			} else {
				System.out.println("🔍 Found " + matchingNotes.size() + " matching note(s):");
				System.out.println("─".repeat(50));
				for (String note : matchingNotes) {
					System.out.println(note);
				}
			}

		} catch (IOException e) {
			System.err.println("❌ Error searching notes: " + e.getMessage());
			logException(e);
		}
	}

	private static void clearAllNotes() {
		System.out.println("\n=== Clear All Notes ===");
		System.out.print("Are you sure you want to delete all notes? (y/N): ");
		String confirmation = scanner.nextLine().toLowerCase();

		if (!confirmation.equals("y") && !confirmation.equals("yes")) {
			System.out.println("Operation cancelled.");
			return;
		}

		try (FileWriter writer = new FileWriter(NOTES_FILE, false)) {

			writer.write("");
			System.out.println("✓ All notes cleared successfully!");

		} catch (IOException e) {
			System.err.println("❌ Error clearing notes: " + e.getMessage());
			logException(e);
		}
	}

	private static void logException(Exception e) {
		String logFile = "error_log.txt";
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		try (FileWriter logWriter = new FileWriter(logFile, true)) {
			logWriter.write(String.format("[%s] Exception: %s%n", timestamp, e.getMessage()));
			logWriter.write("Stack trace:%n");

			// Write stack trace
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logWriter.write(sw.toString());
			logWriter.write("─".repeat(50) + "%n");

		} catch (IOException logException) {
			System.err.println("Failed to write to error log: " + logException.getMessage());
		}
	}
}