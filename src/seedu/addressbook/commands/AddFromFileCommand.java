package seedu.addressbook.commands;

import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.*;
import seedu.addressbook.data.person.UniquePersonList.DuplicatePersonException;
import seedu.addressbook.data.person.UniquePersonList.PersonNotFoundException;
import seedu.addressbook.data.tag.Tag;
import seedu.addressbook.data.tag.UniqueTagList;
import seedu.addressbook.parser.Parser;

import static seedu.addressbook.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * Adds a person to the address book.
 */
public class AddFromFileCommand extends Command {

    public static final String COMMAND_WORD = "addfile";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Adds the given text file of people into the address book. \n\t"
            + "Each line in the file should follow the same syntax as the add command:\n"
            + AddCommand.MESSAGE_USAGE;
    
    public static final String MESSAGE_SUCCESS = "File loaded: %1$s\nPeople loaded:\n%2$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "There exists duplicate people in the file.";
    public static final String MESSAGE_FILE_NOT_FOUND = "File not found.";
    public static final String MESSAGE_CANNOT_READ_FILE = "Unable to load file.";

    private final String fileName;
    private ArrayList<Person> toAdd = new ArrayList<Person>();

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException
     *             if any of the raw values are invalid
     */
    public AddFromFileCommand(String fileName) throws IllegalValueException {

        this.fileName = fileName;
    }

    @Override
    public CommandResult execute() {
        try {
            loadPeopleFromFile();
        } catch (FileNotFoundException ex) {
            return new CommandResult(MESSAGE_FILE_NOT_FOUND);
        } catch (IllegalValueException e) {
            return new CommandResult(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddFromFileCommand.MESSAGE_USAGE));
        } catch (IOException e) {
            return new CommandResult(MESSAGE_FILE_NOT_FOUND);
        }
        try {
            String peopleString = addPeople();
            return new CommandResult(String.format(MESSAGE_SUCCESS, fileName, peopleString));
        } catch (UniquePersonList.DuplicatePersonException dpe) {
            rollbackAddressbook();
            return new CommandResult(MESSAGE_DUPLICATE_PERSON);
        }
    }
    

    /**
     * Loads a list of add commands from the specified file.
     */
    private void loadPeopleFromFile() throws FileNotFoundException, IOException, IllegalValueException {
        toAdd.clear();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line = br.readLine();
        while (line != null) {
            final Matcher matcher = Parser.PERSON_DATA_ARGS_FORMAT.matcher(line);
            if (!matcher.matches()) {
                br.close();
                throw new IllegalValueException(line);
            }
            Set<String> tags = Parser.getTagsFromArgs(matcher.group("tagArguments"));

            final Set<Tag> tagSet = new HashSet<>();
            for (String tagName : tags) {
                tagSet.add(new Tag(tagName));
            }

            this.toAdd.add(new Person(new Name(matcher.group("name")),
                    new Phone(matcher.group("phone"),
                            Parser.isPrivatePrefixPresent(matcher.group("isPhonePrivate"))),
                    new Email(matcher.group("email"),
                            Parser.isPrivatePrefixPresent(matcher.group("isEmailPrivate"))),
                    new Address(matcher.group("address"),
                            Parser.isPrivatePrefixPresent(matcher.group("isAddressPrivate"))),
                    new UniqueTagList(tagSet)));

            line = br.readLine();
        }
        br.close();
    }
    
    /**
     * Adds the list of people (toAdd) to the addressbook
     */
    private String addPeople() throws DuplicatePersonException {
        StringBuilder peopleString = new StringBuilder();
        for (Person person : toAdd) {
            peopleString.append(person.toString() + "\n");
            addressBook.addPerson(person);
        }
        return peopleString.toString();
    }

    /**
     * Adding a person caused an error. Removes all people that have been added.
     */
    private void rollbackAddressbook() {
        for (ReadOnlyPerson person : toAdd) {
            if (addressBook.containsPerson(person)) {
                try {
                    addressBook.removePerson(person);
                } catch (PersonNotFoundException e) {
                    // Does not matter
                }
            }
        }
    }


}
