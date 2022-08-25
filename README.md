# Getting Started

### How to start this service
1. After you git clone this project from : 

https://github.com/stevenmtl/island_campsite_reservation.git

2. you can run BookingApiApplication from Intellij IDEA

  2.1 run it from Intellij IDE
   
  2.2 go to windows command terminal, then go to the project folder:

   ./gradlew bootRun

  2.3 or after running gradle build and run below jar file

   (assume your project is under D:\bookingAPI\)

   java -jar D:\bookingAPI\build\libs\bookingAPI-0.0.1-SNAPSHOT.jar

3. Then go to src\doc folder to try different post requests as below:
   bookCancellation.http
   
   bookModification.http
   
   bookReservation.http
   
   bookVerySlowReservation.http --> this is to demo how concurrent (Optimistic Locking)
   
   getFreeCampsites.http
   
   initInventory.http

### How to run Post requests against their endpoints:

1. How to reserve campsites:

POST http://localhost:9090/bookReservation
Content-Type: application/json

{
"startDate":"2022-08-27",
"endDate":"2022-08-30",
"email":"steven.mtl@gmail.com",
"name":"Steven Li",
"numOfGuests": 30
}

2. for other requests, pls go to src/doc folder to see all *.http files


### Reference for RestAPI design: Campsite booking

* 1. RestController:
   BookingController
   
* 2. Service:
   BookingService
   MaintenanceService
   
* 3. Components:
   BookingReservation
   BookingCancellation
   BookingModification
     
* 4. Repository:
     InventoryRepository
     ReservationRespository
     
* 5. Entity:
     InventoryEntity
     ReservationEntity


