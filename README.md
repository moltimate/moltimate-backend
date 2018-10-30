# moltimate-backend
ProMol successor's backend application.

### Setup & Run

##### IntelliJ

To start the application from IntelliJ, import the project as a Maven project, and run the `main(...)` entrypoint in [`Application.java`](`src/main/java/com/moltimate/moltimatebackend/Application.java`).

##### Maven CLI

To start the application using Maven's CLI, run:

        mvn spring-boot:run



### Wiping Local Database

To wipe the database, delete the generated file `moltimate.mv.db` at the root of the project.
