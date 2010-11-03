package de.steadycrypt.v2.bob;

import java.util.ArrayList;
import java.util.List;

import de.steadycrypt.v2.views.model.IDroppedElementVisitor;

public class EncryptedFile extends DroppedElement {
	protected static List<EncryptedFile> newFiles = buildBookList();
	protected static int cursor = 0;
	
	public EncryptedFile(String title, String authorGivenName, String authorSirName) {
		super(title, authorGivenName, authorSirName);
	}
	
	
	
	
	public static EncryptedFile newFile() {
		EncryptedFile newFile = (EncryptedFile)newFiles.get(cursor);
		cursor = ((cursor + 1) % newFiles.size());
		return newFile;
	}
	
	
	protected static List<EncryptedFile> buildBookList() {
		newFiles = new ArrayList<EncryptedFile>();
		EncryptedFile[] files = new EncryptedFile[] {
			new EncryptedFile("Advanced Java: Idioms, Pitfalls, Styles and Programming Tips", "Chris", "Laffra"),
			new EncryptedFile("Programming Ruby: A Pragmatic Programmer's Guide", "David", "Thomas"),
			new EncryptedFile("The Pragmatic Programmer", "Andrew", "Hunt"),
			new EncryptedFile("Java Virtual Machine", "Jon", "Meyer"),
			new EncryptedFile("Using Netscape IFC", "Arun", "Rao"),
			new EncryptedFile("Smalltalk-80", "Adele", "Goldberg"),
			new EncryptedFile("Cold Mountain", "Charles", "Frazier"),
			new EncryptedFile("Software Development Using Eiffel", "Richard", "Wiener"),
			new EncryptedFile("Winter's Heart", "Robert", "Jordan"),
			new EncryptedFile("Ender's Game", "Orson Scott", "Card"),
			new EncryptedFile("Castle", "David", "Macaulay"),
			new EncryptedFile("Cranberry Thanksgiving", "Wende", "Devlin"),
			new EncryptedFile("The Biggest Bear", "Lynd", "Ward"),
			new EncryptedFile("The Boxcar Children", "Gertrude Chandler", "Warner"),
			new EncryptedFile("BASIC Fun with Adventure Games", "Susan Drake", "Lipscomb"),
			new EncryptedFile("Bridge to Terabithia", "Katherine", "Paterson"),
			new EncryptedFile("One Renegade Cell", "Robert A.", "Weinberg"),
			new EncryptedFile("Programming Internet Mail", "David", "Wood"),
			new EncryptedFile("Refactoring", "Martin", "Fowler"),
			new EncryptedFile("Effective Java", "Joshua", "Bloch"),
			new EncryptedFile("Cutting-Edge Java Game Programming", "Neil", "Bartlett"),
			new EncryptedFile("The C Programming Language", "Brian W.", "Kernighan"),
			new EncryptedFile("The Design and Analysis of Spatial Data Structures", "Hanan", "Samet"),
			new EncryptedFile("Object-Oriented Programming", "Brad", "Cox"),
			new EncryptedFile("Python Essential Reference", "David M.", "Beazley"),
			new EncryptedFile("The Practical SQL Handbook", "Judith S.", "Bowman"),
			new EncryptedFile("The Design Patterns Smalltalk Companion", "Sherman R.", "Alpert"),
			new EncryptedFile("Design Patterns", "Erich", "Gamma"),
			new EncryptedFile("Gig", "John", "Bowe"),
			new EncryptedFile("You Can't Be Too Careful", "David Pryce", "Jones"),
			new EncryptedFile("Go for Beginners", "Kaoru", "Iwamoto"),
			new EncryptedFile("How to Read a EncryptedFile", "Mortimer J.", "Adler"),
			new EncryptedFile("The Message", "Eugene H.", "Peterson"),
			new EncryptedFile("Beyond Bumper Sticker Ethics", "Steve", "Wilkens"),
			new EncryptedFile("Life Together", "Dietrich", "Bonhoeffer"),
			new EncryptedFile("Java 2 Exam Cram", "William", "Brogden")
		};
		
		for (int i = 0; i < files.length; i++) {
			newFiles.add(files[i]);
			
		}
		return newFiles;
	}
	/*
	 * @see DroppedElement#accept(ModelVisitorI, Object)
	 */
	public void accept(IDroppedElementVisitor visitor, Object passAlongArgument) {
		visitor.visitFile(this, passAlongArgument);
	}

}
