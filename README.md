# E-LIBRARY SERVER MODULE

### SETTING UP
Before running the project you need to create a database. Run the .ddl file that is located in project root directory.

After creating the database you need to create a new config file in the src/main/resources directory named
external_config.yml with template below:

```yaml
server:
    port: [SERVER_PORT]
spring:
    datasource:
        password: [DB_CONNECTION_PASSWORD]
        username: [DB_CONNECTION_USERNAME]
        url: jdbc:mysql://localhost:3306/[DB_SCHEMA_NAME]?characterEncoding=utf8&&useSSL=false
        driver-class-name: com.mysql.jdbc.Driver

