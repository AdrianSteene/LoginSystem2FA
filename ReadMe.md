# MedRec

## Description

This project is a secure system developed using Java21 and Springboot to handle HTTPS connections with TLS to both client and database, which is a MySQL instance. The system is designed to reduce human error through its simplified syntax and automatic memory management. The system uses Springboot modules to set up and handle HTTPS with TLS connections, input validation, authentication, authorization, logging, and other security functions.

## Database

The database is composed of 4 tables: User, MedicalRecord, Division, and Login.

- User: userId (int), username (string), passwordhash (string), salt (string), role (Enum: patient, nurse, doctor, govorg), divisionId (int) (nullable)
- MedicalRecord: recordId (int), patientId (int), nurseId (int), doctorId (int), divisionId (int), note (string)
- Division: divisionId (int), divisionName (string)
- Login: loginId (int), userId (int), loginToken (string), created (datetime), expired (boolean)

## Login Procedure

The login procedure involves a username, password, a second factor of authentication, and a final access token in the form of JWT. The procedure includes several steps involving the client sending an HTTP post request with their username and password via HTTPS to the server, the server retrieving the necessary salt from the database, and more.

## Installation

### Install Maven
For Linux: 
```     
sudo apt update
sudo apt install maven
```

For Mac: 
```
brew update
brew install maven
```

### Install Java 21
For Linux: 
``` 
sudo apt install openjdk-21-jdk
```
For Mac: 
```
brew install openjdk@21
```

### Install JKS Keystore and Add certificate 




## Usage

## Contributing

## License

## Contact

