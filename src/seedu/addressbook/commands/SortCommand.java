package seedu.addressbook.commands;

public class SortCommand extends Command{

	public static final String COMMAND_WORD = "sort";
	private static final String MESSAGE_EMPTY_ADDRESSBOOK = "Address book is empty!";
	private static final String MESSAGE_SUCCESSFUL_SORT = "Successfully sorted!";
	public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Sorts address book.\n\t"
            + "Example: " + COMMAND_WORD;
	
	public SortCommand() {}
	
	
	@Override
	public CommandResult execute() {
		addressBook.sort();
		return new CommandResult(MESSAGE_SUCCESSFUL_SORT);
	}

}
