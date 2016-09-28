package seedu.addressbook.commands;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList.PersonNotFoundException;
import seedu.addressbook.data.tag.Tag;
import seedu.addressbook.data.tag.UniqueTagList.TagNotFoundException;

public class RemoveTag extends Command {
	/**
	 *Remove all the tag with a particular tagname
	 */

	public static final String COMMAND_WORD = "removetag";

	public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
			+ "Remove all the tag identified by the tag name\n\t"
			+ "Parameters: TAG_NAME\n\t" + "Example: " + COMMAND_WORD + " Friends";

	public static final String MESSAGE_REMOVE_TAG_SUCCESS = "Removed Tag: %1$s";
	public static final String MESSAGE_TAG_NOT_FOUND = "Tag not found. %1$s does not exist.";
	public static final String MESAGE_FAILED_INVALID_TAG_NAME = "Tag name is invalid. %1$s";

	private final String tagName;

	public RemoveTag(String tagName) {
		this.tagName = tagName;
	}

	@Override
	public CommandResult execute() {
		try {
			Tag removedTag = new Tag(tagName);
			addressBook.removeAllTag(removedTag);
			addressBook.removeTag(removedTag);
			return new CommandResult(String.format(MESSAGE_REMOVE_TAG_SUCCESS, removedTag));
		} catch (TagNotFoundException tnfe) {
			return new CommandResult(String.format(MESSAGE_TAG_NOT_FOUND, tagName));
		} catch (IllegalValueException ive) {
			return new CommandResult(String.format(MESAGE_FAILED_INVALID_TAG_NAME, Tag.MESSAGE_TAG_CONSTRAINTS));
		}
	}
}
