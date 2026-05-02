package org.example;

public class Address {
        private String streetAddress;
        private String city;
        private String state;
        private String zipCode;
        private String country;

        public Address(String street, String city, String country, String zipCode) {
                this.streetAddress = street;
                this.city = city;
                this.country = country;
                this.zipCode = zipCode;
        }

        public String getStreetAddress() {
                return streetAddress;
        }

        public void setStreetAddress(String streetAddress) {
                this.streetAddress = streetAddress;
        }

        public String getCity() {
                return city;
        }

        public void setCity(String city) {
                this.city = city;
        }

        public String getState() {
                return state;
        }

        public void setState(String state) {
                this.state = state;
        }

        public String getZipCode() {
                return zipCode;
        }

        public void setZipCode(String zipCode) {
                this.zipCode = zipCode;
        }

        public String getCountry() {
                return country;
        }

        public void setCountry(String country) {
                this.country = country;
        }
}
