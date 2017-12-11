package com.example.enduser.lostpets;

/**
 * Created by ZenithPC on 11/12/2017.
 */

public class Pet {
    private String name;
    private String weight;
    private String gender;
    private String zipCode;
    private String breed;
    private String description;
    private String microchip;
    private String urlOne;
    private String urlTwo;
    private String urlThree;



    public Pet(String name, String weight, String gender, String zipCode, String breed){
        this.name = name;
        this.weight = weight;
        this.gender = gender;
        this.zipCode = zipCode;
        this.breed = breed;
    }

    public Pet(String name, String weight, String gender, String zipCode, String breed, String microchip, String description){
        this.name = name;
        this.weight = weight;
        this.gender = gender;
        this.zipCode = zipCode;
        this.breed = breed;
        this.description = description;
        this.microchip = microchip;
    }

    public Pet(String name, String weight, String gender, String zipCode, String breed, String microchip, String description, String url){
        this.name = name;
        this.weight = weight;
        this.gender = gender;
        this.zipCode = zipCode;
        this.breed = breed;
        this.description = description;
        this.microchip = microchip;
        urlOne = url;
    }
    public Pet(String name, String weight, String gender, String zipCode, String breed, String microchip, String description, String url, String url2, String url3){
        this.name = name;
        this.weight = weight;
        this.gender = gender;
        this.zipCode = zipCode;
        this.breed = breed;
        this.description = description;
        this.microchip = microchip;
        urlOne = url;
        urlTwo = url2;
        urlThree = url3;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setUrlOne(String urlOne){this.urlOne = urlOne;}
    public void setDescription(String description){this.description = description;}
    public void setMicrochip(String microchip){this.microchip = microchip;}
    public  void setWeight(String weight){
        this.weight = weight;
    }
    public void setGender(String gender){
        this.gender = gender;
    }
    public void setZipCode(String zip){
        zipCode = zip;
    }
    public void setBreed(String breed){
        this.breed = breed;
    }
    public void setUrlTwo(String urlTwo){this.urlTwo = urlTwo;}
    public void setUrlThree(String urlThree){this.urlThree = urlThree;}

    public String getName(){return name;}
    public String getWeight(){return weight;}
    public String getGender(){return gender;}
    public String getZipCode(){return zipCode;}
    public String getBreed(){return breed;}
    public String getDescription(){return description;}
    public String getMicrochip(){return microchip;}
    public String getUrlOne(){return urlOne;}
    public String getUrlTwo(){return urlTwo;}
    public String getUrlThree(){return urlThree;}






}
