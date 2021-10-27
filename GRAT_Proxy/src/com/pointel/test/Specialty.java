package com.pointel.test;

public class Specialty {
	 private String primarySpecialtyIndicator;
	 private String specialtyTypeCode;
	 private String specialtyFullDescription;


	 // Getter Methods 

	 public String getPrimarySpecialtyIndicator() {
	  return primarySpecialtyIndicator;
	 }

	 public String getSpecialtyTypeCode() {
	  return specialtyTypeCode;
	 }

	 public String getSpecialtyFullDescription() {
	  return specialtyFullDescription;
	 }

	 // Setter Methods 

	 public void setPrimarySpecialtyIndicator(String primarySpecialtyIndicator) {
	  this.primarySpecialtyIndicator = primarySpecialtyIndicator;
	 }

	 public void setSpecialtyTypeCode(String specialtyTypeCode) {
	  this.specialtyTypeCode = specialtyTypeCode;
	 }

	 public void setSpecialtyFullDescription(String specialtyFullDescription) {
	  this.specialtyFullDescription = specialtyFullDescription;
	 }
	}