# Scout24-car-market 

## Summary
This project a simple Restful implementation using scala, slick and akka-http. 

## Build

Go to project's home directory in which `build.sbt` exists. And execute the following command:

```bash
sbt universal:packageBin
```

This will create a zip file named `car-market-<version-number>.zip`. Extract this zip file and you will see that it has the following structure:
`bin/car-market` is the executable file of this project.

```text
├── bin
│   ├── car-market
│   └── car-market.bat
├── conf
│   ├── application.conf
│   └── logback.xml
├── lib
│   ├── ch.qos.logback.logback-classic-1.1.7.jar
│   ├── ch.qos.logback.logback-core-1.1.7.jar
│   └── ...
└── readme.md
```

## Configuration

Configure the `conf/application.conf` file. It has the following content by default. Change them according to your own settings.
 
```hocon
//slick configurations
database = {
  url = "jdbc:mysql://127.0.0.1:3306/car_market"
  driver = com.mysql.jdbc.Driver
  user = "root"
  password = "12345"
  connectionPool = disabled
  keepAliveConnection = true
}

//web-service configurations
car-market{
  ws-host: 127.0.0.1
  ws-port: 8080
}
```

|field| type| notes|
|---|---|---|
|database.url|String| Mysql db url for slick to establish connection|
|database.drive|String| Mysql driver for slick to establish connection|
|database.user|String| User name for mysql db to access|
|database.password|String| User's password for mysql db to access|
|database.connectionPool|String| No need to change|
|database.keepAliveConnection|Boolean| No need to change|
|car-market.ws-host|String| Ip address for webserver to run on|
|car-market.ws-port|Int| Port number for web server to run on|

## Run
- Before running the web-server, make sure that required database and tables are created. Use (create_db.sql)[create_db.sql] file to easily initialize database and tables. 

- Execute the following command on Unix environment:

```bash
# in case of permission needed
chmod +x bin/car-market  

# starts the web server
bin/car-market
```

- Execute the following command on Windows environment:

```bash
bin/car-market.bat
```

## Web Interfaces

In this project there are 5 services available to manipulate the `cars` table. A car object is as following

```scala
case class Car(id: Option[Long], title: String, fuel: String, price: Long, isNew: Boolean, mileage: Option[Long], firstReg: Option[java.sql.Date])
```
_Valid Car:_ New cars shouldn't have mileage and firstRef fields. On the other hand, a used car must have mileage and firstReg.
 
### 1. GET /cars?sort={sort_type}

Returns a list of all cars. Takes a sort type as optional. If it does not exist, or invalid, the default will be taken as `car.id` 

*Sample Response*

```json
[
  {
    "price": 40000,
    "fuel": "diesel",
    "id": 3,
    "isNew": true,
    "title": "AUDI R8"
  },
  {
    "price": 40000,
    "fuel": "gasoline",
    "id": 7,
    "isNew": true,
    "title": "Ford Mustang"
  },
   {
      "firstReg": "2008-01-01",
      "mileage": 150000,
      "price": 28000,
      "fuel": "diesel",
      "id": 5,
      "isNew": false,
      "title": "BMW X3"
    }
]
```
- This route can return previous json with 200 code or 500 with an error message.

### 2. POST /cars

Takes a valid car object in its body, and inserts it into database.

*Sample request body*
```json
  {
    "price": 40000,
    "fuel": "gasoline",
    "isNew": true,
    "title": "Ford Mustang"
  }
```

*Sample Responses*
- If car is invalid returns 400 with a message.
- If car is valid, and insert operation is successful returns 200 without body.

### 3. GET /cars/{id}

Returns the car with given id as json.

*Sample Responses*

- If internal error occurs, returns 500 with the exception message
- If the id does not exist, returns 404 with a message
- If the id exists in the database,a valid car json with 200 code, 
```json
{
  "price": 40000,
  "fuel": "gasoline",
  "id": 11,
  "isNew": true,
  "title": "Ford Mustang"
}
```

### 4. DELETE /cars/{id}

Deletes the car with given id

*Sample Responses*
- If operation is successful, returns 200 without any content
- If operation is failure, returns 500 with the error message

### 5. PUT /cars/{id}

Updates the given id car with the new valid car in the body

*Sample Request*
Needs a valid car json in its body:

```json
{
  "price": 24000,
  "fuel": "diesel",
  "isNew": true,
  "title": "Camaro"
}
```
*Sample Response*  
- If the car is not valid, returns 400 with a message,
- If the operation fails, returns 500 with an error message,
- If the operation successful, returns 200 without any content

## Dependencies

- "ch.qos.logback" % "logback-classic" % "1.1.7"
- "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
- "com.typesafe" % "config" % "1.3.0"
- "com.typesafe.slick" %% "slick" % "3.0.0",
- "mysql" % "mysql-connector-java" % "5.1.39"
- "com.typesafe.akka" %% "akka-http" % "10.0.6",
- "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.3",
- "com.typesafe.akka" %% "akka-http-testkit" % "10.0.6" % Test

## Additional Notes
- There are 2 tables in database: fuels and cars. And car's fuel type is a foreign key to fuels table.
  Therefore, while inserting a car into `cars` table, if the fuel type does not exist in the `fuels` table, the insert or update operation fails. 





