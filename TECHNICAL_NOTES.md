# Technical Implementation Notes

This document captures key design decisions and implementation details for the Global Clearing FIX Demo.

## Process Separation Strategy

The goal was to separate the Acceptor and Initiator into distinct runnable processes without splitting the code into multiple Maven modules.

### Spring Profiles
- We use `@Profile("acceptor")` and `@Profile("initiator")` to toggle components.
- Each process has its own entry point:
    - `ClearingHouseApp.java`: Forces `acceptor` profile.
    - `MemberFirmApp.java`: Forces `initiator` profile.
- This allows for a shared codebase while maintaining runtime isolation.

### Maven Configuration
- The `spring-boot-maven-plugin` is configured to use a custom property `${start-class}`.
- This allows developers to specify which app to run via command line: `-Dstart-class=...`.

## Database & Persistence

### Shared H2 Instance
- To simulate a shared ledger, both processes point to the same file-based H2 database.
- **Concurrent Access:** Enabled via the `;AUTO_SERVER=TRUE` JDBC parameter. This allows one process to act as the primary database server while the other connects as a client.
- **Location:** The database files are stored in `target/` to ensure they are cleaned during `mvn clean`.

## FIX Protocol Details

### Data Dictionary (`FIX44.xml`)
- A custom data dictionary was implemented to support specific `AllocationInstruction` (35=J) and `AllocationReport` (35=AS) fields.
- **Customization:** Tag 58 (Text) was explicitly added to the `AllocationReport` message definition to prevent session-level rejections during message crack.

### Message Cracking
- Both applications extend `MessageCracker` to handle specific incoming message types.
- The `MemberInitiator` saves incoming reports into a `FixReport` table, which is then served to the UI via the `MemberFirmController`.

## Web Implementation

- **Thymeleaf:** Used for server-side rendering.
- **Bootstrap 5:** Provides the premium dark-mode aesthetic.
- **Path Mapping:** To avoid conflicts during integrated testing, the Member Firm UI is mapped to `/member`, while the Clearing Ledger remains at `/`.
