package com.example.Uber.Backend.service.Impl;

import com.example.Uber.Backend.Model.Customer;
import com.example.Uber.Backend.Model.Driver;
import com.example.Uber.Backend.Model.TripBooking;
import com.example.Uber.Backend.Model.TripStatus;
import com.example.Uber.Backend.Reposetory.CustomerReposetory;
import com.example.Uber.Backend.Reposetory.DriverReposetory;
import com.example.Uber.Backend.Reposetory.TripBookingReposetory;
import com.example.Uber.Backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService{
    @Autowired
    CustomerReposetory customerRepository2;

    @Autowired
    DriverReposetory driverRepository2;

    @Autowired
    TripBookingReposetory tripBookingRepository2;

    @Override
    public void register(Customer customer) {
        //Save the customer in database
        customerRepository2.save(customer);
    }

    @Override
    public void deleteCustomer(Integer customerId) {
        // Delete customer without using deleteById function
        Customer customer = customerRepository2.findById(customerId).get();
        customerRepository2.delete(customer);
    }

    @Override
    public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception {
        //Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
        //Avoid using SQL query
        TripBooking tripBooking = new TripBooking();
        Driver driver = null;
        List<Driver> allDriver = driverRepository2.findAll();
        for (Driver drv : allDriver) {
            if (drv.getCab().getAvailable()) {
                if (driver == null || driver.getDriverId() > drv.getDriverId()) {
                    driver = drv;
                }
            }
        }
        if (driver == null) {
            throw new Exception("No cab available!");
        }
        Customer customer = customerRepository2.findById(customerId).get();
        tripBooking.setDistanceInKm(distanceInKm);
        tripBooking.setFromLocation(fromLocation);
        tripBooking.setToLocation(toLocation);
        tripBooking.setCustomer(customer);
        tripBooking.setDriver(driver);
        driver.getCab().setAvailable(false);
        tripBooking.setStatus(TripStatus.CONFIRMED);
        tripBooking.setBill(driver.getCab().getPerKmRate() * distanceInKm);

        customer.getTripBookingList().add(tripBooking);
        driver.getTripBookingList().add(tripBooking);

        driverRepository2.save(driver);
        customerRepository2.save(customer);
        return tripBooking;

    }

    @Override
    public void cancelTrip(Integer tripId) {
        //Cancel the trip having given trip Id and update TripBooking attributes accordingly
        TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
        tripBooking.setStatus(TripStatus.CANCELED);
        tripBooking.setBill(0);

        Driver driver = tripBooking.getDriver();
        driver.getCab().setAvailable(true);
        driverRepository2.save(driver);
        tripBookingRepository2.save(tripBooking);
    }

    @Override
    public void completeTrip(Integer tripId) {
        //Complete the trip having given trip Id and update TripBooking attributes accordingly
        TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
        tripBooking.setStatus(TripStatus.COMPLETED);
        Driver driver = tripBooking.getDriver();
        driver.getCab().setAvailable(true);
        driverRepository2.save(driver);
        tripBookingRepository2.save(tripBooking);
    }
}