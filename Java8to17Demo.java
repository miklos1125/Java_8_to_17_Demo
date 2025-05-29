/* 
Demonstration of language features introduced between
Java 8 – which is an old friend by now – marked a turning point in modern Java development.
- Lambdas and functional programming constructs  **not added yet**
- Streams for powerful collection processing  **not added yet**
- Optional for safer null handling
- Default and static methods in interfaces  **not added yet**
and Java 17, which is the current industry-standard LTS (Long-Term Support) version:
- var: local variable type inference (Java 10)
- switch with arrow syntax (->) and yield (Java 14)
- record: simple immutable data classes (Java 14)
- text blocks: multiline string literals using """ (Java 15)
- sealed classes: controlled inheritance (Java 17)
- instanceof pattern matching (Java 16)
- Files.writeString(): simplified file writing (Java 11)
import java.util.Scanner;
*/
import java.time.LocalDate;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class Java8to17Demo {

    public static void main(String args[]) {

        System.out.println("Greetings!");
        Scanner sc = new Scanner(System.in);
        var input = sc.nextLine();

        // Modern switch expression with yield (Java 14+)  *****
        // Determine nationality based on greeting, using enhanced switch expression
        String nationality = switch (input)
        {
            case "Hello!" -> "english";
            case "Hola!" -> "spanish";
            case "Hi!" -> "american";
            case "Szia!" ->{System.out.println("Szevasz! ;)"); yield "hungarian";}
            case "Halo!" -> "german";
            case "Salut!" -> "french";
            case "Ciao!" -> "italian";
            default -> "unknown";
        };
        System.out.println("What's your name?");
        var input2 = sc.nextLine();

        // Wrap the name input in an Optional to avoid null-related issues
        Optional<String> name = input2.trim().length() > 0
                ? Optional.of(input2)
                : Optional.empty();
        
        // Validate that birth year input is numeric using try-catch inside a loop
        boolean b = true;
        int birth = -1;
        do
        {
            System.out.println("Year of your birth?");
            try
            {
                birth = Integer.parseInt(sc.nextLine());
                b = false;
            } catch (NumberFormatException e)
            {
                System.out.println("That's not a number!");
            }
        } while (b);

        // Calculate age if input is valid
        int preAge = LocalDate.now().getYear() - birth;
        Optional<Integer> age = preAge > 0
                ? Optional.of(LocalDate.now().getYear() - birth)
                : Optional.empty();

        String finalName = name.orElse("unknown");
        int finalAge = age.orElse(-1);
        
        PersonData person = new PersonData(finalName, finalAge, nationality);
        
        // Use sealed class hierarchy to distinguish between adults and children
        Human human;
        if (finalAge >= 18) {
            human = new Adult(person);
        } else {
            human = new Child();
        }
        
        // If person is adult, print info and write their data as JSON using Files.writeString
        // also using "instanceof pattern matching"
        if(human instanceof Adult adult){
            System.out.println("Basic personal information:");
            System.out.println(adult.getPerson());
            try {
                Files.writeString(Path.of("person.json"), person.getJsonFormat());
                System.out.println("JSON saved to person.json");
            } catch (IOException e) {
                System.out.println("Could not write file: " + e.getMessage());
            }
        } else {
            System.out.println("Age restriction.");
        }
    }
}

// record is an immutable data holder, with auto-generated constructor, toString, and more
record PersonData(String name, int age, String nationality){
    public boolean isAdult() {
        return age >= 18;
    }
    // Custom toString to make output more expressive
    @Override
        public String toString() {
        return String.format("Name: %s (%s) - %s %s", name.toUpperCase(),
        age>=0? String.valueOf(age): "unknown age" , isAdult() ? "adult":"", nationality);
    }
    
    //A method presenting Text blocks (String with """ """)
    public String getJsonFormat() {
        return String.format("""
            {
                "name": "%s",
                "age": %d,
                "nationality": "%s",
                "adult": %b
            }
            """, name, age, nationality, isAdult());
    }
}

sealed class Human permits Child, Adult{}

final class Child extends Human {}

non-sealed class Adult extends Human {

    private PersonData person;

    Adult(PersonData person) {
        this.person = person;
    }

    public PersonData getPerson() {
        return person;
    }
}
