# Getting Started

### How to start this service
1. After you git clone this project from : 

https://github.com/stevenmtl/island_campsite_reservation.git

2. you can run BookingApiApplication from Intellij IDEA

3. Then go to src\doc folder to try different post requests.

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


