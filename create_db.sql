# drop schema if exist
DROP SCHEMA IF EXISTS car_market;

# create and use car_market schema
CREATE SCHEMA car_market;
Use car_market;

# create fuels table
create table car_market.fuels
(
	name varchar(100) primary key
);

# create cars table
create table car_market.cars
(
	id int not null auto_increment primary key,
	title varchar(200) not null,
	fuel varchar(100) not null,
	price int not null,
	is_new boolean not null,
	mileage int,
	first_reg date,
	constraint cars_fuel_fk foreign key (fuel) references car_market.fuels (name)
);


# insert some fuels into fuels table
INSERT INTO car_market.fuels(name) VALUES("gasoline");
INSERT INTO car_market.fuels(name) VALUES("diesel");

# insert some new cars into cars table
INSERT INTO car_market.cars(title, fuel, price, is_new) VALUES("AUDI A4","gasoline",10000,true);
INSERT INTO car_market.cars(title, fuel, price, is_new) VALUES("AUDI A5","gasoline",15000,true);
INSERT INTO car_market.cars(title, fuel, price, is_new) VALUES("AUDI R8","diesel",40000,true);

# insert some used cars into cars table
INSERT INTO car_market.cars(title, fuel, price, is_new, mileage, first_reg) VALUES("BMW Z4","diesel",8000,false, 100000, "2010-01-01");
INSERT INTO car_market.cars(title, fuel, price, is_new, mileage, first_reg) VALUES("BMW X3","diesel",28000,false, 150000, "2008-01-01");
INSERT INTO car_market.cars(title, fuel, price, is_new, mileage, first_reg) VALUES("BMW 520","diesel",22000,false, 80000, "2013-01-01");
